package com.example.tflmcpserver.config;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.tflmcpserver.model.JourneyPlannerRateLimiterProperties;
import io.github.resilience4j.ratelimiter.RateLimiter;
import java.time.Duration;
import org.junit.jupiter.api.Test;

class JourneyPlannerRateLimiterConfigTest {

	@Test
	void createsRateLimiterWithConfiguredLimits() {
		JourneyPlannerRateLimiterProperties properties = new JourneyPlannerRateLimiterProperties(3,
				Duration.ofSeconds(5), Duration.ofMillis(250));
		JourneyPlannerRateLimiterConfig config = new JourneyPlannerRateLimiterConfig();

		RateLimiter rateLimiter = config.journeyPlannerRateLimiter(properties);

		assertEquals(3, rateLimiter.getRateLimiterConfig().getLimitForPeriod());
		assertEquals(Duration.ofSeconds(5), rateLimiter.getRateLimiterConfig().getLimitRefreshPeriod());
		assertEquals(Duration.ofMillis(250), rateLimiter.getRateLimiterConfig().getTimeoutDuration());
	}
}
