package com.github.fabbaraujo.libraryapi.service;

import com.github.fabbaraujo.libraryapi.model.entity.Loan;

import java.util.Optional;

public interface LoanService {
    Loan save(Loan loan);
    Optional<Loan> getById(Long id);
    Loan update(Loan loan);
}
