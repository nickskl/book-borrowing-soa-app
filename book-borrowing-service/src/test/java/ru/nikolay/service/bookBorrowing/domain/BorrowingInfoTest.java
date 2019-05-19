package ru.nikolay.service.bookBorrowing.domain;

import org.junit.Test;
import ru.nikolay.responses.BorrowingInfoResponse;

import java.util.Date;

import static org.junit.Assert.assertEquals;

public class BorrowingInfoTest {

    @Test
    public void bookToResponseTest() {
        BorrowingInfoResponse expectedResponse = new BorrowingInfoResponse("ABABA", "1234",
                "4321", "1111", new Date(12345), new Date(54321));
        BorrowingInfoResponse actualResponse = new BorrowingInfo()
                .setId("ABABA")
                .setUserId("1234")
                .setBookId("4321")
                .setStorageId("1111")
                .setBorrowedDate(new Date(12345))
                .setDateToReturnBook(new Date(54321))
                .toResponse();
        assertEquals(expectedResponse, actualResponse);
    }
}