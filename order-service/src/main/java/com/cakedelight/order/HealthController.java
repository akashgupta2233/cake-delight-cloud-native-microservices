package com.cakedelight.order;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Health check endpoint for monitoring and orchestration.
 */
@RestController
public class HealthController {

    @GetMapping("/health")
    public String health() {
        return "UP";
    }
}
