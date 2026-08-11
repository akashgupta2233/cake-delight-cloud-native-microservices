package com.cakedelight.catalog.controller;

import com.cakedelight.catalog.service.CakeService;
import com.cakedelight.catalog.entity.Cake;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/cakes")
public class CakeController {

    private final CakeService cakeService;

    public CakeController(CakeService cakeService) {
        this.cakeService = cakeService;
    }

    @GetMapping
    public List<Cake> getCakes(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice) {
        return cakeService.findCakes(category, minPrice, maxPrice);
    }

    @GetMapping("/{id}")
    public Cake getCake(@PathVariable Long id) {
        return cakeService.findById(id);
    }

    @PostMapping
    public ResponseEntity<Cake> createCake(@Valid @RequestBody Cake cake) {
        Cake saved = cakeService.createCake(cake);
        return ResponseEntity.created(URI.create("/cakes/" + saved.getId())).body(saved);
    }
}