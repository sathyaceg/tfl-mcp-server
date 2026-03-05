package com.example.tflmcpserver.service;

import com.example.tflmcpserver.client.TflJourneyClient;
import com.example.tflmcpserver.model.JourneyDisambiguationSuggestion;
import com.example.tflmcpserver.model.JourneyLegDetail;
import com.example.tflmcpserver.model.JourneyOptionDetail;
import com.example.tflmcpserver.model.JourneyPlanRequest;
import com.example.tflmcpserver.model.JourneyPlanToolResponse;
import com.example.tflmcpserver.model.JourneyPlannerErrorCode;
import com.example.tflmcpserver.model.tfl.TflDisambiguationOption;
import com.example.tflmcpserver.model.tfl.TflDisruption;
import com.example.tflmcpserver.model.tfl.TflIdentifier;
import com.example.tflmcpserver.model.tfl.TflItineraryResult;
import com.example.tflmcpserver.model.tfl.TflJourney;
import com.example.tflmcpserver.model.tfl.TflJourneyLeg;
import com.example.tflmcpserver.model.tfl.TflPlannedWork;
import com.example.tflmcpserver.model.tfl.TflRouteOption;
import io.github.resilience4j.ratelimiter.RateLimiter;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class JourneyPlannerService {

	private static final Logger logger = LoggerFactory.getLogger(JourneyPlannerService.class);
	private static final String TIMEOUT_MESSAGE = "Timed out while calling TfL API.";
	private static final String UPSTREAM_REQUEST_FAILED_MESSAGE = "TfL API request failed.";
	private static final String INTERNAL_ERROR_MESSAGE = "Unexpected error while planning journey.";
	private static final String RATE_LIMIT_EXCEEDED_MESSAGE = "Rate limit exceeded for planJourney.";

	private final TflJourneyClient tflJourneyClient;
	private final RateLimiter journeyPlannerRateLimiter;

	public JourneyPlannerService(TflJourneyClient tflJourneyClient, RateLimiter journeyPlannerRateLimiter) {
		this.tflJourneyClient = tflJourneyClient;
		this.journeyPlannerRateLimiter = journeyPlannerRateLimiter;
	}

	public JourneyPlanToolResponse planJourney(JourneyPlanRequest request) {
		if (!journeyPlannerRateLimiter.acquirePermission()) {
			logger.warn("Journey planning denied due to rate limiting");
			return error(JourneyPlannerErrorCode.RATE_LIMIT_EXCEEDED, RATE_LIMIT_EXCEEDED_MESSAGE);
		}
		try {
			TflItineraryResult itineraryResult = tflJourneyClient.journeyResults(request);
			List<JourneyDisambiguationSuggestion> fromLocationDisambiguation = buildSuggestions(
					itineraryResult.getFromLocationDisambiguation() == null
							? null
							: itineraryResult.getFromLocationDisambiguation().getDisambiguationOptions());
			List<JourneyDisambiguationSuggestion> toLocationDisambiguation = buildSuggestions(
					itineraryResult.getToLocationDisambiguation() == null
							? null
							: itineraryResult.getToLocationDisambiguation().getDisambiguationOptions());
			if (!fromLocationDisambiguation.isEmpty() || !toLocationDisambiguation.isEmpty()) {
				logger.info("Journey planning requires disambiguation (fromOptions={}, toOptions={})",
						fromLocationDisambiguation.size(), toLocationDisambiguation.size());
				return JourneyPlanToolResponse.disambiguationRequired(fromLocationDisambiguation,
						toLocationDisambiguation);
			}
			List<JourneyOptionDetail> topJourneyDetails = topFiveFastestJourneys(itineraryResult);
			logger.info("Journey planning succeeded with {} option(s)", topJourneyDetails.size());
			return JourneyPlanToolResponse.success(topJourneyDetails);
		} catch (RuntimeException ex) {
			return toErrorResponse(ex);
		}
	}

	private List<JourneyDisambiguationSuggestion> buildSuggestions(List<TflDisambiguationOption> options) {
		if (options == null) {
			return List.of();
		}
		return options.stream()
				.filter(option -> option.getParameterValue() != null && !option.getParameterValue().isBlank())
				.sorted(Comparator.comparing(TflDisambiguationOption::getMatchQuality,
						Comparator.nullsLast(Comparator.reverseOrder())))
				.map(option -> new JourneyDisambiguationSuggestion(option.getParameterValue(),
						option.getMatchQuality()))
				.limit(5).toList();
	}

	private List<JourneyOptionDetail> topFiveFastestJourneys(TflItineraryResult itineraryResult) {
		if (itineraryResult == null || itineraryResult.getJourneys() == null) {
			return List.of();
		}
		return itineraryResult.getJourneys().stream()
				.sorted(Comparator.comparingInt(TflJourney::getDuration)
						.thenComparing(TflJourney::getArrivalDateTime, Comparator.nullsLast(String::compareTo))
						.thenComparing(TflJourney::getStartDateTime, Comparator.nullsLast(String::compareTo)))
				.limit(5).map(this::toDetail).toList();
	}

	private JourneyOptionDetail toDetail(TflJourney journey) {
		List<JourneyLegDetail> legDetails = journey.getLegs() == null
				? List.of()
				: journey.getLegs().stream().map(this::toLegDetail).toList();
		int legCount = legDetails.size();
		Integer totalFarePence = journey.getFare() == null ? null : journey.getFare().getTotalCost();
		return new JourneyOptionDetail(journey.getDuration(), journey.getStartDateTime(), journey.getArrivalDateTime(),
				legCount, journey.getDescription(), journey.isAlternativeRoute(), totalFarePence, legDetails);
	}

	private JourneyLegDetail toLegDetail(TflJourneyLeg leg) {
		String instructionSummary = leg.getInstruction() == null ? null : leg.getInstruction().getSummary();
		String instructionDetailed = leg.getInstruction() == null ? null : leg.getInstruction().getDetailed();
		String modeId = leg.getMode() == null ? null : leg.getMode().getId();
		String modeName = leg.getMode() == null ? null : leg.getMode().getName();

		String routeName = null;
		String direction = null;
		if (leg.getRouteOptions() != null && !leg.getRouteOptions().isEmpty()) {
			TflRouteOption primaryRouteOption = leg.getRouteOptions().get(0);
			routeName = primaryRouteOption.getLineIdentifier() != null
					&& primaryRouteOption.getLineIdentifier().getName() != null
							? primaryRouteOption.getLineIdentifier().getName()
							: primaryRouteOption.getName();
			direction = primaryRouteOption.getDirection();
		}

		List<String> disruptionSummaries = leg.getDisruptions() == null
				? List.of()
				: leg.getDisruptions().stream().map(this::disruptionSummary)
						.filter(text -> text != null && !text.isBlank()).toList();

		List<String> plannedWorkDescriptions = leg.getPlannedWorks() == null
				? List.of()
				: leg.getPlannedWorks().stream().map(TflPlannedWork::getDescription)
						.filter(text -> text != null && !text.isBlank()).toList();

		List<String> stopPoints = (leg.getPath() == null || leg.getPath().getStopPoints() == null)
				? List.of()
				: leg.getPath().getStopPoints().stream()
						.map(stopPoint -> stopPoint.getName() != null && !stopPoint.getName().isBlank()
								? stopPoint.getName()
								: stopPoint.getId())
						.filter(text -> text != null && !text.isBlank()).toList();

		return new JourneyLegDetail(leg.getDuration(), leg.getDepartureTime(), leg.getArrivalTime(), leg.getDistance(),
				instructionSummary, instructionDetailed, modeId, modeName, routeName, direction,
				leg.getInterChangeDuration(), leg.getInterChangePosition(), leg.isDisrupted(), disruptionSummaries,
				plannedWorkDescriptions, stopPoints);
	}

	private String disruptionSummary(TflDisruption disruption) {
		if (disruption == null) {
			return null;
		}
		if (disruption.getSummary() != null && !disruption.getSummary().isBlank()) {
			return disruption.getSummary();
		}
		return disruption.getDescription();
	}

	private JourneyPlanToolResponse toErrorResponse(RuntimeException ex) {
		if (ex instanceof IllegalArgumentException) {
			logger.warn("Journey planning validation failed: {}", ex.getMessage());
			return error(JourneyPlannerErrorCode.VALIDATION_ERROR, ex.getMessage());
		}
		if (ex instanceof WebClientResponseException responseException) {
			logger.warn("TfL API returned non-success status: {}", responseException.getStatusCode().value());
			return error(JourneyPlannerErrorCode.UPSTREAM_ERROR,
					"TfL API returned status " + responseException.getStatusCode().value() + ".");
		}
		if (ex instanceof WebClientRequestException requestException) {
			logger.warn("TfL API request failed: {}", requestException.getMessage());
			return hasTimeoutCause(requestException)
					? error(JourneyPlannerErrorCode.UPSTREAM_TIMEOUT, TIMEOUT_MESSAGE)
					: error(JourneyPlannerErrorCode.UPSTREAM_ERROR, UPSTREAM_REQUEST_FAILED_MESSAGE);
		}
		logger.error("Unexpected journey planning failure", ex);
		return hasTimeoutCause(ex)
				? error(JourneyPlannerErrorCode.UPSTREAM_TIMEOUT, TIMEOUT_MESSAGE)
				: error(JourneyPlannerErrorCode.INTERNAL_ERROR, INTERNAL_ERROR_MESSAGE);
	}

	private JourneyPlanToolResponse error(JourneyPlannerErrorCode code, String message) {
		return JourneyPlanToolResponse.error(code.name(), message);
	}

	private boolean hasTimeoutCause(Throwable throwable) {
		Throwable current = throwable;
		while (current != null) {
			if (current instanceof TimeoutException) {
				return true;
			}
			current = current.getCause();
		}
		return false;
	}
}
