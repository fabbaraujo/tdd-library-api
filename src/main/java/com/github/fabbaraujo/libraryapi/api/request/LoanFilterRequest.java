package com.github.fabbaraujo.libraryapi.api.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanFilterRequest {

    private String isbn;
    private String customer;
}
