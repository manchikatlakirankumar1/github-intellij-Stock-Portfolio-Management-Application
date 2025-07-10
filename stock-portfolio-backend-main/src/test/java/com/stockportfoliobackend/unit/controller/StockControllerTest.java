package com.stockportfoliobackend.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stockportfoliobackend.controller.StockController;
import com.stockportfoliobackend.model.Stock;
import com.stockportfoliobackend.service.StockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class StockControllerTest {

    @InjectMocks
    private StockController stockController;

    @Mock
    private StockService stockService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(stockController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void shouldGetAllStocks() throws Exception {
        // Arrange
        List<Stock> mockStocks = Arrays.asList(
                new Stock(1L, "AAPL", 10, 150.0),
                new Stock(2L, "GOOGL", 5, 2800.0)
        );
        when(stockService.getAllStocks()).thenReturn(mockStocks);

        // Act & Assert
        mockMvc.perform(get("/api/stocks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].symbol").value("AAPL"))
                .andExpect(jsonPath("$[0].quantity").value(10))
                .andExpect(jsonPath("$[0].currentPrice").value(150.0))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].symbol").value("GOOGL"))
                .andExpect(jsonPath("$[1].quantity").value(5))
                .andExpect(jsonPath("$[1].currentPrice").value(2800.0));

        verify(stockService, times(1)).getAllStocks();
    }

    @Test
    void shouldAddStock() throws Exception {
        // Arrange
        Stock stockToAdd = new Stock(null, "AAPL", 10, 0.0);
        Stock savedStock = new Stock(1L, "AAPL", 10, 150.0);

        when(stockService.addStock(any(Stock.class))).thenReturn(savedStock);

        // Act & Assert
        mockMvc.perform(post("/api/stocks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(stockToAdd)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.symbol").value("AAPL"))
                .andExpect(jsonPath("$.quantity").value(10))
                .andExpect(jsonPath("$.currentPrice").value(150.0));

        verify(stockService, times(1)).addStock(any(Stock.class));
    }

    @Test
    void shouldDeleteStock() throws Exception {
        // Arrange
        Long stockId = 1L;
        doNothing().when(stockService).deleteStock(stockId);

        // Act & Assert
        mockMvc.perform(delete("/api/stocks/{id}", stockId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(stockService, times(1)).deleteStock(stockId);
    }

    @Test
    void shouldReturnPortfolioValue() throws Exception {
        // Arrange
        double portfolioValue = 15000.0;
        when(stockService.getPortfolioValue()).thenReturn(portfolioValue);

        // Act & Assert
        mockMvc.perform(get("/api/stocks/value")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("15000.0"));

        verify(stockService, times(1)).getPortfolioValue();
    }
}
