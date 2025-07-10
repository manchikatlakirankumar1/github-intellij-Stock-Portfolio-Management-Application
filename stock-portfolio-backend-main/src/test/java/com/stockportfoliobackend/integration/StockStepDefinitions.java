package com.stockportfoliobackend.integration;

import com.stockportfoliobackend.StockPortfolioBackendApplication;
import com.stockportfoliobackend.model.Stock;
import com.stockportfoliobackend.repository.StockRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@CucumberContextConfiguration
@SpringBootTest(classes = StockPortfolioBackendApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)// Use application-test.properties
@ActiveProfiles("test")
public class StockStepDefinitions {

    @Autowired
    private StockRepository stockRepository;

    private List<Stock> stockList;
    private double portfolioValue;

    @Given("a stock with symbol {string} and quantity {int}")
    public void a_stock_with_symbol_and_quantity(String symbol, int quantity) {
        Stock stock = new Stock();
        stock.setSymbol(symbol);
        stock.setQuantity(quantity);
        stock.setCurrentPrice(150.0); // Simulated price
        stockRepository.save(stock);
    }

    @When("I add the stock to the portfolio")
    public void i_add_the_stock_to_the_portfolio() {
        // Stock is already saved in the repository in the @Given step
    }

    @Then("the stock should be added successfully")
    public void the_stock_should_be_added_successfully() {
        stockList = stockRepository.findAll();
        assertEquals(1, stockList.size());
        assertEquals("AAPL", stockList.get(0).getSymbol());
    }

    @Given("there are stocks in the portfolio")
    public void there_are_stocks_in_the_portfolio() {
        Stock stock1 = new Stock();
        stock1.setSymbol("AAPL");
        stock1.setQuantity(10);
        stock1.setCurrentPrice(150.0);
        stockRepository.save(stock1);

        Stock stock2 = new Stock();
        stock2.setSymbol("GOOGL");
        stock2.setQuantity(5);
        stock2.setCurrentPrice(200.0);
        stockRepository.save(stock2);
    }

    @When("I fetch all stocks")
    public void i_fetch_all_stocks() {
        stockList = stockRepository.findAll();
    }

    @Then("I should get a list of all stocks")
    public void i_should_get_a_list_of_all_stocks() {
        assertEquals(3, stockList.size());
    }

    @When("I calculate the portfolio value")
    public void i_calculate_the_portfolio_value() {
        portfolioValue = stockRepository.findAll()
                .stream()
                .mapToDouble(stock -> stock.getQuantity() * stock.getCurrentPrice())
                .sum();
    }

    @Then("I should get the total portfolio value")
    public void i_should_get_the_total_portfolio_value() {
        assertEquals(6500.0, portfolioValue, 0.1);
    }
}