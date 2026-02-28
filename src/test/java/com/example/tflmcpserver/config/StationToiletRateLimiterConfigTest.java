package com.example.tflmcpserver.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.tflmcpserver.model.StationToiletRateLimiterProperties;
import io.github.resilience4j.ratelimiter.RateLimiter;
import java.time.Duration;
import org.junit.jupiter.api.Test;

class StationToiletRateLimiterConfigTest {

	@Test
	void createsRateLimiterWithConfiguredLimits() {
		StationToiletRateLimiterProperties properties = new StationToiletRateLimiterProperties(4, Duration.ofSeconds(2),
				Duration.ofMillis(100));
		StationToiletRateLimiterConfig config = new StationToiletRateLimiterConfig();

		RateLimiter rateLimiter = config.stationToiletRateLimiter(properties);

		assertEquals(4, rateLimiter.getRateLimiterConfig().getLimitForPeriod());
		assertEquals(Duration.ofSeconds(2), rateLimiter.getRateLimiterConfig().getLimitRefreshPeriod());
		assertEquals(Duration.ofMillis(100), rateLimiter.getRateLimiterConfig().getTimeoutDuration());
	}
}
