package com.ryanair.interconnectingflights.api;

import com.ryanair.interconnectingflights.api.model.ApiError;
import com.ryanair.interconnectingflights.api.model.FlightInterconnection;
import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Api(value = "interconnections", description = "the interconnections API", tags = {"interconnections",})
public interface InterconnectionsApi {

    @ApiOperation(value = "Find flights interconnections", nickname = "interconnections", notes = "Returns direct and interconnected flights given a departure and arrival airport as well as departure and arrival date", tags = {"interconnections",}, response = FlightInterconnection.class, responseContainer = "List")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Successful operation", response = FlightInterconnection.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Invalid request params", response = ApiError.class, responseContainer = "List"),
            @ApiResponse(code = 404, message = "Interconnections not found")})
    @RequestMapping(value = "api/v1/interconnections",
            produces = {"application/json"},
            method = RequestMethod.GET)
    ResponseEntity<List<FlightInterconnection>> interconnections(@NotNull @ApiParam(value = "Departure airport IATA code", required = true) @Valid @RequestParam(value = "departure", required = true) String departure,
                                                                 @NotNull @ApiParam(value = "Departure datetime in the departure airport timezone in ISO format", required = true) @Valid @RequestParam(value = "departureDateTime", required = true) LocalDateTime departureDateTime,
                                                                 @NotNull @ApiParam(value = "Arrival airport IATA code", required = true) @Valid @RequestParam(value = "arrival", required = true) String arrival,
                                                                 @NotNull @ApiParam(value = "Arrival datetime in the arrival airport timezone in ISO format", required = true) @Valid @RequestParam(value = "arrivalDateTime", required = true) LocalDateTime arrivalDateTime);

}
