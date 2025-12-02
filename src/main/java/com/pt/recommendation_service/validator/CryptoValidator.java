package com.pt.recommendation_service.validator;

import com.pt.recommendation_service.enums.SupportedCryptos;
import com.pt.recommendation_service.exception.UnsupportedCryptoException;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class CryptoValidator {
    public SupportedCryptos validateSymbol(String symbol) {
        return Arrays.stream(SupportedCryptos.values())
                .filter(s -> s.name().equalsIgnoreCase(symbol))
                .findFirst()
                .orElseThrow(() -> new UnsupportedCryptoException(symbol));
    }

    public boolean isSymbolValid(String symbol) {
        return Arrays.stream(SupportedCryptos.values())
                .anyMatch(s -> s.name().equalsIgnoreCase(symbol));
    }
}
