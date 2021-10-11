package com.github.fabbaraujo.libraryapi.service;

import com.github.fabbaraujo.libraryapi.model.entity.Book;

import java.util.Optional;

public interface BookService {
    Book save(Book book);
    Optional<Book> getById(Long id);
}
