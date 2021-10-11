package com.github.fabbaraujo.libraryapi.api.resource;

import com.github.fabbaraujo.libraryapi.api.request.BookRequest;
import com.github.fabbaraujo.libraryapi.model.entity.Book;
import com.github.fabbaraujo.libraryapi.service.BookService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
    public BookRequest create(@RequestBody BookRequest request) {
        Book entity = mapper.map(request, Book.class);

        entity = service.save(entity);

        return mapper.map(entity, BookRequest.class);
    }
}
