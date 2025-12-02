package com.pt.recommendation_service.exception;

public class NoPriceFoundForDateException extends RuntimeException {
    public NoPriceFoundForDateException(String date) {
        super("No price data found for date: " + date);
    }
}