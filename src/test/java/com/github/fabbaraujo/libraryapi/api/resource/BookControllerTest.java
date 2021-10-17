package com.github.fabbaraujo.libraryapi.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fabbaraujo.libraryapi.api.request.BookRequest;
import com.github.fabbaraujo.libraryapi.exception.BusinessException;
import com.github.fabbaraujo.libraryapi.model.entity.Book;
import com.github.fabbaraujo.libraryapi.service.BookService;
import com.github.fabbaraujo.libraryapi.service.LoanService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = BookController.class)
@AutoConfigureMockMvc
class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService service;

    @MockBean
    private LoanService loanService;

    @Test
    @DisplayName("Deve criar um livro com sucesso.")
    void createBookTest() throws Exception {

        BookRequest requestBook = createNewBookRequest();
        Book savedBook = Book.builder()
                .id(10L)
                .author("Autor")
                .title("Meu Livro")
                .isbn("123")
                .build();

        BDDMockito.given(service.save(Mockito.any(Book.class))).willReturn(savedBook);
        String json = new ObjectMapper().writeValueAsString(requestBook);

        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc
                .perform(request)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("title").value(requestBook.getTitle()))
                .andExpect(jsonPath("author").value(requestBook.getAuthor()))
                .andExpect(jsonPath("isbn").value(requestBook.getIsbn()));
    }

    @Test
    @DisplayName("Deve lançar erro de validação quando não houver dados suficientes para criação do livro.")
    void createInvalidBookTest() throws Exception {
        String json = new ObjectMapper().writeValueAsString(new BookRequest());

        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(3)));
    }

    @Test
    @DisplayName("Deve lançar erro ao tentar cadastrar um livro com isbn já utilizado por outro.")
    void createBookWithDuplicateIsbn() throws Exception {

        BookRequest requestBook = createNewBookRequest();
        String message = "Isbn já cadastrado.";
        String json = new ObjectMapper().writeValueAsString(requestBook);
        BDDMockito.given(service.save(Mockito.any(Book.class)))
                .willThrow(new BusinessException(message));

        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(BOOK_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", hasSize(1)))
                .andExpect(jsonPath("errors[0]").value(message));
    }

    @Test
    @DisplayName("Deve obter informações de um livro.")
    void getBookDetailsTest() throws Exception {
        Long id = 1L;

        Book book = Book.builder()
                .id(id)
                .author(createNewBookRequest().getAuthor())
                .title(createNewBookRequest().getTitle())
                .isbn(createNewBookRequest().getIsbn())
                .build();
        BDDMockito.given(service.getById(id)).willReturn(Optional.of(book));

        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/").concat(id.toString()))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(createNewBookRequest().getTitle()))
                .andExpect(jsonPath("author").value(createNewBookRequest().getAuthor()))
                .andExpect(jsonPath("isbn").value(createNewBookRequest().getIsbn()));
    }

    @Test
    @DisplayName("Deve retornar not found quando não encontrar o livro.")
    void bookNotFoundException() throws Exception {

        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat("/").concat("1"))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve deletar um livro.")
    void deleteBookTest() throws Exception {

        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.of(Book.builder().id(1L).build()));

        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/").concat("1"));

        mvc.perform(request)
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar resource not found quando não encontrar o livro para deletar.")
    void deleteInexistentBookTest() throws Exception {

        BDDMockito.given(service.getById(Mockito.anyLong())).willReturn(Optional.empty());

        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(BOOK_API.concat("/").concat("1"));

        mvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve atualizar um livro com sucesso.")
    void updateBookTest() throws Exception {

        Long id = 1L;
        String json = new ObjectMapper().writeValueAsString(createNewBookRequest());
        Book updatingBook = Book.builder()
                .id(id)
                .title("some title")
                .author("some author")
                .isbn("321")
                .build();
        Book updatedBook = Book.builder()
                .id(id)
                .author("Autor")
                .title("Meu Livro")
                .isbn("321")
                .build();
        BDDMockito.given(service.getById(id))
                .willReturn(Optional.of(updatingBook));
        BDDMockito.given(service.update(updatingBook))
                .willReturn(updatedBook);

        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/").concat(id.toString()))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(id))
                .andExpect(jsonPath("title").value(createNewBookRequest().getTitle()))
                .andExpect(jsonPath("author").value(createNewBookRequest().getAuthor()))
                .andExpect(jsonPath("isbn").value("321"));
    }

    @Test
    @DisplayName("Deve retornar 404 ao tentar atualizar um livro inexistente.")
    void updateInexistentBookTest() throws Exception {

        String json = new ObjectMapper().writeValueAsString(createNewBookRequest());
        BDDMockito.given(service.getById(Mockito.anyLong()))
                .willReturn(Optional.empty());

        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .put(BOOK_API.concat("/").concat("1"))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve filtrar livros.")
    void findBooksTest() throws Exception {
        Long id = 1L;

        Book book = Book.builder()
                .id(id)
                .title(createNewBookRequest().getTitle())
                .isbn(createNewBookRequest().getIsbn())
                .author(createNewBookRequest().getAuthor())
                .build();

        BDDMockito.given(service.find(
                Mockito.any(Book.class), Mockito.any(Pageable.class))
        ).willReturn(
                new PageImpl<Book>(List.of(book),
                        PageRequest.of(0, 100), 1)
        );

        String queryString = String.format("?title=%s&author=%s&page=0&size=100", book.getTitle(), book.getAuthor());

        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(BOOK_API.concat(queryString))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("content", hasSize(1)))
                .andExpect(jsonPath("totalElements").value(1))
                .andExpect(jsonPath("pageable.pageSize").value(100))
                .andExpect(jsonPath("pageable.pageNumber").value(0));
    }

    private BookRequest createNewBookRequest() {
        return BookRequest.builder()
                .author("Autor")
                .title("Meu Livro")
                .isbn("123")
                .build();
    }
}
