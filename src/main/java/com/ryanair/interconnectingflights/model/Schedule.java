package com.ryanair.interconnectingflights.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Schedule {

    private Integer month;

    @JsonFormat
    private List<DaySchedule> days;

}
