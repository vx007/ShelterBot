package com.skypro.shelterbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(scanBasePackages = {"com.skypro.shelterbot"})
@EnableCaching
public class  ShelterBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShelterBotApplication.class, args);
    }
}
