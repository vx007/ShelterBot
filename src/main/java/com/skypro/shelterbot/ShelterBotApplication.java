package com.skypro.shelterbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShelterBotApplication {

    public static void main(String[] args) {
        SpringApplication.run (ShelterBotApplication.class,  args);
        System.out.println("Hello world!");
    }

}
