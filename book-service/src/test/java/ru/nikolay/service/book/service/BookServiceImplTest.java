package ru.nikolay.service.book.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.internal.matchers.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.nikolay.requests.BookRequest;
import ru.nikolay.service.book.domain.Book;
import ru.nikolay.service.book.repository.BookRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class BookServiceImplTest {
    @MockBean
    private BookRepository mockedBookRepository;

    @Autowired
    private BookServiceImpl bookService;

    @Test
    public void getBookByIdTest() {
        Book expectedBookToFind = new Book()
                .setId("0")
                .setTitle("HELLOWORLD")
                .setAuthor("0xBADC0DE")
                .setDescription("...");
        given(mockedBookRepository.findOne("0")).willReturn(expectedBookToFind);

        Book actualBookFound = bookService.getById("0");

        assertEquals(expectedBookToFind, actualBookFound);
    }

    @Test(expected = NullPointerException.class)
    public void getBookByIdThrowsOnNonexistentBookTest() {
        given(mockedBookRepository.findOne(anyString())).willReturn(null);
        bookService.getById("0");
    }

    @Test
    public void getAllBooksPagedTest() {
        PageRequest pageRequest = new PageRequest(1, 2);
        List<Book> expectedBooks = new ArrayList<>();
        expectedBooks.add(new Book("0", "book-0", "author-0", "description-0"));
        expectedBooks.add(new Book("1", "book-1", "author-1", "description-1"));
        given(mockedBookRepository.findAll(pageRequest)).willReturn(new PageImpl<Book>(expectedBooks, pageRequest, 20));

        Page<Book> actualBooks = bookService.getPage(pageRequest);

        assertThat(actualBooks).containsExactlyElementsOf(expectedBooks);
    }

    @Test
    public void addBookTest() {
        Book expectedBookToBeAdded = new Book()
                .setTitle("HELLOWORLD")
                .setAuthor("0xBADC0DE")
                .setDescription("...");

        BookRequest request = new BookRequest("HELLOWORLD","0xBADC0DE", "...");
        given(mockedBookRepository.save(expectedBookToBeAdded)).willReturn(expectedBookToBeAdded);

        Book actualBookAdded = bookService.add(request);

        assertEquals("HELLOWORLD", actualBookAdded.getTitle());
        assertEquals("0xBADC0DE", actualBookAdded.getAuthor());
        assertEquals("...", actualBookAdded.getDescription());

        verify(mockedBookRepository, times(1)).save(expectedBookToBeAdded);
    }

    @Test
    public void deleteBookTest() {
        bookService.delete("Hello world!");
        verify(mockedBookRepository, times(1)).delete("Hello world!");
    }

    @Test(expected = NullPointerException.class)
    public void updateBookThrowsOnBookNotFoundTest() {
        given(mockedBookRepository.findOne(anyString())).willReturn(null);
        bookService.update("0", new BookRequest("1", "2", "3"));
    }

    @Test
    public void updateBookTest() {
        Book expectedBookToBeUpdated = new Book()
                .setId("123")
                .setTitle("HELLOWORLD")
                .setAuthor("0xBADC0DE")
                .setDescription("...");
        Book expectedUpdateResult = new Book()
                .setId("123")
                .setTitle("HELL")
                .setAuthor("ABABA")
                .setDescription("WHO READS THIS STUFF");

        BookRequest request = new BookRequest("HELL","ABABA", "WHO READS THIS STUFF");
        given(mockedBookRepository.save(expectedUpdateResult)).willReturn(expectedUpdateResult);
        given(mockedBookRepository.findOne("123")).willReturn(expectedBookToBeUpdated);
        Book actualBookUpdated = bookService.update("123", request);

        assertEquals("HELL", actualBookUpdated.getTitle());
        assertEquals("ABABA", actualBookUpdated.getAuthor());
        assertEquals("WHO READS THIS STUFF", actualBookUpdated.getDescription());

        verify(mockedBookRepository, times(1)).save(expectedBookToBeUpdated);
    }


    @Test
    public void kindaUpdateBookTest() {
        Book expectedBookToBeUpdated = new Book()
                .setId("123")
                .setTitle("HELLOWORLD")
                .setAuthor("0xBADC0DE")
                .setDescription("...");

        BookRequest request = new BookRequest(null,null, null);
        given(mockedBookRepository.save(expectedBookToBeUpdated)).willReturn(expectedBookToBeUpdated);
        given(mockedBookRepository.findOne("123")).willReturn(expectedBookToBeUpdated);
        Book actualBookUpdated = bookService.update("123", request);

        assertEquals("HELLOWORLD", actualBookUpdated.getTitle());
        assertEquals("0xBADC0DE", actualBookUpdated.getAuthor());
        assertEquals("...", actualBookUpdated.getDescription());

        verify(mockedBookRepository, times(1)).save(expectedBookToBeUpdated);
    }
}
