package com.pt.recommendation_service.service;

import com.pt.recommendation_service.entity.Price;
import com.pt.recommendation_service.repository.PriceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CsvLoaderServiceTest {

    private PriceRepository priceRepository;
    private PathMatchingResourcePatternResolver resolver;
    private CsvLoaderService csvLoaderService;

    @BeforeEach
    void setUp() {
        priceRepository = mock(PriceRepository.class);
        resolver = mock(PathMatchingResourcePatternResolver.class);
        csvLoaderService = new CsvLoaderService(priceRepository, resolver);
    }

    @Test
    void run_readsCsvAndSavesPrices() throws Exception {
        // Prepare a fake CSV file as a Resource
        String csvContent = "timestamp,symbol,price\n" +
                "1640995200000,BTC,42000.0\n" +
                "1640998800000,ETH,3200.0\n";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
        Resource resource = mock(Resource.class);
        when(resource.getInputStream()).thenReturn(inputStream);

        // Mock the resolver to return our fake resource
        when(resolver.getResources("classpath:csv/*.csv")).thenReturn(new Resource[]{resource});

        // Run the loader
        csvLoaderService.run(mock(ApplicationArguments.class));

        // Capture the saved Price entities
        ArgumentCaptor<Price> captor = ArgumentCaptor.forClass(Price.class);
        verify(priceRepository, times(2)).save(captor.capture());
        List<Price> savedPrices = captor.getAllValues();

        // Assert the first record
        Price first = savedPrices.get(0);
        assertEquals("BTC", first.getSymbol());
        assertEquals(42000.0, first.getPrice());
        assertEquals(LocalDateTime.of(2022, 1, 1, 0, 0), first.getDateTime());

        // Assert the second record
        Price second = savedPrices.get(1);
        assertEquals("ETH", second.getSymbol());
        assertEquals(3200.0, second.getPrice());
        assertEquals(LocalDateTime.of(2022, 1, 1, 1, 0), second.getDateTime());
    }
}