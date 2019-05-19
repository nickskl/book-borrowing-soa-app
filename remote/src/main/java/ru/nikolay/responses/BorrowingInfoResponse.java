package ru.nikolay.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class BorrowingInfoResponse {
    private String id;
    private String userId;
    private String bookId;
    private String storageId;
    private Date borrowingDate;
    private Date dateToReturnBook;
}
