package ru.nikolay.service.book.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.nikolay.requests.BookRequest;
import ru.nikolay.responses.BookResponse;
import ru.nikolay.service.book.domain.Book;
import ru.nikolay.service.book.service.BookService;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(BookRestController.class)
public class BookRestControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    @Test
    public void getExistingBookTest() throws Exception {
        Book expectedBookToFind = new Book()
                .setId("0")
                .setTitle("HELLOWORLD")
                .setAuthor("0xBADC0DE")
                .setDescription("...");
        BookResponse response = expectedBookToFind.toResponse();
        given(bookService.getById("0")).willReturn(expectedBookToFind);

        mvc.perform(get("/book/0").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedBookToFind.getId())))
                .andExpect(jsonPath("$.title", is(expectedBookToFind.getTitle())))
                .andExpect(jsonPath("$.author", is(expectedBookToFind.getAuthor())))
                .andExpect(jsonPath("$.description", is(expectedBookToFind.getDescription())));
    }

    @Test
    public void getNonexistentBookReturnsErrorTest() throws Exception {
        given(bookService.getById("0")).willThrow(new NullPointerException("Book not found"));

        mvc.perform(get("/book/0").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void addNewBookTest() throws Exception {
        Book expectedBookToAdd = new Book()
                .setId("0")
                .setTitle("HELLOWORLD")
                .setAuthor("0xBADC0DE")
                .setDescription("...");
        BookRequest request = new BookRequest("HELLOWORLD","0xBADC0DE", "...");
        given(bookService.add(request)).willReturn(expectedBookToAdd);

        mvc.perform(post("/book/")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/book/0"));
    }

    @Test
    public void deleteBookTest() throws Exception {
        mvc.perform(delete("/book/0")).andExpect(status().isNoContent());
    }

    @Test
    public void onGenericExceptionTest() throws Exception {
        given(bookService.getById(anyString())).willThrow(new RuntimeException("Some kind of error"));

        mvc.perform(get("/book/12345").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void updateBookTest() throws Exception {
        Book expectedBookToUpdate = new Book()
                .setId("0")
                .setTitle("HELLO")
                .setAuthor("0xBAD")
                .setDescription(".");
        BookRequest request = new BookRequest("HELLO","0xBAD", ".");
        given(bookService.update("0", request)).willReturn(expectedBookToUpdate);

        mvc.perform(patch("/book/0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    public void updateBookReturnsErrorTest() throws Exception {
        BookRequest request = new BookRequest("HELLO","0xBAD", ".");
        given(bookService.update("0", request)).willThrow(new NullPointerException("Book not found"));

        mvc.perform(patch("/book/0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }
}
