package com.ryanair.interconnectingflights.services;

import com.ryanair.interconnectingflights.api.model.FlightInterconnection;

import java.time.LocalDateTime;
import java.util.List;

public interface FlightInterconnectionService {

    List<FlightInterconnection> getInterconnectingFlights(String departureCode, String arrivalCode,
                                                          LocalDateTime departureDateTime,
                                                          LocalDateTime arrivalDateTime);
}
