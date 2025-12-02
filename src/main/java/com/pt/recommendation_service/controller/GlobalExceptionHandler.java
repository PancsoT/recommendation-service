package com.pt.recommendation_service.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.pt.recommendation_service.exception.InvalidDateFormatException;
import com.pt.recommendation_service.exception.NoPriceFoundForDateException;
import com.pt.recommendation_service.exception.UnsupportedCryptoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler for REST controllers.
 * <p>
 * Handles custom application exceptions and returns appropriate HTTP responses with error messages.
 * Also logs each exception for monitoring and debugging purposes.
 * </p>
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handles {@link InvalidDateFormatException} thrown when a date string cannot be parsed.
     *
     * @param ex the exception
     * @return a {@link ResponseEntity} with HTTP 400 Bad Request and the error message
     */
    @ExceptionHandler(InvalidDateFormatException.class)
    public ResponseEntity<String> handleInvalidDateFormatException(InvalidDateFormatException ex) {
        logger.warn("InvalidDateFormatException: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    /**
     * Handles {@link UnsupportedCryptoException} thrown when an unsupported cryptocurrency symbol is requested.
     *
     * @param ex the exception
     * @return a {@link ResponseEntity} with HTTP 400 Bad Request and the error message
     */
    @ExceptionHandler(UnsupportedCryptoException.class)
    public ResponseEntity<String> handleUnsupportedCryptoException(UnsupportedCryptoException ex) {
        logger.warn("UnsupportedCryptoException: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    /**
     * Handles {@link NoPriceFoundForDateException} thrown when no price data is found for a given date.
     *
     * @param ex the exception
     * @return a {@link ResponseEntity} with HTTP 404 Not Found and the error message
     */
    @ExceptionHandler(NoPriceFoundForDateException.class)
    public ResponseEntity<String> handleNoPriceFoundForDateException(NoPriceFoundForDateException ex) {
        logger.warn("NoPriceFoundForDateException: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }
}