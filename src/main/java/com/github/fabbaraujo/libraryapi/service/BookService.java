package com.github.fabbaraujo.libraryapi.service;

import com.github.fabbaraujo.libraryapi.model.entity.Book;
import org.springframework.stereotype.Service;

@Service
public interface BookService {
    Book save(Book book);
}
