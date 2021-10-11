package com.github.fabbaraujo.libraryapi.api.exception;

import com.github.fabbaraujo.libraryapi.exception.BusinessException;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.List;

public class ApiErrors {
    private final List<String> errors;

    public ApiErrors(BindingResult bindingResult) {
        this.errors = new ArrayList<>();
        bindingResult.getAllErrors().forEach(error -> this.errors.add(error.getDefaultMessage()));
    }

    public ApiErrors(BusinessException exception) {
        this.errors = List.of(exception.getMessage());
    }

    public List<String> getErrors() {
        return errors;
    }
}
