package ru.nikolay.service.book.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.nikolay.service.book.domain.Book;
import ru.nikolay.requests.BookRequest;

public interface BookService {
    Page<Book> getPage(Pageable pageable);

    Book getById(String id);

    Book add(BookRequest book);

    Book update(String id, BookRequest book);

    void delete(String id);
}
