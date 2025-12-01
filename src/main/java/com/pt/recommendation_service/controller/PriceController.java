package com.pt.recommendation_service.controller;

import com.pt.recommendation_service.dto.CryptoNormalizedRangeDto;
import com.pt.recommendation_service.repository.PriceRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class PriceController {

    private final PriceRepository priceRepository;

    public PriceController(PriceRepository priceRepository) {
        this.priceRepository = priceRepository;
    }

    @GetMapping("/normalized-range")
    public List<CryptoNormalizedRangeDto> getNormalizedRanges() {
        return priceRepository.findNormalizedRangesDesc();
    }
}