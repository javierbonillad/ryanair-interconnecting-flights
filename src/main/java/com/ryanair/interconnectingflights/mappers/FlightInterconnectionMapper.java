package com.ryanair.interconnectingflights.mappers;

import com.ryanair.interconnectingflights.api.model.FlightInterconnection;
import com.ryanair.interconnectingflights.api.model.FlightLeg;

import java.util.List;

public class FlightInterconnectionMapper {

    public static FlightInterconnection createInterconnectionFromLegs(List<FlightLeg> legs) {
        FlightInterconnection interconnection = new FlightInterconnection();
        interconnection.setStops(legs.size() - 1);
        interconnection.setLegs(legs);

        return interconnection;
    }

}
