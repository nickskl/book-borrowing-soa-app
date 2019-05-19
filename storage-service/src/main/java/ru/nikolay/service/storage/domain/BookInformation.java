package ru.nikolay.service.storage.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import ru.nikolay.responses.BookInformationResponse;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class BookInformation {
    @Id private String id;
    private String bookId;
    private Integer numberLeft;

    public BookInformation(String bookId, Integer numberLeft) {
        this.bookId = bookId;
        this.numberLeft = numberLeft;
    }

    public BookInformationResponse toResponse() {
        return new BookInformationResponse(id, bookId, numberLeft);
    }
}
