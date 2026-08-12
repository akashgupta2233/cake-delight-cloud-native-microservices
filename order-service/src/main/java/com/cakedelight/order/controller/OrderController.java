package com.cakedelight.order.controller;

import com.cakedelight.order.entity.CustomerOrder;
import com.cakedelight.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

/**
 * REST controller for order checkout operations.
 * Handles converting basket items into completed orders.
 */
@RestController
@RequestMapping
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    /**
     * Processes checkout by creating an order from basket items.
     * Publishes order completed event and clears the basket.
     *
     * @return the created order with location header
     */
    @PostMapping("/checkout")
    public ResponseEntity<CustomerOrder> checkout() {
        CustomerOrder created = orderService.checkout();
        return ResponseEntity.created(URI.create("/orders/" + created.getId())).body(created);
    }
}
