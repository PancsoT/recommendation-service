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

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidDateFormatException.class)
    public ResponseEntity<String> handleInvalidDateFormatException(InvalidDateFormatException ex) {
        logger.warn("InvalidDateFormatException: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(UnsupportedCryptoException.class)
    public ResponseEntity<String> handleUnsupportedCryptoException(UnsupportedCryptoException ex) {
        logger.warn("UnsupportedCryptoException: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ex.getMessage());
    }

    @ExceptionHandler(NoPriceFoundForDateException.class)
    public ResponseEntity<String> handleNoPriceFoundForDateException(NoPriceFoundForDateException ex) {
        logger.warn("NoPriceFoundForDateException: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ex.getMessage());
    }
}