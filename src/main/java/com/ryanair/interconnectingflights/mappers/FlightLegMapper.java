package com.ryanair.interconnectingflights.mappers;

import com.ryanair.interconnectingflights.api.model.FlightLeg;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class FlightLegMapper {

    public static FlightLeg mapFlightLeg(String departureCode, String arrivalCode, Integer departureYear,
                                         Integer departureMonth, Integer departureDay, LocalTime departureTime,
                                         Integer arrivalYear, Integer arrivalMonth, Integer arrivalDay, LocalTime arrivalTime) {


        LocalDateTime departureDateTime = LocalDateTime.of(LocalDate.of(departureYear, departureMonth, departureDay), departureTime);
        LocalDateTime arrivalDateTime = LocalDateTime.of(LocalDate.of(arrivalYear, arrivalMonth, arrivalDay), arrivalTime);

        if (arrivalDateTime.compareTo(departureDateTime) < 0) {
            arrivalDateTime = arrivalDateTime.plusDays(1);
        }

        return createFlightLeg(departureCode, arrivalCode, departureDateTime, arrivalDateTime);
    }

    private static FlightLeg createFlightLeg(String departureCode, String arrivalCode, LocalDateTime departureDateTime, LocalDateTime arrivalDateTime) {
        return FlightLeg.builder().departureAirport(departureCode).arrivalAirport(arrivalCode)
                .departureDateTime(departureDateTime).arrivalDateTime(arrivalDateTime).build();
    }

}
