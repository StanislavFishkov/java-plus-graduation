package ru.practicum.ewm.participationrequest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableFeignClients(basePackages = "ru.practicum.ewm.common.feignclient")
@ComponentScan(value = {"ru.practicum.ewm", "ru.practicum.stats.client"})
@SpringBootApplication
public class ParticipationRequest {
    public static void main(String[] args) {
        SpringApplication.run(ParticipationRequest.class, args);
    }
}