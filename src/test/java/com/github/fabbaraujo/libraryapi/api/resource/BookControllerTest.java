package com.github.fabbaraujo.libraryapi.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fabbaraujo.libraryapi.api.request.BookRequest;
import com.github.fabbaraujo.libraryapi.exception.BusinessException;
import com.github.fabbaraujo.libraryapi.model.entity.Book;
import com.github.fabbaraujo.libraryapi.service.BookService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
class BookControllerTest {

    static String BOOK_API = "/api/books";

    @Autowired
    MockMvc mvc;

    @MockBean
    BookService service;

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

    private BookRequest createNewBookRequest() {
        return BookRequest.builder()
                .author("Autor")
                .title("Meu Livro")
                .isbn("123")
                .build();
    }
}
