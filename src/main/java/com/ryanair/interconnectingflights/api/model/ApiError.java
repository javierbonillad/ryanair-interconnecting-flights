package com.ryanair.interconnectingflights.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiError {

    @JsonProperty("type")
    private ErrorType type;

    @JsonProperty("message")
    private String message;

    public enum ErrorType {
        INVALID_PARAMS, NOT_FOUND_ROUTE
    }
}
