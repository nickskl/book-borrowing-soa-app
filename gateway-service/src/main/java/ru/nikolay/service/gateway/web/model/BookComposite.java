package ru.nikolay.service.gateway.web.model;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.nikolay.responses.BookResponse;

@Data
@Accessors(chain = true)
public class BookComposite {
    private String bookId;
    private String title;
    private String author;
    private String description;

    static public BookComposite fromResponse(BookResponse response) {
        return new BookComposite()
                .setBookId(response.getId())
                .setTitle(response.getTitle())
                .setAuthor(response.getAuthor())
                .setDescription(response.getDescription());
    }
}
