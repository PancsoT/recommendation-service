package com.pt.recommendation_service.exception;

/**
 * Exception thrown when a date string cannot be parsed due to an invalid format.
 * <p>
 * Typically used to indicate that a user-provided date does not match the expected format (e.g., yyyy-MM-dd).
 * </p>
 */
public class InvalidDateFormatException extends RuntimeException {

    /**
     * Constructs a new InvalidDateFormatException with the specified detail message.
     *
     * @param message the detail message explaining the cause of the exception
     */
    public InvalidDateFormatException(String message) {
        super(message);
    }
}