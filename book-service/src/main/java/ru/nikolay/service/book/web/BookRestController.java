package ru.nikolay.service.book.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.nikolay.service.book.domain.Book;
import ru.nikolay.service.book.service.BookService;
import ru.nikolay.requests.BookRequest;
import ru.nikolay.responses.BookResponse;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/book")
public class BookRestController {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionController.class);

    @Autowired
    private BookService bookService;

    @RequestMapping(method = RequestMethod.GET)
    public Page<BookResponse> getBooks(Pageable pageable) {
        logger.debug("Book: getting all books with page[" + pageable + "]");
        Page<Book> books = bookService.getPage(pageable);
        return books.map(Book::toResponse);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public BookResponse getBookById(@PathVariable String id) {
        logger.debug("Book: getting book with id[" + id + "]");
        return bookService.getById(id).toResponse();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public BookResponse addBook(@Valid @RequestBody BookRequest bookRequest, HttpServletResponse response) {
        logger.debug("Book: creating book with request[" + bookRequest + "]");
        Book book = bookService.add(bookRequest);
        response.addHeader(HttpHeaders.LOCATION, "/book/" + book.getId());
        return book.toResponse();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    public BookResponse updateBook(@PathVariable String id, @Valid @RequestBody BookRequest bookRequest) {
        logger.debug("Book: updating book with id [" + id + "] and request[" + bookRequest + "]");
        return bookService.update(id, bookRequest).toResponse();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteBook(@PathVariable String id) {
        logger.debug("Book: deleting book with id[" + id + "]");
        bookService.delete(id);
    }
}
