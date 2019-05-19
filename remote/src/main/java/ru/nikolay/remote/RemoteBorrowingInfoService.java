package ru.nikolay.remote;

import ru.nikolay.responses.BorrowingInfoResponse;

import java.util.Date;
import java.util.List;

public interface RemoteBorrowingInfoService {
    BorrowingInfoResponse getBorrowingInfo(String borrowingInfoId);
    List<BorrowingInfoResponse> getAllBorrowedBookInfoByUser(String userId);
    List<BorrowingInfoResponse> getAllBorrowedBookInfoByBook(String bookId);
    BorrowingInfoResponse borrowBook(String userId, String bookId, String storageId, Date borrowedDate,
                                     Date dateToReturnBook);
    void returnBook(String borrowingInfoId);
}
