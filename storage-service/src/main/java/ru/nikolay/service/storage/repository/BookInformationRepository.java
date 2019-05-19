package ru.nikolay.service.storage.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import ru.nikolay.service.storage.domain.BookInformation;

import java.util.List;

public interface BookInformationRepository extends MongoRepository<BookInformation, String> {
    BookInformation findFirstByBookId(String bookId);
    Page<BookInformation> findAllByIdIn(List<String> bookInformationIds, Pageable pageable);
    List<BookInformation> findAllByBookId(String bookId);
}
