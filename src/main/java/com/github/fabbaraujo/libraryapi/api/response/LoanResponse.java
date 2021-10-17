package com.github.fabbaraujo.libraryapi.api.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanResponse {

    private Long id;
    private String isbn;
    private String customer;
    private String customerEmail;
    private BookResponse book;
}
