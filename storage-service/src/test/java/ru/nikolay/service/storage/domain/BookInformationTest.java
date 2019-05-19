package ru.nikolay.service.storage.domain;

import org.junit.Test;
import ru.nikolay.responses.BookInformationResponse;

import static org.junit.Assert.assertEquals;

public class BookInformationTest {
    @Test
    public void bookInformationToResponseTest() {
        BookInformationResponse expectedResponse = new BookInformationResponse("0", "0xBADC0DE", 10);
        BookInformationResponse actualResponse = new BookInformation()
                .setId("0")
                .setBookId("0xBADC0DE")
                .setNumberLeft(10)
                .toResponse();
        assertEquals(expectedResponse, actualResponse);
    }
}
