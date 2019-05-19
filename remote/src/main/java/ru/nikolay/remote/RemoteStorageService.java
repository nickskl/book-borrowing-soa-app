package ru.nikolay.remote;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.nikolay.responses.BookInformationResponse;
import ru.nikolay.responses.StorageResponse;

import java.util.List;

public interface RemoteStorageService {
    StorageResponse getStorage(String storageId);

    Page<StorageResponse> getAllStoragesPaged(Pageable pageable);

    List<StorageResponse> findStorageByBook(String bookId);

    List<BookInformationResponse> getAllBookInformationForStorage(String storageId);

    Page<BookInformationResponse> getAllBookInformationForStoragePaged(String storageId, Pageable pageable);

    BookInformationResponse getBookInformationForStorage(String storageId, String bookInformationId);

    BookInformationResponse getBookInformationForStorageAndBookId(String storageId, String bookId);

    StorageResponse createStorage(String location);

    void deleteStorage(String storageId);

    BookInformationResponse addBookInformationToStorage(String storageId, String bookId, Integer numberOfBooksLeft);

    void updateBookInformationInStorage(String storageId, String bookInformationId, String bookId,
                                                              Integer numberOfBooksLeft);

    void deleteBookInformationFromStorage(String storageId, String bookInformationId);

    void deleteBookInformationFromStorageByBookId(String storageId, String bookId);
}
