package com.cakedelight.catalog.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Exception thrown when a requested cake is not found.
 * Returns HTTP 404 status.
 */
public class CakeNotFoundException extends ResponseStatusException {

    public CakeNotFoundException(Long id) {
        super(HttpStatus.NOT_FOUND, "Cake not found: " + id);
    }
}