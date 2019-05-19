package ru.nikolay.service.book.domain;

import org.junit.Test;
import ru.nikolay.responses.BookResponse;

import static org.junit.Assert.assertEquals;

public class BookTest {
    @Test
    public void bookToResponseTest() {
        BookResponse expectedResponse = new BookResponse("ABABA", "Some book",
                "AAAA", "test-test-test");
        BookResponse actualResponse = new Book()
                .setId("ABABA")
                .setTitle("Some book")
                .setAuthor("AAAA")
                .setDescription("test-test-test")
                .toResponse();
        assertEquals(expectedResponse, actualResponse);
    }
}
