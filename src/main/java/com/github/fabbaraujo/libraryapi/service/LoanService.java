package com.github.fabbaraujo.libraryapi.service;

import com.github.fabbaraujo.libraryapi.api.request.LoanFilterRequest;
import com.github.fabbaraujo.libraryapi.model.entity.Book;
import com.github.fabbaraujo.libraryapi.model.entity.Loan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface LoanService {
    Loan save(Loan loan);
    Optional<Loan> getById(Long id);
    Loan update(Loan loan);
    Page<Loan> find(LoanFilterRequest filterRequest, Pageable pageable);
    Page<Loan> getLoansByBook(Book book, Pageable pageable);
}
