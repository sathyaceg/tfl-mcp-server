package com.example.tflmcpserver;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class HelloControllerTest {

    @Test
    void helloReturnsExpectedMessage() {
        HelloController controller = new HelloController();

        assertEquals("tfl-mcp-server is running", controller.hello());
    }
}
