package com.ryanair.interconnectingflights;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class InterconnectingFlightsApplication {

    public static void main(String[] args) {
        SpringApplication.run(InterconnectingFlightsApplication.class, args);
    }

}
