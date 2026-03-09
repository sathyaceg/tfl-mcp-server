package com.example.tflmcpserver.service;

import com.example.tflmcpserver.client.TflJourneyClient;
import com.example.tflmcpserver.model.JourneyPlannerErrorCode;
import com.example.tflmcpserver.model.api.journey.JourneyDisambiguationSuggestion;
import com.example.tflmcpserver.model.api.journey.JourneyOptionDetail;
import com.example.tflmcpserver.model.api.journey.JourneyPlanRequest;
import com.example.tflmcpserver.model.api.journey.JourneyPlanResponse;
import com.example.tflmcpserver.model.tfl.journey.TflItineraryResultWire;
import com.example.tflmcpserver.mapper.JourneyResponseMapper;
import io.github.resilience4j.ratelimiter.RateLimiter;
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
	private final JourneyResponseMapper journeyResponseMapper;
	private final RateLimiter journeyPlannerRateLimiter;

	public JourneyPlannerService(TflJourneyClient tflJourneyClient, JourneyResponseMapper journeyResponseMapper,
			RateLimiter journeyPlannerRateLimiter) {
		this.tflJourneyClient = tflJourneyClient;
		this.journeyResponseMapper = journeyResponseMapper;
		this.journeyPlannerRateLimiter = journeyPlannerRateLimiter;
	}

	public JourneyPlanResponse planJourney(JourneyPlanRequest request) {
		if (!journeyPlannerRateLimiter.acquirePermission()) {
			logger.warn("Journey planning denied due to rate limiting");
			return error(JourneyPlannerErrorCode.RATE_LIMIT_EXCEEDED, RATE_LIMIT_EXCEEDED_MESSAGE);
		}
		try {
			TflItineraryResultWire itineraryResult = tflJourneyClient.journeyResults(request);
			List<JourneyDisambiguationSuggestion> fromLocationDisambiguation = journeyResponseMapper
					.toDisambiguationSuggestions(itineraryResult.getFromLocationDisambiguation() == null
							? null
							: itineraryResult.getFromLocationDisambiguation().getDisambiguationOptions());
			List<JourneyDisambiguationSuggestion> toLocationDisambiguation = journeyResponseMapper
					.toDisambiguationSuggestions(itineraryResult.getToLocationDisambiguation() == null
							? null
							: itineraryResult.getToLocationDisambiguation().getDisambiguationOptions());
			if (!fromLocationDisambiguation.isEmpty() || !toLocationDisambiguation.isEmpty()) {
				logger.info("Journey planning requires disambiguation (fromOptions={}, toOptions={})",
						fromLocationDisambiguation.size(), toLocationDisambiguation.size());
				return JourneyPlanResponse.disambiguationRequired(fromLocationDisambiguation, toLocationDisambiguation);
			}
			List<JourneyOptionDetail> topJourneyDetails = journeyResponseMapper.toTopJourneyDetails(itineraryResult, 5);
			logger.info("Journey planning succeeded with {} option(s)", topJourneyDetails.size());
			return JourneyPlanResponse.success(topJourneyDetails);
		} catch (RuntimeException ex) {
			return toErrorResponse(ex);
		}
	}

	private JourneyPlanResponse toErrorResponse(RuntimeException ex) {
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

	private JourneyPlanResponse error(JourneyPlannerErrorCode code, String message) {
		return JourneyPlanResponse.error(code.name(), message);
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
