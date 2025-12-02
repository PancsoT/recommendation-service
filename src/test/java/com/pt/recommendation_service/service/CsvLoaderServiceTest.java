package com.pt.recommendation_service.service;

import com.pt.recommendation_service.entity.Price;
import com.pt.recommendation_service.repository.PriceRepository;
import com.pt.recommendation_service.validator.CryptoValidator;
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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CsvLoaderServiceTest {

    private PriceRepository priceRepository;
    private PathMatchingResourcePatternResolver resolver;
    private CryptoValidator cryptoValidator;
    private CsvLoaderService csvLoaderService;

    @BeforeEach
    void setUp() {
        priceRepository = mock(PriceRepository.class);
        resolver = mock(PathMatchingResourcePatternResolver.class);
        cryptoValidator = mock(CryptoValidator.class);
        csvLoaderService = new CsvLoaderService(priceRepository, resolver, cryptoValidator);
    }

    @Test
    void run_readsCsvAndSavesPrices() throws Exception {
        String csvContent = """
                timestamp,symbol,price
                1640995200000,BTC,42000.0
                1640998800000,ETH,3200.0
                """;
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
        Resource resource = mock(Resource.class);
        when(resource.getInputStream()).thenReturn(inputStream);

        when(resolver.getResources("classpath:csv/*.csv")).thenReturn(new Resource[]{resource});
        when(cryptoValidator.isSymbolValid("BTC")).thenReturn(true);
        when(cryptoValidator.isSymbolValid("ETH")).thenReturn(true);

        csvLoaderService.run(mock(ApplicationArguments.class));

        ArgumentCaptor<Price> captor = ArgumentCaptor.forClass(Price.class);
        verify(priceRepository, times(2)).save(captor.capture());
        List<Price> savedPrices = captor.getAllValues();

        Price first = savedPrices.getFirst();
        assertEquals("BTC", first.getSymbol());
        assertEquals(42000.0, first.getPrice());
        assertEquals(LocalDateTime.of(2022, 1, 1, 0, 0), first.getDateTime());

        Price second = savedPrices.get(1);
        assertEquals("ETH", second.getSymbol());
        assertEquals(3200.0, second.getPrice());
        assertEquals(LocalDateTime.of(2022, 1, 1, 1, 0), second.getDateTime());
    }

    @Test
    void run_onlyTheSupportedCryptosAreSaved() throws Exception {
        String csvContent = """
                timestamp,symbol,price
                1640995200000,BTC,42000.0
                1640998800000,NEW_STUFF,3200.0
                """;
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
        Resource resource = mock(Resource.class);
        when(resource.getInputStream()).thenReturn(inputStream);

        when(resolver.getResources("classpath:csv/*.csv")).thenReturn(new Resource[]{resource});
        when(cryptoValidator.isSymbolValid("BTC")).thenReturn(true);
        when(cryptoValidator.isSymbolValid("NEW_STUFF")).thenReturn(false);

        csvLoaderService.run(mock(ApplicationArguments.class));

        ArgumentCaptor<Price> captor = ArgumentCaptor.forClass(Price.class);
        verify(priceRepository, times(1)).save(captor.capture());
        List<Price> savedPrices = captor.getAllValues();

        Price first = savedPrices.getFirst();
        assertEquals("BTC", first.getSymbol());
        assertEquals(42000.0, first.getPrice());
        assertEquals(LocalDateTime.of(2022, 1, 1, 0, 0), first.getDateTime());

        assertEquals(1, savedPrices.size());
    }

    @Test
    void run_doesNotThrowExceptionIfNoCsvFilesFound() throws Exception {
        PriceRepository priceRepository = mock(PriceRepository.class);
        PathMatchingResourcePatternResolver resolver = mock(PathMatchingResourcePatternResolver.class);
        CsvLoaderService service = new CsvLoaderService(priceRepository, resolver, cryptoValidator);

        when(resolver.getResources("classpath:csv/*.csv")).thenReturn(new Resource[0]);

        assertDoesNotThrow(() -> service.run(mock(ApplicationArguments.class)));
    }

    @Test
    void run_doesNotThrowExceptionOnMalformedLine() throws Exception {
        PriceRepository priceRepository = mock(PriceRepository.class);
        PathMatchingResourcePatternResolver resolver = mock(PathMatchingResourcePatternResolver.class);
        CsvLoaderService service = new CsvLoaderService(priceRepository, resolver, cryptoValidator);

        String csvContent = """
                timestamp,symbol,price
                1640995200000,BTC,42000.0
                1640998800000,ETH,not_a_number
                1640999000000,ETH,3200.0
                """;
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));
        Resource resource = mock(Resource.class);
        when(resource.getInputStream()).thenReturn(inputStream);
        when(resource.getFilename()).thenReturn("test.csv");
        when(resolver.getResources("classpath:csv/*.csv")).thenReturn(new Resource[]{resource});
        when(cryptoValidator.isSymbolValid("BTC")).thenReturn(true);
        when(cryptoValidator.isSymbolValid("ETH")).thenReturn(true);

        assertDoesNotThrow(() -> service.run(mock(ApplicationArguments.class)));

        verify(priceRepository, times(2)).save(any(Price.class));
    }

    @Test
    void run_doesNotThrowExceptionOnFileProcessingError() throws Exception {
        PriceRepository priceRepository = mock(PriceRepository.class);
        PathMatchingResourcePatternResolver resolver = mock(PathMatchingResourcePatternResolver.class);
        CsvLoaderService service = new CsvLoaderService(priceRepository, resolver, cryptoValidator);

        Resource resource = mock(Resource.class);
        when(resource.getInputStream()).thenThrow(new RuntimeException("File read error"));
        when(resource.getFilename()).thenReturn("broken.csv");
        when(resolver.getResources("classpath:csv/*.csv")).thenReturn(new Resource[]{resource});

        assertDoesNotThrow(() -> service.run(mock(ApplicationArguments.class)));

        verify(priceRepository, never()).save(any(Price.class));
    }
}