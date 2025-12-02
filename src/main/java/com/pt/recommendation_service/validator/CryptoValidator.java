package com.pt.recommendation_service.validator;

import com.pt.recommendation_service.enums.SupportedCryptos;
import com.pt.recommendation_service.exception.UnsupportedCryptoException;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Validator component for checking and validating supported cryptocurrency symbols.
 * <p>
 * Provides methods to validate if a symbol is supported and to retrieve the corresponding enum value.
 * </p>
 */
@Component
public class CryptoValidator {

    /**
     * Validates the given cryptocurrency symbol and returns the corresponding {@link SupportedCryptos} enum value.
     * <p>
     * The comparison is case-insensitive. If the symbol is not supported, an {@link UnsupportedCryptoException} is thrown.
     * </p>
     *
     * @param symbol the cryptocurrency symbol to validate
     * @return the corresponding {@link SupportedCryptos} enum value
     * @throws UnsupportedCryptoException if the symbol is not supported
     */
    public SupportedCryptos validateSymbol(String symbol) {
        return Arrays.stream(SupportedCryptos.values())
                .filter(s -> s.name().equalsIgnoreCase(symbol))
                .findFirst()
                .orElseThrow(() -> new UnsupportedCryptoException(symbol));
    }

    /**
     * Checks if the given cryptocurrency symbol is supported.
     * <p>
     * The comparison is case-insensitive.
     * </p>
     *
     * @param symbol the cryptocurrency symbol to check
     * @return {@code true} if the symbol is supported, {@code false} otherwise
     */
    public boolean isSymbolValid(String symbol) {
        return Arrays.stream(SupportedCryptos.values())
                .anyMatch(s -> s.name().equalsIgnoreCase(symbol));
    }
}