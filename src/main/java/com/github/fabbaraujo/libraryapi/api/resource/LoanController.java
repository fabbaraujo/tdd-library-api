package com.github.fabbaraujo.libraryapi.api.resource;

import com.github.fabbaraujo.libraryapi.api.request.LoanRequest;
import com.github.fabbaraujo.libraryapi.model.entity.Book;
import com.github.fabbaraujo.libraryapi.model.entity.Loan;
import com.github.fabbaraujo.libraryapi.service.BookService;
import com.github.fabbaraujo.libraryapi.service.LoanSerivce;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanSerivce loanSerivce;
    private final BookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Long create(@RequestBody LoanRequest request) {
        Book book = bookService.getBookByIsbn(request.getIsbn())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Book not found for passed isbn."));
        Loan entity = Loan.builder()
                .book(book)
                .customer(request.getCustomer())
                .loanDate(LocalDate.now())
                .build();

        entity = loanSerivce.save(entity);
        return entity.getId();
    }
}
