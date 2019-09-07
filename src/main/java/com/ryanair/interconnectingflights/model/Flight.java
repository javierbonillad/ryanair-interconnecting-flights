package com.ryanair.interconnectingflights.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

@Getter
@Setter
public class Flight {

    private String number;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalTime departureTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalTime arrivalTime;

}
