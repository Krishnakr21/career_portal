
package com.roadmap.roadmap_service.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController

public class HealthCheck {
    @GetMapping("/health")
    public String healthCheck() {
        return "Roadmap Service is running";
    }
}
