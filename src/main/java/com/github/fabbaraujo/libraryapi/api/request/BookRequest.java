package com.github.fabbaraujo.libraryapi.api.request;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookRequest {

    private Long id;
    private String title;
    private String author;
    private String isbn;
}
