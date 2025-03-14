package ru.practicum.ewm.event;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@EnableFeignClients(basePackages = "ru.practicum.ewm.common.feignclient")
@SpringBootApplication
@ComponentScan(value = {"ru.practicum.ewm", "ru.practicum.stats.client"})
public class Event {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(Event.class, args);
    }
}