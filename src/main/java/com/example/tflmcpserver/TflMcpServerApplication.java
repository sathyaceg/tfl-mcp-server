package com.example.tflmcpserver;

import com.example.tflmcpserver.config.TflApiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(TflApiProperties.class)
public class TflMcpServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(TflMcpServerApplication.class, args);
    }
}
