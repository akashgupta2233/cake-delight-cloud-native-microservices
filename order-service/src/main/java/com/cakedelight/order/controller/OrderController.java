package com.cakedelight.order.controller;

import com.cakedelight.order.entity.CustomerOrder;
import com.cakedelight.order.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    public ResponseEntity<CustomerOrder> checkout() {
        CustomerOrder created = orderService.checkout();
        return ResponseEntity.created(URI.create("/orders/" + created.getId())).body(created);
    }
}
