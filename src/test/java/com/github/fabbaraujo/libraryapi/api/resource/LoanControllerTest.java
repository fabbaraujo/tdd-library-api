package com.github.fabbaraujo.libraryapi.api.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fabbaraujo.libraryapi.api.request.LoanRequest;
import com.github.fabbaraujo.libraryapi.model.entity.Book;
import com.github.fabbaraujo.libraryapi.model.entity.Loan;
import com.github.fabbaraujo.libraryapi.service.BookService;
import com.github.fabbaraujo.libraryapi.service.LoanSerivce;
import org.hamcrest.Matchers;
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

import java.time.LocalDate;
import java.util.Optional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = LoanController.class)
@AutoConfigureMockMvc
class LoanControllerTest {

    static final String LOAN_API = "/api/loans";

    @Autowired
    MockMvc mvc;

    @MockBean
    private BookService bookService;

    @MockBean
    private LoanSerivce loanService;

    @Test
    @DisplayName("Deve realizar um empréstimo.")
    void createLoanTest() throws Exception {
        LoanRequest requestBody = LoanRequest.builder()
                .isbn("123")
                .customer("Fulano")
                .build();
        String json = new ObjectMapper().writeValueAsString(requestBody);
        Book book = Book.builder()
                .id(1L)
                .isbn("123")
                .build();
        Loan loan = Loan.builder()
                .id(1L)
                .customer("Fulano")
                .book(book)
                .loanDate(LocalDate.now())
                .build();
        BDDMockito.given(bookService.getBookByIsbn("123")).willReturn(Optional.of(book));
        BDDMockito.given(loanService.save(Mockito.any(Loan.class))).willReturn(loan);

        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(LOAN_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isCreated())
                .andExpect(content().string("1"));
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar fazer empréstimo de um livro inexistente.")
    void invalidIsbnCreateLoanTest() throws Exception {
        LoanRequest requestBody = LoanRequest.builder()
                .isbn("123")
                .customer("Fulano")
                .build();
        String json = new ObjectMapper().writeValueAsString(requestBody);
        BDDMockito.given(bookService.getBookByIsbn("123")).willReturn(Optional.empty());

        final MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .post(LOAN_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(request)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(jsonPath("errors[0]").value("Book not found for passed isbn."));
    }
}
