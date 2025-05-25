package com.bernate.services_back.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Anotar con @ResponseStatus(HttpStatus.NOT_FOUND) hace que Spring devuelva 404
// automáticamente si esta excepción no es capturada por un @ExceptionHandler.
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}