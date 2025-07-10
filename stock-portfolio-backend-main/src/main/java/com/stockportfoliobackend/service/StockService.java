package com.stockportfoliobackend.service;

import com.stockportfoliobackend.model.Stock;
import com.stockportfoliobackend.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;

@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private WebClient webClient;

    @Value("${alpha.vantage.api.key}")
    private String apiKey;

    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    public Stock addStock(Stock stock) {
        double currentPrice = fetchStockPrice(stock.getSymbol());
        stock.setCurrentPrice(currentPrice);
        return stockRepository.save(stock);
    }

    public void deleteStock(Long id) {
        if (!stockRepository.existsById(id)) {
            throw new RuntimeException("Stock with ID " + id + " not found");
        }
        stockRepository.deleteById(id);
    }

    public double getPortfolioValue() {
        return stockRepository.findAll()
                .stream()
                .mapToDouble(stock -> stock.getQuantity() * stock.getCurrentPrice())
                .sum();
    }

    private double fetchStockPrice(String symbol) {
        try {
            // Use ParameterizedTypeReference for type safety
            Map<String, Object> response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/query")
                            .queryParam("function", "TIME_SERIES_INTRADAY")
                            .queryParam("symbol", symbol)
                            .queryParam("interval", "1min")
                            .queryParam("apikey", apiKey)
                            .build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .block(); // Blocking for simplicity; you can make it reactive if needed.

            // Validate response
            if (response == null || !response.containsKey("Time Series (1min)")) {
                throw new RuntimeException("Invalid response from Alpha Vantage API");
            }

            // Extract "Time Series (1min)"
            Map<String, Object> timeSeries = (Map<String, Object>) response.get("Time Series (1min)");
            if (timeSeries == null || timeSeries.isEmpty()) {
                throw new RuntimeException("Invalid or missing 'Time Series (1min)' in API response");
            }

            // Get the latest timestamp
            String latestTimestamp = timeSeries.keySet().stream().findFirst()
                    .orElseThrow(() -> new RuntimeException("No timestamp data found in 'Time Series (1min)'"));

            // Get the latest stock data
            Map<String, Object> latestData = (Map<String, Object>) timeSeries.get(latestTimestamp);
            if (latestData == null || latestData.isEmpty()) {
                throw new RuntimeException("Invalid or missing stock data for latest timestamp");
            }

            // Extract the stock's opening price
            String openPriceString = (String) latestData.get("1. open");
            if (openPriceString == null || openPriceString.isEmpty()) {
                throw new RuntimeException("Missing '1. open' field in stock data");
            }

            // Parse and return the opening price
            return Double.parseDouble(openPriceString);

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch stock price for symbol: " + symbol, e);
        }
    }
}
