package com.github.fabbaraujo.libraryapi.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoanRequest {

    @NotEmpty
    private String isbn;
    @NotEmpty
    private String customer;
    @NotEmpty
    private String customerEmail;
}
