package com.pt.recommendation_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Entity representing a cryptocurrency price record.
 * <p>
 * Stores the price of a specific cryptocurrency at a given date and time.
 * </p>
 */
@Entity
@Data
public class Price {

    /**
     * The unique identifier of the price record (primary key).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The date and time when the price was recorded.
     */
    private LocalDateTime dateTime;

    /**
     * The symbol of the cryptocurrency (e.g., BTC, ETH).
     */
    private String symbol;

    /**
     * The price value of the cryptocurrency at the given date and time.
     */
    private Double price;
}