package com.siddhi.smartparking.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardWebSocketController {

    @GetMapping("/ws-test")
    public String test() {
        return "WebSocket Controller Working";
    }
}