package com.example.tflmcpserver.service;

import com.example.tflmcpserver.client.TflStationDataClient;
import com.example.tflmcpserver.model.StationDisambiguationSuggestion;
import com.example.tflmcpserver.model.StationToiletDetail;
import com.example.tflmcpserver.model.StationToiletErrorCode;
import com.example.tflmcpserver.model.StationToiletLookupRequest;
import com.example.tflmcpserver.model.StationToiletToolResponse;
import io.github.resilience4j.ratelimiter.RateLimiter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class StationToiletService {

	private static final Logger logger = LoggerFactory.getLogger(StationToiletService.class);
	private static final String RATE_LIMIT_EXCEEDED_MESSAGE = "Rate limit exceeded for stationToilets.";
	private static final String INTERNAL_ERROR_MESSAGE = "Unexpected error while loading station toilet data.";
	private static final String DATA_NOT_AVAILABLE_MESSAGE = "Station data is not available.";
	private static final Pattern PLATFORM_PATTERN = Pattern.compile("(?i)platforms?\\s*([0-9,\\sand]+)");

	private final TflStationDataClient tflStationDataClient;
	private final RateLimiter stationToiletRateLimiter;

	public StationToiletService(TflStationDataClient tflStationDataClient,
			@Qualifier("stationToiletRateLimiter") RateLimiter stationToiletRateLimiter) {
		this.tflStationDataClient = tflStationDataClient;
		this.stationToiletRateLimiter = stationToiletRateLimiter;
	}

	public StationToiletToolResponse stationToilets(StationToiletLookupRequest request) {
		if (!stationToiletRateLimiter.acquirePermission()) {
			logger.warn("Station toilet lookup denied due to rate limiting");
			return error(StationToiletErrorCode.RATE_LIMIT_EXCEEDED, RATE_LIMIT_EXCEEDED_MESSAGE);
		}
		try {
			if (request == null || !StringUtils.hasText(request.stationName())) {
				return error(StationToiletErrorCode.VALIDATION_ERROR, "Field 'stationName' must be provided.");
			}

			List<TflStationDataClient.StationRecord> stations = tflStationDataClient
					.findStationsByName(request.stationName(), 5);
			if (stations.isEmpty()) {
				return error(StationToiletErrorCode.STATION_NOT_FOUND, "No matching station found.");
			}
			if (stations.size() > 1) {
				return StationToiletToolResponse.disambiguationRequired(stations.stream()
						.map(station -> new StationDisambiguationSuggestion(station.uniqueId(), station.name()))
						.toList());
			}

			TflStationDataClient.StationRecord station = stations.get(0);
			List<StationToiletDetail> toilets = tflStationDataClient
					.findToiletsByStationUniqueId(station.uniqueId(), 25).stream()
					.map(toilet -> new StationToiletDetail(toilet.location(), extractPlatforms(toilet.location()),
							toilet.accessible(), toilet.babyChanging(), toilet.insideGateLine(), toilet.feeCharged(),
							toilet.type()))
					.toList();
			return StationToiletToolResponse.success(station.name(), station.uniqueId(), toilets);
		} catch (IllegalStateException ex) {
			logger.error("Station data files unavailable", ex);
			return error(StationToiletErrorCode.DATA_NOT_AVAILABLE, DATA_NOT_AVAILABLE_MESSAGE);
		} catch (RuntimeException ex) {
			logger.error("Unexpected station toilet lookup failure", ex);
			return error(StationToiletErrorCode.INTERNAL_ERROR, INTERNAL_ERROR_MESSAGE);
		}
	}

	private List<Integer> extractPlatforms(String location) {
		if (!StringUtils.hasText(location)) {
			return List.of();
		}
		Matcher matcher = PLATFORM_PATTERN.matcher(location);
		if (!matcher.find()) {
			return List.of();
		}
		String group = matcher.group(1);
		Matcher numberMatcher = Pattern.compile("\\d+").matcher(group);
		List<Integer> numbers = new java.util.ArrayList<>();
		while (numberMatcher.find()) {
			numbers.add(Integer.parseInt(numberMatcher.group()));
		}
		return numbers.stream().distinct().sorted().toList();
	}

	private StationToiletToolResponse error(StationToiletErrorCode errorCode, String message) {
		return StationToiletToolResponse.error(errorCode.name(), message);
	}
}
