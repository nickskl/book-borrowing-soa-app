package ru.nikolay.remote;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.nikolay.responses.BookResponse;

public interface RemoteBookService {
    Page<BookResponse> getBooksPaged(Pageable pageable);

    BookResponse getBook(String bookId);

    BookResponse createBook(String title, String author, String description);

    void updateBook(String bookId, String title, String author, String description);

    void deleteBook(String bookId);
}
