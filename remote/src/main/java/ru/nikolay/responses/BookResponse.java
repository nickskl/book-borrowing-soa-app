package ru.nikolay.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookResponse {
    private String id;
    private String title;
    private String author;
    private String description;
}
