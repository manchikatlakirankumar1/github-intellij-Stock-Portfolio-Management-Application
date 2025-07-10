package com.stockportfoliobackend.repository;

import com.stockportfoliobackend.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Long> {
}