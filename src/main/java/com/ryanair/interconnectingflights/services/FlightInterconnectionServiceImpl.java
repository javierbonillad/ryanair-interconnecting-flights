package com.ryanair.interconnectingflights.services;

import com.ryanair.interconnectingflights.api.model.FlightInterconnection;
import com.ryanair.interconnectingflights.api.model.FlightLeg;
import com.ryanair.interconnectingflights.clients.RyanairClient;
import com.ryanair.interconnectingflights.exceptions.InvalidParamsException;
import com.ryanair.interconnectingflights.exceptions.NotAvailableRoute;
import com.ryanair.interconnectingflights.mappers.FlightInterconnectionMapper;
import com.ryanair.interconnectingflights.mappers.FlightLegMapper;
import com.ryanair.interconnectingflights.model.DaySchedule;
import com.ryanair.interconnectingflights.model.Flight;
import com.ryanair.interconnectingflights.model.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class FlightInterconnectionServiceImpl implements FlightInterconnectionService {

    private static final String RYANAIR_OPERATOR = "RYANAIR";

    private static final Integer HOURS_DIFFERENCE_BETWEEN_STOPS = 2;

    private static final Integer MAX_STOPS = 1;

    @Autowired
    private RyanairClient ryanairClient;

    public List<FlightInterconnection> getInterconnectingFlights(String departureCode, String arrivalCode,
                                                                 LocalDateTime departureDateTime,
                                                                 LocalDateTime arrivalDateTime) {

        validateParams(departureCode, arrivalCode, departureDateTime, arrivalDateTime);

        List<String> interconnectionStops = getInterconnectionStops(departureCode, arrivalCode);

        if (!isAvailableDirectRoute(departureCode, arrivalCode) && CollectionUtils.isEmpty(interconnectionStops)) {
            throw new NotAvailableRoute();
        }

        return getAllFlights(interconnectionStops, departureCode, arrivalCode, departureDateTime, arrivalDateTime);
    }

    private List<FlightInterconnection> getAllFlights(List<String> interconnectionStops, String departureCode,
                                                      String arrivalCode, LocalDateTime departureDateTime,
                                                      LocalDateTime arrivalDateTime) {
        List<FlightInterconnection> allFlights = new ArrayList<>();

        List<FlightLeg> directFlights =
                getFlights(departureCode, arrivalCode, departureDateTime, arrivalDateTime);

        allFlights.addAll(directFlights.stream()
                .map(flightLeg -> FlightInterconnectionMapper.createInterconnectionFromLegs(Arrays.asList(flightLeg))).collect(Collectors.toList()));

        if (!CollectionUtils.isEmpty(interconnectionStops)) {
            allFlights.addAll(interconnectionStops.parallelStream().flatMap(stopIata -> {

                List<FlightLeg> firstLegs = getFlights(departureCode, stopIata, departureDateTime, arrivalDateTime);
                List<FlightLeg> secondLegs = getFlights(stopIata, arrivalCode, departureDateTime, arrivalDateTime);

                return firstLegs.stream().flatMap(firstLeg -> secondLegs.stream()
                        .filter(secondLeg -> firstLeg.getArrivalDateTime().plusHours(HOURS_DIFFERENCE_BETWEEN_STOPS).compareTo(secondLeg.getDepartureDateTime()) <= 0)
                        .map(secondLeg -> FlightInterconnectionMapper.createInterconnectionFromLegs(Arrays.asList(firstLeg, secondLeg))));

            }).collect(Collectors.toList()));
        }

        return allFlights;
    }

    private List<FlightLeg> getFlights(String departureCode, String arrivalCode,
                                       LocalDateTime departureDateTime, LocalDateTime arrivalDateTime) {
        return IntStream.rangeClosed(departureDateTime.getYear(), arrivalDateTime.getYear()).mapToObj(year -> {
            int startMonth = year == departureDateTime.getYear() ? departureDateTime.getMonthValue() : 1;

            return IntStream.rangeClosed(startMonth, year == arrivalDateTime.getYear() ? arrivalDateTime.getMonthValue() : 12)
                    .parallel().mapToObj(month -> {

                        try {
                            Schedule schedules = ryanairClient.getScheduleByIatasAndDate(departureCode, arrivalCode, year, month);

                            if ((year == departureDateTime.getYear() && month == departureDateTime.getMonthValue())
                                    || (year == arrivalDateTime.getYear() && month == arrivalDateTime.getMonthValue())) {

                                return schedules.getDays().stream()
                                        .filter(daySchedule -> isValidFlightDay(year, month, daySchedule, departureDateTime, arrivalDateTime))
                                        .flatMap(daySchedule ->
                                                daySchedule.getFlights().stream()
                                                        .filter(flight -> isValidFlightDate(year, month,
                                                                daySchedule.getDay(), flight, departureDateTime, arrivalDateTime)).map(flight ->
                                                        FlightLegMapper.mapFlightLeg(departureCode, arrivalCode, year,
                                                                month, daySchedule.getDay(), flight.getDepartureTime(), year,
                                                                month, daySchedule.getDay(), flight.getArrivalTime())
                                                ));

                            } else {
                                return schedules.getDays().stream()
                                        .flatMap(daySchedule -> daySchedule.getFlights().stream().map(flight ->
                                                FlightLegMapper.mapFlightLeg(departureCode, arrivalCode, year,
                                                        month, daySchedule.getDay(), flight.getDepartureTime(), year,
                                                        month, daySchedule.getDay(), flight.getArrivalTime())
                                        ));
                            }
                        } catch (Exception e) {
                            return null;
                        }


                    }).flatMap(e -> e).filter(flightLeg -> flightLeg != null);
        }).flatMap(e -> e).collect(Collectors.toList());
    }

    private boolean isValidFlightDate(Integer year, Integer month, Integer day, Flight flight,
                                      LocalDateTime departureDateTime, LocalDateTime arrivalDateTime) {
        LocalDateTime departure = LocalDateTime.of(LocalDate.of(year, month, day), flight.getDepartureTime());
        LocalDateTime arrival = LocalDateTime.of(LocalDate.of(year, month, day), flight.getArrivalTime());

        return departure.compareTo(departureDateTime) >= 0
                && arrival.compareTo(arrivalDateTime) <= 0;
    }

    private boolean isValidFlightDay(Integer year, Integer month, DaySchedule daySchedule,
                                     LocalDateTime departureDateTime, LocalDateTime arrivalDateTime) {
        LocalDate localDate = LocalDate.of(year, month, daySchedule.getDay());

        return localDate.compareTo(departureDateTime.toLocalDate()) >= 0
                && localDate.compareTo(arrivalDateTime.toLocalDate()) <= 0;
    }

    private List<String> getInterconnectionStops(String departureCode, String arrivalCode) {
        List<String> connectingIatas = getAllConnectingIatas(departureCode, arrivalCode);

        return filterValidStops(connectingIatas);
    }

    private List<String> filterValidStops(List<String> connectingIatas) {
        Map<String, Integer> iatasFrequency = new HashMap<>();
        connectingIatas.forEach(iata -> iatasFrequency.compute(iata, (key, value) -> value == null ? 1 : value + 1));

        return iatasFrequency.entrySet().stream().filter(entry -> entry.getValue() > MAX_STOPS)
                .map(entry -> entry.getKey()).collect(Collectors.toList());
    }

    private List<String> getAllConnectingIatas(String departureCode, String arrivalCode) {
        return ryanairClient.getAvailableRoutes().stream()
                .filter(route -> route.getConnectingAirport() == null && RYANAIR_OPERATOR.equals(route.getOperator())
                        && (departureCode.equals(route.getAirportFrom()) || arrivalCode.equals(route.getAirportTo()))
                        && !(departureCode.equals(route.getAirportFrom()) && arrivalCode.equals(route.getAirportTo())))
                .map(route -> departureCode.equals(route.getAirportFrom()) ? route.getAirportTo() : route.getAirportFrom())
                .collect(Collectors.toList());
    }

    private boolean isAvailableDirectRoute(String departureCode, String arrivalCode) {
        return ryanairClient.getAvailableRoutes().stream()
                .filter(route -> route.getConnectingAirport() == null && RYANAIR_OPERATOR.equals(route.getOperator()))
                .anyMatch(route -> departureCode.equals(route.getAirportFrom()) && arrivalCode.equals(route.getAirportTo()));
    }

    private void validateParams(String departureCode, String arrivalCode, LocalDateTime departureDateTime,
                                LocalDateTime arrivalDateTime) {

        if (!isValidIataFormat(departureCode) || !isValidIataFormat(arrivalCode)
                || !isValidDate(departureDateTime, arrivalDateTime)) {
            throw new InvalidParamsException();
        }
    }

    private boolean isValidDate(LocalDateTime departureDateTime, LocalDateTime arrivalDateTime) {
        return departureDateTime.isBefore(arrivalDateTime) && departureDateTime.isAfter(LocalDateTime.now());
    }

    private boolean isValidIataFormat(String iataCode) {
        return iataCode.length() == 3 && iataCode.chars().allMatch(c -> Character.isUpperCase(c));
    }

}
