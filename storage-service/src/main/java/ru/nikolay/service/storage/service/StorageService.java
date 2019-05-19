package ru.nikolay.service.storage.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.nikolay.requests.BookInformationRequest;
import ru.nikolay.requests.StorageRequest;
import ru.nikolay.service.storage.domain.BookInformation;
import ru.nikolay.service.storage.domain.Storage;
import java.util.List;

public interface StorageService {
    Storage getById(String id);

    Page<Storage> getAllStoragesPaged(Pageable pageable);

    List<Storage> findStoragesByBookId(String bookId);

    List<BookInformation> getBookInformationForStorageId(String id);

    BookInformation getBookInformationForStorageIdAndBookInformationId(String storageId, String bookId);

    BookInformation getBookInformationForStorageIdAndBookId(String storageId, String bookId);

    Storage add(StorageRequest storageRequest);

    void delete(String id);

    BookInformation addBookInformationForStorageId(String id, BookInformationRequest bookInformationRequest);

    BookInformation updateBookInformationForStorageId(String id, String bookInformationId,
                                                      BookInformationRequest bookInformationRequest);

    void deleteBookInformationForStorageId(String id, String bookId);

    void deleteBookInformationForStorageIdByBookId(String storageId, String bookId);
}
