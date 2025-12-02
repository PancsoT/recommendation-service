package com.pt.recommendation_service.enums;

/**
 * Enum representing the supported cryptocurrency symbols in the application.
 * <p>
 * Only these symbols are considered valid for statistics and price queries.
 * </p>
 */
public enum SupportedCryptos {
    /** Bitcoin */
    BTC,
    /** Dogecoin */
    DOGE,
    /** Ethereum */
    ETH,
    /** Litecoin */
    LTC,
    /** Ripple */
    XRP
}