package com.ryanair.interconnectingflights.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class FlightInterconnection {

    @JsonProperty("stops")
    private Integer stops;

    @JsonProperty("legs")
    private List<FlightLeg> legs;
}
