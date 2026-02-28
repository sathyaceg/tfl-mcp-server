package com.example.tflmcpserver;

import com.example.tflmcpserver.model.JourneyPlannerRateLimiterProperties;
import com.example.tflmcpserver.model.McpTransportAuthProperties;
import com.example.tflmcpserver.model.StationToiletRateLimiterProperties;
import com.example.tflmcpserver.model.TflStationDataProperties;
import com.example.tflmcpserver.model.TflApiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({TflApiProperties.class, McpTransportAuthProperties.class,
		JourneyPlannerRateLimiterProperties.class, TflStationDataProperties.class,
		StationToiletRateLimiterProperties.class})
public class TflMcpServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(TflMcpServerApplication.class, args);
	}
}
