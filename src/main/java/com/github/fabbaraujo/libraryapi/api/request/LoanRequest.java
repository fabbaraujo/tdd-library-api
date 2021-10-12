package com.github.fabbaraujo.libraryapi.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequest {

    private String isbn;
    private String customer;
}
