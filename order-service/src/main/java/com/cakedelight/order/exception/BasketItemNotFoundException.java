package com.cakedelight.order.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class BasketItemNotFoundException extends ResponseStatusException {
    public BasketItemNotFoundException(Long id) {
        super(HttpStatus.NOT_FOUND, "Basket item not found: " + id);
    }
}
