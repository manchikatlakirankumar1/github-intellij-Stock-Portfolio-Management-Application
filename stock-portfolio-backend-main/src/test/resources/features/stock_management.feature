Feature: Stock Portfolio Management

  Scenario: Add a stock to the portfolio
    Given a stock with symbol "AAPL" and quantity 10
    When I add the stock to the portfolio
    Then the stock should be added successfully

  Scenario: Retrieve all stocks
    Given there are stocks in the portfolio
    When I fetch all stocks
    Then I should get a list of all stocks

  Scenario: Calculate portfolio value
    Given there are stocks in the portfolio
    When I calculate the portfolio value
    Then I should get the total portfolio value