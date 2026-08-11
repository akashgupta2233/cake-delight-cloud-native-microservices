package com.cakedelight.catalog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CakeNotFoundException extends ResponseStatusException {

    public CakeNotFoundException(Long id) {
        super(HttpStatus.NOT_FOUND, "Cake not found: " + id);
    }
}