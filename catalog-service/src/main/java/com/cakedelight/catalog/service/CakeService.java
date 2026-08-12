package com.cakedelight.catalog.service;

import com.cakedelight.catalog.repository.CakeRepository;
import com.cakedelight.catalog.entity.Cake;
import com.cakedelight.catalog.exception.CakeNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service layer for cake catalog business logic.
 * Handles filtering, searching, and CRUD operations for cakes.
 */
@Service
public class CakeService {

    private final CakeRepository cakeRepository;

    public CakeService(CakeRepository cakeRepository) {
        this.cakeRepository = cakeRepository;
    }

    /**
     * Finds cakes based on dynamic filtering criteria.
     * Supports filtering by category and price range in various combinations.
     *
     * @param category optional category filter
     * @param minPrice optional minimum price
     * @param maxPrice optional maximum price
     * @return filtered list of cakes
     */
    public List<Cake> findCakes(String category, BigDecimal minPrice, BigDecimal maxPrice) {
        boolean hasCategory = StringUtils.hasText(category);
        boolean hasMin = minPrice != null;
        boolean hasMax = maxPrice != null;

        if (hasCategory && hasMin && hasMax) {
            return cakeRepository.findByCategoryIgnoreCaseAndPriceBetween(category, minPrice, maxPrice);
        }
        if (hasCategory && hasMin) {
            return cakeRepository.findByCategoryIgnoreCaseAndPriceBetween(category, minPrice, BigDecimal.valueOf(Double.MAX_VALUE));
        }
        if (hasCategory && hasMax) {
            return cakeRepository.findByCategoryIgnoreCaseAndPriceBetween(category, BigDecimal.ZERO, maxPrice);
        }
        if (hasCategory) {
            return cakeRepository.findByCategoryIgnoreCase(category);
        }
        if (hasMin && hasMax) {
            return cakeRepository.findByPriceBetween(minPrice, maxPrice);
        }
        if (hasMin) {
            return cakeRepository.findByPriceGreaterThanEqual(minPrice);
        }
        if (hasMax) {
            return cakeRepository.findByPriceLessThanEqual(maxPrice);
        }
        return cakeRepository.findAll();
    }

    /**
     * Finds a cake by ID or throws exception if not found.
     *
     * @param id the cake identifier
     * @return the cake entity
     */
    public Cake findById(Long id) {
        return cakeRepository.findById(id)
                .orElseThrow(() -> new CakeNotFoundException(id));
    }

    /**
     * Creates a new cake ensuring default values are set.
     *
     * @param cake the cake to create
     * @return the persisted cake entity
     */
    public Cake createCake(Cake cake) {
        cake.setId(null);
        if (cake.getAvailability() == null) {
            cake.setAvailability(Boolean.TRUE);
        }
        return cakeRepository.save(cake);
    }
}