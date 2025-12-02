package com.pt.recommendation_service.exception;

/**
 * Exception thrown when no price data is found for a given date.
 * <p>
 * Typically used to indicate that there are no price records available for the requested date.
 * </p>
 */
public class NoPriceFoundForDateException extends RuntimeException {

    /**
     * Constructs a new NoPriceFoundForDateException with a message indicating the missing date.
     *
     * @param date the date for which no price data was found
     */
    public NoPriceFoundForDateException(String date) {
        super("No price data found for date: " + date);
    }
}