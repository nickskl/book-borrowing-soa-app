package ru.nikolay.service.bookBorrowing.domain;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import ru.nikolay.responses.BorrowingInfoResponse;

import java.util.Date;

@Data
@Accessors(chain = true)
public class BorrowingInfo {
    @Id
    private String id;
    private String userId;
    private String bookId;
    private String storageId;
    private Date borrowedDate;
    private Date dateToReturnBook;

    public BorrowingInfoResponse toResponse() {
        return new BorrowingInfoResponse(id, userId, bookId,
                storageId, borrowedDate, dateToReturnBook);
    }
}