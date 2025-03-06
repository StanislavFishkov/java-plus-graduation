package ru.practicum.ewm.location;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import ru.practicum.ewm.common.error.ErrorHandler;

@EnableFeignClients(basePackages = "ru.practicum.ewm.common.feignclient")
@Import({ErrorHandler.class})
@SpringBootApplication
public class Location {
    public static void main(String[] args) {
        SpringApplication.run(Location.class, args);
    }
}