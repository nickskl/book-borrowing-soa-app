package ru.nikolay.service.gateway.web.model;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.nikolay.responses.BookResponse;
import ru.nikolay.responses.BorrowingInfoResponse;
import ru.nikolay.responses.StorageResponse;
import ru.nikolay.responses.UserResponse;

import java.util.Date;

@Data
@Accessors(chain = true)
public class BorrowingInfoComposite {
    private String borrowingInfoId;
    private String userId;
    private String bookId;
    private String bookTitle;
    private String storageId;
    private String storageLocation;
    private Date borrowingDate;
    private Date dateToReturnBook;

    public static BorrowingInfoComposite fromResponse(BorrowingInfoResponse borrowingInfoResponse,
                                                      UserResponse userResponse, BookResponse bookResponse,
                                                      StorageResponse storageResponse) {
        if(bookResponse != null) {
            if(!bookResponse.getId().equals(borrowingInfoResponse.getBookId())) {
                throw new IllegalArgumentException("Book ids in responses are not equal");
            }
        }
        if(userResponse != null) {
            if(!userResponse.getId().equals(borrowingInfoResponse.getUserId())) {
                throw new IllegalArgumentException("User ids in responses are not equal");
            }
        }
        if(storageResponse != null) {
            if(!storageResponse.getId().equals(borrowingInfoResponse.getStorageId())) {
                throw new IllegalArgumentException("Storage ids in responses are not equal");
            }
        }

        String title = bookResponse == null ? null : bookResponse.getTitle();
        String location = storageResponse == null ? null : storageResponse.getLocation();

        return new BorrowingInfoComposite()
                .setBorrowingInfoId(borrowingInfoResponse.getId())
                .setBookId(borrowingInfoResponse.getBookId())
                .setBookTitle(title)
                .setUserId(borrowingInfoResponse.getUserId())
                .setStorageId(borrowingInfoResponse.getStorageId())
                .setStorageLocation(location)
                .setBorrowingDate(borrowingInfoResponse.getBorrowingDate())
                .setDateToReturnBook(borrowingInfoResponse.getDateToReturnBook());
    }
}
