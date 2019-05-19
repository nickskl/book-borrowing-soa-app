package ru.nikolay.service.book.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import ru.nikolay.responses.BookResponse;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    @Id private String id;
    private String title;
    private String author;
    private String description;

    public BookResponse toResponse() {
        return new BookResponse(id, title, author, description);
    }
}
