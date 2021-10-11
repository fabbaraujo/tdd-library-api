package com.github.fabbaraujo.libraryapi.api.resource;

import com.github.fabbaraujo.libraryapi.api.exception.ApiErrors;
import com.github.fabbaraujo.libraryapi.api.request.BookRequest;
import com.github.fabbaraujo.libraryapi.api.response.BookResponse;
import com.github.fabbaraujo.libraryapi.exception.BusinessException;
import com.github.fabbaraujo.libraryapi.model.entity.Book;
import com.github.fabbaraujo.libraryapi.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookService service;
    private final ModelMapper mapper;

    public BookController(BookService service, ModelMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BookRequest create(@RequestBody @Valid BookRequest request) {
        Book entity = mapper.map(request, Book.class);

        entity = service.save(entity);

        return mapper.map(entity, BookRequest.class);
    }

    @GetMapping("/{id}")
    public BookResponse getBookById(@PathVariable Long id) {
        return service
                .getById(id)
                .map(book -> mapper.map(book, BookResponse.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        Book book = service
                .getById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        service.delete(book);
    }

    @PutMapping("/{id}")
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidationExceptions(MethodArgumentNotValidException exception) {
        BindingResult bindResult = exception.getBindingResult();

        return new ApiErrors(bindResult);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleBusinessException(BusinessException exception) {
        return new ApiErrors(exception);
    }
}
