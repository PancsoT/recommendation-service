package com.pt.recommendation_service.exception;

/**
 * Exception thrown when an unsupported cryptocurrency symbol is requested.
 * <p>
 * Typically used to indicate that the provided symbol is not among the application's supported cryptocurrencies.
 * </p>
 */
public class UnsupportedCryptoException extends RuntimeException {

    /**
     * Constructs a new UnsupportedCryptoException with a message indicating the unsupported symbol.
     *
     * @param cryptoSymbol the cryptocurrency symbol that is not supported
     */
    public UnsupportedCryptoException(String cryptoSymbol) {
        super("Crypto is not supported: " + cryptoSymbol);
    }
}