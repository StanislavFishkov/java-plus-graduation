package ru.yandex.practicum.infra.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {
    @GetMapping("main_service-failure")
    String mainServiceFallback() {
        return "Main service is temporarily unavailable.";
    }
}