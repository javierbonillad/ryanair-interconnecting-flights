package com.ryanair.interconnectingflights.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class FlightLeg {

    @JsonProperty("departureAirport")
    private String departureAirport;

    @JsonProperty("arrivalAirport")
    private String arrivalAirport;

    @JsonProperty("departureDateTime")
    private LocalDateTime departureDateTime;

    @JsonProperty("arrivalDateTime")
    private LocalDateTime arrivalDateTime;

}
