package ru.nikolay.service.gateway.web.model;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.nikolay.responses.BookInformationResponse;
import ru.nikolay.responses.BookResponse;

@Data
@Accessors(chain = true)
public class BookInformationComposite {
    private String bookInformationId;
    private String bookId;
    private String bookTitle;
    private Integer numberLeft;

    static public BookInformationComposite fromResponse(BookInformationResponse bookInformationResponse,
                                                        BookResponse bookResponse) {
        if(bookInformationResponse.getBookId().equals(bookResponse.getId())) {
            return new BookInformationComposite()
                    .setBookInformationId(bookInformationResponse.getId())
                    .setBookId(bookInformationResponse.getBookId())
                    .setBookTitle(bookResponse.getTitle())
                    .setNumberLeft(bookInformationResponse.getNumberLeft());
        }
        throw new IllegalArgumentException("Book ids in responses are not equal");
    }
}
