package com.ryanair.interconnectingflights.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Route {

    private String airportFrom;

    private String airportTo;

    private String connectingAirport;

    private Boolean newRoute;

    private Boolean seasonalRoute;

    private String operator;

    private String group;

}
