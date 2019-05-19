package ru.nikolay.requests;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class BookInformationRequest {
    @NotNull
    private String bookId;

    @NotNull
    @Min(0)
    private Integer numberLeft;

}
