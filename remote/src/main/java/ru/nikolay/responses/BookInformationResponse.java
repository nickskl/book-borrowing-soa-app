package ru.nikolay.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookInformationResponse {
    private String id;
    private String bookId;
    private Integer numberLeft;
}
