package com.stockportfoliobackend.unit.service;

import com.stockportfoliobackend.model.Stock;
import com.stockportfoliobackend.repository.StockRepository;
import com.stockportfoliobackend.service.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StockServiceTest {

    @InjectMocks
    private StockService stockService;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private WebClient webClient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllStocks() {
        List<Stock> mockStocks = Arrays.asList(
                new Stock() {{
                    setId(1L);
                    setSymbol("AAPL");
                    setQuantity(10);
                    setCurrentPrice(150.0);
                }},
                new Stock() {{
                    setId(2L);
                    setSymbol("GOOGL");
                    setQuantity(5);
                    setCurrentPrice(200.0);
                }}
        );

        when(stockRepository.findAll()).thenReturn(mockStocks);

        List<Stock> result = stockService.getAllStocks();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("AAPL", result.get(0).getSymbol());
        verify(stockRepository, times(1)).findAll();
    }

    @Test
    void testAddStock() {
        // Define input stock
        Stock stock = new Stock();
        stock.setSymbol("MSFT");
        stock.setQuantity(20);

        // Define the saved stock
        Stock savedStock = new Stock();
        savedStock.setId(3L);
        savedStock.setSymbol("MSFT");
        savedStock.setQuantity(20);
        savedStock.setCurrentPrice(300.0);

        // Mock API response
        Map<String, Object> mockResponse = Map.of(
                "Time Series (1min)", Map.of(
                        "2024-12-06 20:00:00", Map.of("1. open", "300.0")
                )
        );

        // Explicit casting to avoid generic issues
        WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        WebClient.RequestHeadersSpec<?> requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        WebClient.ResponseSpec responseSpec = mock(WebClient.ResponseSpec.class);

        // Mock the WebClient chain
        when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) requestHeadersUriSpec); // Explicit casting here
        when(requestHeadersUriSpec.uri(any(Function.class))).thenReturn(requestHeadersSpec); // Correct type for `uri()`
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec); // Mock the `retrieve()` method
        when(responseSpec.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {}))
                .thenReturn(Mono.just(mockResponse)); // Mock the API response in `bodyToMono()`

        // Mock the repository save behavior
        when(stockRepository.save(any(Stock.class))).thenReturn(savedStock);

        // Call the service method
        Stock result = stockService.addStock(stock);

        // Assertions
        assertNotNull(result);
        assertEquals("MSFT", result.getSymbol());
        assertEquals(300.0, result.getCurrentPrice());

        // Verify the interactions
        verify(webClient, times(1)).get();
        verify(stockRepository, times(1)).save(any(Stock.class));
    }


    @Test
    void testDeleteStock() {
        Long stockId = 1L;

        when(stockRepository.existsById(stockId)).thenReturn(true);

        stockService.deleteStock(stockId);

        verify(stockRepository, times(1)).deleteById(stockId);
    }

    @Test
    void testDeleteStockNotFound() {
        Long stockId = 1L;

        // Mock repository to return false for existsById
        when(stockRepository.existsById(stockId)).thenReturn(false);

        // Assert that the exception is thrown
        RuntimeException exception = assertThrows(RuntimeException.class, () -> stockService.deleteStock(stockId));
        assertEquals("Stock with ID 1 not found", exception.getMessage());

        // Verify deleteById is never called
        verify(stockRepository, never()).deleteById(stockId);
    }

    @Test
    void testGetPortfolioValue() {
        List<Stock> mockStocks = Arrays.asList(
                new Stock() {{
                    setQuantity(10);
                    setCurrentPrice(150.0);
                }},
                new Stock() {{
                    setQuantity(5);
                    setCurrentPrice(200.0);
                }}
        );

        when(stockRepository.findAll()).thenReturn(mockStocks);

        double totalValue = stockService.getPortfolioValue();

        assertEquals(2500.0, totalValue);
        verify(stockRepository, times(1)).findAll();
    }
}
