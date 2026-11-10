package com.qred.payment.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.qred.payment.helpers.CurrentDateHelper;
import io.swagger.v3.oas.annotations.media.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

import lombok.RequiredArgsConstructor;

@ControllerAdvice
@RequiredArgsConstructor
public final class GlobalExceptionHandler {

    @Schema(description = "ErrorResponse")
    public record ErrorResponse(
            @Schema(example = "<error message>")
            @JsonProperty("message")
            String message,
            @JsonProperty("timestamp")
            Instant timestamp
    ) {

    }

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final CurrentDateHelper currentDateHelper;

    private ResponseEntity<ErrorResponse> toErrorResponse(final String message,
                                                          final HttpStatus httpStatus) {
        final ErrorResponse response = new ErrorResponse(
                message,
                currentDateHelper.getInstantNow()
        );

        return new ResponseEntity<>(response, httpStatus);
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> handleThrowable(final Throwable e) {
        log.warn("handleThrowable", e);

        return toErrorResponse(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.warn("handleMethodArgumentNotValidException", e);

        return toErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(final HttpMessageNotReadableException e) {
        log.warn("handleHttpMessageNotReadableException", e);

        return toErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(final DomainException e) {
        log.warn("handleDomainException", e);

        return toErrorResponse(e.getErrorCode(), HttpStatus.BAD_REQUEST);
    }
}
