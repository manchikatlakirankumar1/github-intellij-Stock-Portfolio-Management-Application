CREATE TABLE STOCK (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       symbol VARCHAR(10) NOT NULL,
                       quantity INT NOT NULL,
                       current_price DOUBLE NOT NULL
);

