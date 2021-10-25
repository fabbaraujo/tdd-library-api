package com.github.fabbaraujo.libraryapi.api.resource;

import com.github.fabbaraujo.libraryapi.api.request.BookRequest;
import com.github.fabbaraujo.libraryapi.api.response.BookResponse;
import com.github.fabbaraujo.libraryapi.api.response.LoanResponse;
import com.github.fabbaraujo.libraryapi.model.entity.Book;
import com.github.fabbaraujo.libraryapi.model.entity.Loan;
import com.github.fabbaraujo.libraryapi.service.BookService;
import com.github.fabbaraujo.libraryapi.service.LoanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Api("Book Api")
@Slf4j
public class BookController {

    private final BookService service;
    private final LoanService loanService;
    private final ModelMapper mapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation("Creates a book")
    public BookRequest create(@RequestBody @Valid BookRequest request) {
        log.info("creating a book for isbn: {}", request.getIsbn());
        Book entity = mapper.map(request, Book.class);

        entity = service.save(entity);

        return mapper.map(entity, BookRequest.class);
    }

    @GetMapping("/{id}")
    @ApiOperation("Obtains a book details by id")
    public BookResponse getBookById(@PathVariable Long id) {
        log.info("obtaining details for book by id: {}", id);
        return service
                .getById(id)
                .map(book -> mapper.map(book, BookResponse.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @ApiOperation("Deletes a book by id")
    @ApiResponses({
            @ApiResponse(code = 204, message = "Book succesfully deleted")
    })
    public void delete(@PathVariable Long id) {
        Book book = service
                .getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        service.delete(book);
    }

    @PutMapping("/{id}")
    @ApiOperation("Updates a book")
    public BookResponse update(@PathVariable Long id, BookRequest request) {

        return service
                .getById(id)
                .map(book -> {
                    book.setAuthor(request.getAuthor());
                    book.setTitle(request.getTitle());
                    book = service.update(book);
                    return mapper.map(book, BookResponse.class);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    @ApiOperation("Find books by params")
    public Page<BookResponse> find(BookRequest request, Pageable pageRequest) {
        Book filter = mapper.map(request, Book.class);
        Page<Book> result = service.find(filter, pageRequest);

        List<BookResponse> list = result.getContent().stream()
                .map(entity -> mapper.map(entity, BookResponse.class)).toList();

        return new PageImpl<>(list, pageRequest, result.getTotalElements());
    }

    @GetMapping("/{id}/loans")
    public Page<LoanResponse> loansByBook(@PathVariable Long id, Pageable pageable) {
        Book book = service
                .getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Page<Loan> result = loanService.getLoansByBook(book, pageable);
        List<LoanResponse> listResponse = result.getContent()
                .stream()
                .map(loan -> {
                    Book loanBook = loan.getBook();
                    BookResponse bookResponse = mapper.map(loanBook, BookResponse.class);
                    LoanResponse loanResponse = mapper.map(loan, LoanResponse.class);
                    loanResponse.setBook(bookResponse);
                    return loanResponse;
                }).toList();

        return new PageImpl<LoanResponse>(listResponse, pageable, result.getTotalElements());
    }
}
