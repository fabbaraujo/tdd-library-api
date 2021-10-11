package com.github.fabbaraujo.libraryapi.api.service;

import com.github.fabbaraujo.libraryapi.api.model.repository.BookRepository;
import com.github.fabbaraujo.libraryapi.api.service.impl.BookServiceImpl;
import com.github.fabbaraujo.libraryapi.model.entity.Book;
import com.github.fabbaraujo.libraryapi.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class BookServiceTest {

    BookService service;

    @MockBean
    BookRepository repository;

    @BeforeEach
    void setup() {
        this.service = new BookServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar um livro.")
    void saveBookTest() {
        Book book = Book.builder()
                .isbn("123")
                .author("Fulano")
                .title("As aventuras")
                .build();
        Mockito.when(repository.save(book)).thenReturn(
                Book.builder()
                        .id(1L)
                        .isbn("123")
                        .title("As aventuras")
                        .author("Fulano")
                        .build());

        Book savedBook = service.save(book);

        assertThat(savedBook.getId()).isNotNull();
        assertThat(savedBook.getIsbn()).isEqualTo("123");
        assertThat(savedBook.getTitle()).isEqualTo("As aventuras");
        assertThat(savedBook.getAuthor()).isEqualTo("Fulano");
    }
}
