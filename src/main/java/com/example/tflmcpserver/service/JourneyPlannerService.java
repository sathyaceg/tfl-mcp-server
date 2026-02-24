package com.example.tflmcpserver.service;

import com.example.tflmcpserver.client.TflJourneyClient;
import com.example.tflmcpserver.model.JourneyOptionSummary;
import com.example.tflmcpserver.model.JourneyPlanRequest;
import com.example.tflmcpserver.model.JourneyPlanToolResponse;
import com.example.tflmcpserver.model.JourneyPlannerErrorCode;
import com.example.tflmcpserver.model.tfl.TflItineraryResult;
import com.example.tflmcpserver.model.tfl.TflJourney;
import io.github.resilience4j.ratelimiter.RateLimiter;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeoutException;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class JourneyPlannerService {

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
			return error(JourneyPlannerErrorCode.RATE_LIMIT_EXCEEDED, RATE_LIMIT_EXCEEDED_MESSAGE);
		}
		try {
			TflItineraryResult itineraryResult = tflJourneyClient.journeyResults(request);
			return JourneyPlanToolResponse.success(topFiveFastestJourneys(itineraryResult));
		} catch (RuntimeException ex) {
			return toErrorResponse(ex);
		}
	}

	private List<JourneyOptionSummary> topFiveFastestJourneys(TflItineraryResult itineraryResult) {
		if (itineraryResult == null || itineraryResult.getJourneys() == null) {
			return List.of();
		}
		return itineraryResult.getJourneys().stream()
				.sorted(Comparator.comparingInt(TflJourney::getDuration)
						.thenComparing(TflJourney::getArrivalDateTime, Comparator.nullsLast(String::compareTo))
						.thenComparing(TflJourney::getStartDateTime, Comparator.nullsLast(String::compareTo)))
				.limit(5).map(this::toSummary).toList();
	}

	private JourneyOptionSummary toSummary(TflJourney journey) {
		int legCount = journey.getLegs() == null ? 0 : journey.getLegs().size();
		return new JourneyOptionSummary(journey.getDuration(), journey.getStartDateTime(), journey.getArrivalDateTime(),
				legCount);
	}

	private JourneyPlanToolResponse toErrorResponse(RuntimeException ex) {
		if (ex instanceof IllegalArgumentException) {
			return error(JourneyPlannerErrorCode.VALIDATION_ERROR, ex.getMessage());
		}
		if (ex instanceof WebClientResponseException responseException) {
			return error(JourneyPlannerErrorCode.UPSTREAM_ERROR,
					"TfL API returned status " + responseException.getStatusCode().value() + ".");
		}
		if (ex instanceof WebClientRequestException requestException) {
			return hasTimeoutCause(requestException)
					? error(JourneyPlannerErrorCode.UPSTREAM_TIMEOUT, TIMEOUT_MESSAGE)
					: error(JourneyPlannerErrorCode.UPSTREAM_ERROR, UPSTREAM_REQUEST_FAILED_MESSAGE);
		}
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
