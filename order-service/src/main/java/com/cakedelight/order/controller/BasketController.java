package com.cakedelight.order.controller;

import com.cakedelight.order.entity.BasketItem;
import com.cakedelight.order.service.BasketService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/basket")
public class BasketController {

    private final BasketService basketService;

    public BasketController(BasketService basketService) {
        this.basketService = basketService;
    }

    @PostMapping
    public ResponseEntity<BasketItem> create(@Valid @RequestBody BasketItem payload) {
        BasketItem saved = basketService.create(payload);
        return ResponseEntity.created(URI.create("/basket/" + saved.getId())).body(saved);
    }

    @GetMapping
    public List<BasketItem> list() {
        return basketService.findAll();
    }

    @PutMapping("/{id}")
    public BasketItem update(@PathVariable Long id, @Valid @RequestBody BasketItem payload) {
        return basketService.update(id, payload);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        basketService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
