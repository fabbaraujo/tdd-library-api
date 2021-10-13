package com.github.fabbaraujo.libraryapi.api.service;

import com.github.fabbaraujo.libraryapi.exception.BusinessException;
import com.github.fabbaraujo.libraryapi.model.entity.Book;
import com.github.fabbaraujo.libraryapi.model.repository.BookRepository;
import com.github.fabbaraujo.libraryapi.service.BookService;
import com.github.fabbaraujo.libraryapi.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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
        Book book = createValidBook();
        when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
        when(repository.save(book)).thenReturn(
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

    @Test
    @DisplayName("Deve lançar erro de negócio ao tentar salvar um livro com isbn duplicado.")
    void shouldNotSaveBookWithDuplicatedIsbn() {
        Book book = createValidBook();
        when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        Throwable exception = Assertions.catchThrowable(() -> service.save(book));

        assertThat(exception)
                .isInstanceOf(BusinessException.class)
                .hasMessage("Isbn já cadastrado.");
        Mockito.verify(repository, Mockito.never()).save(book);
    }

    @Test
    @DisplayName("Deve obter um livro por um id.")
    void getByIdTest() {
        Long id = 1L;
        Book book = createValidBook();
        book.setId(id);
        when(repository.findById(id)).thenReturn(Optional.of(book));

        Optional<Book> foundBook = service.getById(id);

        assertThat(foundBook).isPresent();
        assertThat(foundBook.get().getId()).isEqualTo(id);
        assertThat(foundBook.get().getAuthor()).isEqualTo(book.getAuthor());
        assertThat(foundBook.get().getIsbn()).isEqualTo(book.getIsbn());
        assertThat(foundBook.get().getTitle()).isEqualTo(book.getTitle());
    }

    @Test
    @DisplayName("Deve retornar vazio ao obter um livro por um id quando não existe na base.")
    void bookNotFoundByIdTest() {
        Long id = 1L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        Optional<Book> foundBook = service.getById(id);

        assertThat(foundBook).isEmpty();
    }

    @Test
    @DisplayName("Deve deletar um livro.")
    void deleteBookTest() {
        Book book = createValidBook();
        book.setId(1L);

        assertDoesNotThrow(() -> service.delete(book));

        Mockito.verify(repository, Mockito.times(1)).delete(book);
    }

    @Test
    @DisplayName("Deve ocorrer erro ao tentar deletar um livro inexistente.")
    void deleteInvalidBookTest() {
        Book book = new Book();

        assertThrows(IllegalArgumentException.class, () -> service.delete(book));

        Mockito.verify(repository, Mockito.never()).delete(book);
    }

    @Test
    @DisplayName("Deve atualizar um livro.")
    void updateBookTest() {
        long id = 1L;
        Book updatingBook = createValidBook();
        Book updatedBook = createValidBook();
        updatingBook.setId(id);
        updatedBook.setId(id);
        when(repository.save(updatingBook)).thenReturn(updatedBook);

        Book book = service.update(updatingBook);

        assertThat(book.getId()).isEqualTo(updatedBook.getId());
        assertThat(book.getIsbn()).isEqualTo(updatedBook.getIsbn());
        assertThat(book.getAuthor()).isEqualTo(updatedBook.getAuthor());
        assertThat(book.getTitle()).isEqualTo(updatedBook.getTitle());
    }

    @Test
    @DisplayName("Deve ocorrer erro ao tentar atualizar um livro inexistente.")
    void updateInvalidBookTest() {
        Book book = new Book();

        assertThrows(IllegalArgumentException.class, () -> service.update(book));

        Mockito.verify(repository, Mockito.never()).save(book);
    }

    @Test
    @DisplayName("Deve filtrar livros de acordo com as propriedades.")
    void findBookTest() {
        Book book = createValidBook();
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Book> lista = List.of(book);
        Page<Book> page = new PageImpl<>(lista, pageRequest, 1);
        when(repository.findAll(Mockito.any(Example.class), Mockito.any(Pageable.class))).thenReturn(page);

        Page<Book> result = service.find(book, pageRequest);

        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).isEqualTo(lista);
        assertThat(result.getPageable().getPageNumber()).isZero();
        assertThat(result.getPageable().getPageSize()).isEqualTo(10);
    }

    @Test
    @DisplayName("Deve obter um livro pelo isbn.")
    void getBookByIsbnTest() {
        String isbn = "123";
        Book book = createValidBook();
        book.setId(1L);
        when(repository.findByIsbn(isbn)).thenReturn(Optional.of(book));

        Optional<Book> bookByIsbn = service.getBookByIsbn(isbn);

        assertThat(bookByIsbn).isPresent();
        assertThat(bookByIsbn.get().getId()).isEqualTo(1L);
        assertThat(bookByIsbn.get().getIsbn()).isEqualTo(isbn);
        verify(repository, times(1)).findByIsbn(isbn);
    }

    private Book createValidBook() {
        return Book.builder()
                .isbn("123")
                .author("Fulano")
                .title("As aventuras")
                .build();
    }
}
