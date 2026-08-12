package com.cakedelight.catalog.controller;

import com.cakedelight.catalog.service.CakeService;
import com.cakedelight.catalog.entity.Cake;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

/**
 * REST controller for cake catalog operations.
 * Provides endpoints for browsing, filtering, and managing cakes.
 */
@RestController
@RequestMapping("/cakes")
public class CakeController {

    private final CakeService cakeService;

    public CakeController(CakeService cakeService) {
        this.cakeService = cakeService;
    }

    /**
     * Retrieves cakes with optional filtering by category and price range.
     *
     * @param category filter by cake category (case-insensitive)
     * @param minPrice filter by minimum price
     * @param maxPrice filter by maximum price
     * @return list of cakes matching the criteria
     */
    @GetMapping
    public List<Cake> getCakes(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        return cakeService.findCakes(category, minPrice, maxPrice);
    }

    /**
     * Retrieves a single cake by its ID.
     *
     * @param id the cake identifier
     * @return the cake entity
     */
    @GetMapping("/{id}")
    public Cake getCake(@PathVariable Long id) {
        return cakeService.findById(id);
    }

    /**
     * Creates a new cake in the catalog.
     *
     * @param cake the cake data
     * @return the created cake with location header
     */
    @PostMapping
    public ResponseEntity<Cake> createCake(@Valid @RequestBody Cake cake) {
        Cake saved = cakeService.createCake(cake);
        return ResponseEntity.created(URI.create("/cakes/" + saved.getId())).body(saved);
    }
}