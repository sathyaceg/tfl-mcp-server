package com.example.tflmcpserver.config;

import com.example.tflmcpserver.model.JourneyPlannerRateLimiterProperties;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JourneyPlannerRateLimiterConfig {

	@Bean
	public RateLimiter journeyPlannerRateLimiter(JourneyPlannerRateLimiterProperties properties) {
		RateLimiterConfig config = RateLimiterConfig.custom().limitForPeriod(properties.limitForPeriod())
				.limitRefreshPeriod(properties.limitRefreshPeriod()).timeoutDuration(properties.timeoutDuration())
				.build();

		return RateLimiter.of("journeyPlannerRateLimiter", config);
	}
}
