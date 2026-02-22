package com.example.tflmcpserver;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TflMcpServerController {
    @GetMapping("/")
    public String hello() {
        return "tfl-mcp-server is running";
    }
}
