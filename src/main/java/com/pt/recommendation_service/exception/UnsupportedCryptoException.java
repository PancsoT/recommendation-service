package com.pt.recommendation_service.exception;

public class UnsupportedCryptoException extends RuntimeException {
    public UnsupportedCryptoException(String cryptoSymbol) {
        super("Crypto is not supported: " + cryptoSymbol);
    }
}