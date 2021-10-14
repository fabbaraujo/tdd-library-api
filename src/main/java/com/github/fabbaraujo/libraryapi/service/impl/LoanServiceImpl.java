package com.github.fabbaraujo.libraryapi.service.impl;

import com.github.fabbaraujo.libraryapi.exception.BusinessException;
import com.github.fabbaraujo.libraryapi.model.entity.Loan;
import com.github.fabbaraujo.libraryapi.model.repository.LoanRepository;
import com.github.fabbaraujo.libraryapi.service.LoanService;

public class LoanServiceImpl implements LoanService {
    private final LoanRepository repository;

    public LoanServiceImpl(LoanRepository repository) {
        this.repository = repository;
    }

    @Override
    public Loan save(Loan loan) {
        if(repository.existsByBookAndNotReturned(loan.getBook())) {
            throw new BusinessException("Book already loaned.");
        }
        return repository.save(loan);
    }
}
