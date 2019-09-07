package com.ryanair.interconnectingflights.clients;

import com.ryanair.interconnectingflights.model.Route;
import com.ryanair.interconnectingflights.model.Schedule;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient(name = "ryanair", url = "${ryanair.api.url}")
public interface RyanairClient {

    @Cacheable(value = "availableRoutes", unless = "#result==null||#result.isEmpty()")
    @RequestMapping(method = RequestMethod.GET, value = "/locate/3/routes", produces = MediaType.APPLICATION_JSON_VALUE)
    List<Route> getAvailableRoutes();

    @Cacheable(value = "scheduleByIataAndDate", unless = "#result==null")
    @RequestMapping(method = RequestMethod.GET, value = "/timtbl/3/schedules/{departure}/{arrival}/years/{year}/months/{month}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    Schedule getScheduleByIatasAndDate(@PathVariable("departure") String departureCode,
                                       @PathVariable("arrival") String arrivalCode, @PathVariable("year") Integer year,
                                       @PathVariable("month") Integer month);
}
