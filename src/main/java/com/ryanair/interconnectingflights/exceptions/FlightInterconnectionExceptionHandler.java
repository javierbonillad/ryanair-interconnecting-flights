package com.ryanair.interconnectingflights.exceptions;

import com.ryanair.interconnectingflights.api.model.ApiError;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class FlightInterconnectionExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InvalidParamsException.class)
    public ResponseEntity<Object> handleInvalidParamsError(RuntimeException ex, WebRequest webRequest) {

        return genericErrorHandling(ex, webRequest, HttpStatus.BAD_REQUEST,
                ApiError.ErrorType.INVALID_PARAMS, "Invalid params, change them in order to run the request again");
    }

    @ExceptionHandler(NotAvailableRoute.class)
    public ResponseEntity<Object> handleNotAvailableRouteError(RuntimeException ex, WebRequest webRequest) {
        return genericErrorHandling(ex, webRequest, HttpStatus.NOT_FOUND,
                ApiError.ErrorType.NOT_FOUND_ROUTE, "There are not available flights for the requested route");
    }

    private ResponseEntity<Object> genericErrorHandling(RuntimeException ex, WebRequest webRequest, HttpStatus errorCode,
                                                        ApiError.ErrorType errorType, String errorMessage) {
        ApiError apiError = new ApiError();
        apiError.setType(errorType);
        apiError.setMessage(errorMessage);

        return handleExceptionInternal(ex, apiError, new HttpHeaders(), errorCode, webRequest);
    }

}
