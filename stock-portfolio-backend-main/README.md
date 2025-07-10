# Stock Portfolio Management - Backend

## Requirements
- Java 17
- Maven
- An internet connection (to fetch stock prices)

## Steps to Run
1. Navigate to the backend directory:
2. Build the project using Maven: cd stock-portfolio-backend
3. Run the application: mvn clean install
4. mvn spring-boot:run 
5. The backend will be available at `http://localhost:8080`.

## API Endpoints
- **POST** `/api/stocks` - Add a stock.
- **DELETE** `/api/stocks/{symbol}` - Remove a stock.
- **GET** `/api/stocks` - Get all stocks in the portfolio.
- **GET** `/api/stocks/value` - Get total portfolio value.

## Notes
- Uses H2 in-memory database for local testing.
- Stock prices fetched via [Alpha Vantage API].