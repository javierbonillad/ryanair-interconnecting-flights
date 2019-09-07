package com.ryanair.interconnectingflights.controllers;

import com.ryanair.interconnectingflights.api.InterconnectionsApi;
import com.ryanair.interconnectingflights.api.model.FlightInterconnection;
import com.ryanair.interconnectingflights.services.FlightInterconnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class FlightInterconnectionController implements InterconnectionsApi {

    @Autowired
    private FlightInterconnectionService flightInterconnectionService;

    @Override
    public ResponseEntity<List<FlightInterconnection>> interconnections(@NotNull @Valid String departure,
                                                                        @NotNull @Valid @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureDateTime,
                                                                        @NotNull @Valid String arrival,
                                                                        @NotNull @Valid @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime arrivalDateTime) {
        return ResponseEntity.ok(flightInterconnectionService.getInterconnectingFlights(departure, arrival, departureDateTime, arrivalDateTime));
    }

}
