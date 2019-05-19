package ru.nikolay.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookRequest {
    @NotNull
    @Size(min= 2, max = 30)
    private String title;
    @NotNull
    @Size(min= 2, max = 30)
    private String author;
    @NotNull
    private String description;
}
