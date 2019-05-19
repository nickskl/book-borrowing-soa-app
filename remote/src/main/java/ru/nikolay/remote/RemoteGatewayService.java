package ru.nikolay.remote;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.nikolay.service.gateway.web.model.BookComposite;
import ru.nikolay.service.gateway.web.model.BorrowingInfoComposite;
import ru.nikolay.service.gateway.web.model.StorageComposite;

import java.util.Date;

public interface RemoteGatewayService {
    Page<BookComposite> getBooksPaged(Pageable pageable);

    BookComposite getBook(String bookId);

    BookComposite createBook(String title, String author, String description);

    void updateBook(String bookId, String title, String author, String description);

    void deleteBook(String bookId);

    BorrowingInfoComposite borrowBook(String userId, String bookId, String storageId, Date borrowedDate,
                                      Date dateToReturnBook);

    StorageComposite getStorage(String storageId);

    StorageComposite createStorage(String location);

    Page<StorageComposite> getAllStoragesPaged(Pageable pageable);

    boolean isAuthenticated(String token);

    boolean isAdmin(String token);

    TokenPair refreshToken(String refreshToken);

    int countBookBorrowings();
    int countStoragesViewed();
    int countBookViewed();
}
