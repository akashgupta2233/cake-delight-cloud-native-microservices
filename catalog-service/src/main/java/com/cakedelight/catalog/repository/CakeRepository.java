package com.cakedelight.catalog.repository;

import com.cakedelight.catalog.entity.Cake;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Repository interface for cake data access.
 * Provides query methods for filtering cakes by category and price.
 */
@Repository
public interface CakeRepository extends JpaRepository<Cake, Long> {

    List<Cake> findByCategoryIgnoreCase(String category);

    List<Cake> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    List<Cake> findByPriceGreaterThanEqual(BigDecimal minPrice);

    List<Cake> findByPriceLessThanEqual(BigDecimal maxPrice);

    List<Cake> findByCategoryIgnoreCaseAndPriceBetween(String category, BigDecimal minPrice, BigDecimal maxPrice);
}