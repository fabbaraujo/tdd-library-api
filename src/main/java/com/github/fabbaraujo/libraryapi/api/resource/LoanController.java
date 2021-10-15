package com.github.fabbaraujo.libraryapi.api.resource;

import com.github.fabbaraujo.libraryapi.api.request.BookRequest;
import com.github.fabbaraujo.libraryapi.api.request.LoanFilterRequest;
import com.github.fabbaraujo.libraryapi.api.request.LoanRequest;
import com.github.fabbaraujo.libraryapi.api.request.ReturnedLoanRequest;
import com.github.fabbaraujo.libraryapi.api.response.BookResponse;
import com.github.fabbaraujo.libraryapi.api.response.LoanResponse;
import com.github.fabbaraujo.libraryapi.model.entity.Book;
import com.github.fabbaraujo.libraryapi.model.entity.Loan;
import com.github.fabbaraujo.libraryapi.service.BookService;
import com.github.fabbaraujo.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final BookService bookService;
    private final ModelMapper mapper;

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

        entity = loanService.save(entity);
        return entity.getId();
    }

    @PatchMapping("/{id}")
    public void returnBook(@PathVariable Long id, @RequestBody ReturnedLoanRequest request) {
        Loan loan = loanService.getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        loan.setReturned(request.getReturned());

        loanService.update(loan);
    }

    @GetMapping
    public Page<LoanResponse> find(LoanFilterRequest request, Pageable pageRequest) {
        Page<Loan> result = loanService.find(request, pageRequest);

        List<LoanResponse> list = result.getContent().stream()
                .map(entity -> {
                    Book book = entity.getBook();
                    BookResponse bookResponse = mapper.map(book, BookResponse.class);
                    LoanResponse loanResponse = mapper.map(entity, LoanResponse.class);
                    loanResponse.setBook(bookResponse);
                    return loanResponse;
                }).toList();

        return new PageImpl<>(list, pageRequest, result.getTotalElements());
    }
}
