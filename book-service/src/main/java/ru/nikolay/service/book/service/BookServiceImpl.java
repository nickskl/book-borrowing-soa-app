package ru.nikolay.service.book.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.nikolay.service.book.domain.Book;
import ru.nikolay.service.book.repository.BookRepository;
import ru.nikolay.requests.BookRequest;

@Service
public class BookServiceImpl implements BookService{
    @Autowired
    private BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Book> getPage(Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Book getById(String id) {
        Book result = bookRepository.findOne(id);
        if(result == null) {
            throw new NullPointerException("Book[" + id + "] not found in the database");
        }
        return result;
    }

    @Override
    @Transactional
    public Book add(BookRequest bookRequest) {
        return bookRepository.save(new Book().
                        setTitle(bookRequest.getTitle()).
                        setAuthor(bookRequest.getAuthor()).
                        setDescription(bookRequest.getDescription()));
    }

    @Override
    @Transactional
    public Book update(String id, BookRequest bookRequest) {
        Book bookToUpdate = bookRepository.findOne(id);
        if (bookToUpdate == null) {
            throw new NullPointerException("Book[" + id + "] not found in the database");
        }

        if(bookRequest.getTitle() != null) {
            bookToUpdate.setTitle(bookRequest.getTitle());
        }

        if(bookRequest.getAuthor() != null) {
            bookToUpdate.setAuthor(bookRequest.getAuthor());
        }

        if(bookRequest.getDescription() != null) {
            bookToUpdate.setDescription(bookRequest.getDescription());
        }

        return bookRepository.save(bookToUpdate);
    }

    @Override
    @Transactional
    public void delete(String id) {
        bookRepository.delete(id);
    }
}
