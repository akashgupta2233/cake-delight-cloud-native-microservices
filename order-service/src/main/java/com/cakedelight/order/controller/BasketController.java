package com.cakedelight.order.controller;

import com.cakedelight.order.entity.BasketItem;
import com.cakedelight.order.service.BasketService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

/**
 * REST controller for shopping basket operations.
 * Manages items in the user's basket before checkout.
 */
@RestController
@RequestMapping("/basket")
public class BasketController {

    private final BasketService basketService;

    public BasketController(BasketService basketService) {
        this.basketService = basketService;
    }

    /**
     * Adds a cake to the basket or increments quantity if already present.
     *
     * @param payload the basket item to add
     * @return the created or updated basket item
     */
    @PostMapping
    public ResponseEntity<BasketItem> create(@Valid @RequestBody BasketItem payload) {
        BasketItem saved = basketService.create(payload);
        return ResponseEntity.created(URI.create("/basket/" + saved.getId())).body(saved);
    }

    /**
     * Retrieves all items in the basket.
     *
     * @return list of basket items
     */
    @GetMapping
    public List<BasketItem> list() {
        return basketService.findAll();
    }

    /**
     * Updates the quantity of a basket item.
     *
     * @param id the basket item identifier
     * @param payload the updated basket item data
     * @return the updated basket item
     */
    @PutMapping("/{id}")
    public BasketItem update(@PathVariable Long id, @Valid @RequestBody BasketItem payload) {
        return basketService.update(id, payload);
    }

    /**
     * Removes an item from the basket.
     *
     * @param id the basket item identifier
     * @return no content response
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        basketService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
