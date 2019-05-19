package ru.nikolay.service.gateway.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import ru.nikolay.remote.RemoteBookService;
import ru.nikolay.remote.RemoteBorrowingInfoService;
import ru.nikolay.remote.RemoteStorageService;
import ru.nikolay.remote.RemoteUserService;
import ru.nikolay.requests.BookInformationRequest;
import ru.nikolay.requests.BookRequest;
import ru.nikolay.requests.StorageRequest;
import ru.nikolay.requests.UserRequest;
import ru.nikolay.responses.*;
import ru.nikolay.service.gateway.queue.TaskQueue;
import ru.nikolay.service.gateway.web.model.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@WebMvcTest(GatewayRestController.class)
public class GatewayRestControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RemoteStorageService storageService;

    @MockBean
    private RemoteBookService bookService;

    @MockBean
    private RemoteUserService userService;

    @MockBean
    private RemoteBorrowingInfoService borrowingInfoService;

    @MockBean
    TaskQueue queue;

    @Test
    public void getBooksTest() throws Exception {
        List<BookResponse> bookResponses = new ArrayList<>();
        bookResponses.add(new BookResponse("1", "hehehe",
                "random", "nothing special"));
        bookResponses.add(new BookResponse("2", "hehehe!",
                "rand0m", "????"));

        PageRequest pageRequest = new PageRequest(2,2);
        PageImpl<BookResponse> bookResponsePage = new PageImpl<BookResponse>(bookResponses,
                pageRequest, 10);

        given(bookService.getBooksPaged(pageRequest)).willReturn(bookResponsePage);

        mvc.perform(get("/book?page=2&size=2").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(10)))
                .andExpect(jsonPath("$.size", is(2)))
                .andExpect(jsonPath("$.content.size()", is(2)))
                .andExpect(jsonPath("$.content[0].bookId", is("1")))
                .andExpect(jsonPath("$.content[1].bookId", is("2")));
    }

    @Test
    public void getBookByIdTest() throws Exception {
        BookResponse bookResponse = new BookResponse("1", "hehehe",
                "random", "nothing special");

        given(bookService.getBook("1")).willReturn(bookResponse);
        mvc.perform(get("/book/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookId", is(bookResponse.getId())))
                .andExpect(jsonPath("$.title", is(bookResponse.getTitle())))
                .andExpect(jsonPath("$.author", is(bookResponse.getAuthor())))
                .andExpect(jsonPath("$.description", is(bookResponse.getDescription())));
    }

    @Test
    public void createBookTest() throws Exception {
        BookRequest request = new BookRequest("hehehe",
                "random", "who reads this stuff?");
        BookResponse bookResponse = new BookResponse("1", "hehehe",
                "random", "who reads this stuff?");

        given(bookService.createBook("hehehe","random", "who reads this stuff?"))
                .willReturn(bookResponse);

        MvcResult result = mvc.perform(post("/book")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        BookComposite bookComposite = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookComposite.class);
        assertThat(bookComposite).isEqualTo(BookComposite.fromResponse(bookResponse));
    }

    @Test
    public void updateBookTest() throws Exception {
        BookRequest request = new BookRequest("hehehe",
                "random", "who reads this stuff?");

        mvc.perform(patch("/book/11")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        verify(bookService, times(1)).updateBook("11",
                request.getTitle(), request.getAuthor(), request.getDescription());
    }

    @Test
    public void deleteBookTest() throws Exception {
        List<StorageResponse> storageResponses = new ArrayList<>();
        List<String> bookInfoIds = new ArrayList<>();
        bookInfoIds.add("11");

        storageResponses.add(new StorageResponse("22", "ababa", bookInfoIds));

        List<BorrowingInfoResponse> borrowingInfoResponses = new ArrayList<>();
        borrowingInfoResponses.add(new BorrowingInfoResponse("1", "2", "BOOK", "22",
                new Date(11111), new Date(99999)));
        borrowingInfoResponses.add(new BorrowingInfoResponse("2", "22", "BOOK", "22",
                new Date(11111), new Date(99999)));

        given(storageService.findStorageByBook("BOOK")).willReturn(storageResponses);
        given(borrowingInfoService.getAllBorrowedBookInfoByBook("BOOK")).willReturn(borrowingInfoResponses);

        mvc.perform(delete("/book/BOOK")).andExpect(status().isNoContent());

        verify(storageService, times(1))
                .deleteBookInformationFromStorageByBookId("22", "BOOK");
        verify(borrowingInfoService, times(1)).returnBook("1");
        verify(borrowingInfoService, times(1)).returnBook("2");
        verify(bookService, times(1)).deleteBook("BOOK");
    }

    @Test
    public void getStoragesTest() throws Exception {
        List<String> bookInfoIds = new ArrayList<>();
        bookInfoIds.add("11");

        List<StorageResponse> storageResponses = new ArrayList<>();
        storageResponses.add(new StorageResponse("1", "hehehe", new ArrayList<>()));
        storageResponses.add(new StorageResponse("2", "hehehe!",bookInfoIds));

        PageRequest pageRequest = new PageRequest(2,2);
        PageImpl<StorageResponse> storageResponsePage = new PageImpl<StorageResponse>(storageResponses,
                pageRequest, 10);

        given(storageService.getAllStoragesPaged(pageRequest)).willReturn(storageResponsePage);

        mvc.perform(get("/storage?page=2&size=2").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", is(10)))
                .andExpect(jsonPath("$.size", is(2)))
                .andExpect(jsonPath("$.content.size()", is(2)))
                .andExpect(jsonPath("$.content[0].storageId", is("1")))
                .andExpect(jsonPath("$.content[1].storageId", is("2")));
    }

    @Test
    public void getStorageByIdTest() throws Exception {
        List<String> bookInfoIds = new ArrayList<>();
        bookInfoIds.add("11");
        StorageResponse storageResponse = new StorageResponse("2", "hehehe!",bookInfoIds);

        given(storageService.getStorage("2")).willReturn(storageResponse);
        mvc.perform(get("/storage/2").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.storageId", is(storageResponse.getId())))
                .andExpect(jsonPath("$.location", is(storageResponse.getLocation())));
    }

    @Test
    public void getBookInformationByStorageAndBookId() throws Exception {
        BookInformationResponse bookInformationResponse = new BookInformationResponse("QAZXSW",
                "QWERTY", 1);
        BookResponse bookResponse = new BookResponse("QWERTY", "HELLO", "WORLD", "!!!!");

        given(storageService.getBookInformationForStorage("666", "QAZXSW"))
                .willReturn(bookInformationResponse);
        given(bookService.getBook("QWERTY")).willReturn(bookResponse);

        MvcResult result = mvc.perform(get("/storage/666/books/QAZXSW").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BookInformationComposite composite = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookInformationComposite.class);
        assertThat(composite).isEqualTo(BookInformationComposite.fromResponse(bookInformationResponse, bookResponse));
    }

    @Test
    public void createStorageTest() throws Exception {
        List<BookInformationRequest> bookInfos = new ArrayList<>();
        StorageRequest request = new StorageRequest("hehehe", bookInfos);

        List<String> bookInfoIds = new ArrayList<>();

        StorageResponse storageResponse = new StorageResponse("1", "hehehe", bookInfoIds);

        given(storageService.createStorage("hehehe")).willReturn(storageResponse);

        MvcResult result = mvc.perform(post("/storage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        StorageComposite storageComposite = objectMapper.readValue(result.getResponse().getContentAsString(),
                StorageComposite.class);
        assertThat(storageComposite).isEqualTo(StorageComposite.fromResponse(storageResponse));
    }

    @Test
    public void getBorrowingInfoForBookId() throws Exception {
        List<BorrowingInfoResponse> borrowingInfoResponses = new ArrayList<>();
        borrowingInfoResponses.add(new BorrowingInfoResponse("1", "a",
                "baad", "1223", new Date(), new Date()));
        UserResponse userResponse = new UserResponse("a", "test-subject");
        BookResponse bookResponse = new BookResponse("baad", "bad", "bad", "bad");
        StorageResponse storageResponse = new StorageResponse("1223", "somewhere", new ArrayList<>());

        given(borrowingInfoService.getAllBorrowedBookInfoByBook("baad")).willReturn(borrowingInfoResponses);
        given(userService.getUser("a")).willReturn(userResponse);
        given(bookService.getBook("baad")).willReturn(bookResponse);
        given(storageService.getStorage("1223")).willReturn(storageResponse);

        MvcResult result = mvc.perform(get("/borrow?bookId=baad")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        BorrowingInfoComposite[] borrowingInfoComposite = objectMapper.readValue(result.getResponse().getContentAsString(),
                BorrowingInfoComposite[].class);
        assertThat(borrowingInfoComposite[0]).isEqualTo(BorrowingInfoComposite.fromResponse(borrowingInfoResponses.get(0),
                userResponse, bookResponse, storageResponse));
    }

    @Test
    public void getUserByIdTest() throws Exception {
        UserResponse userResponse = new UserResponse("1", "hehehe");

        given(userService.getUser("1")).willReturn(userResponse);
        mvc.perform(get("/user/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(userResponse.getId())))
                .andExpect(jsonPath("$.login", is(userResponse.getLogin())));
    }

    @Test
    public void createUserTest() throws Exception {
        UserRequest userRequest = new UserRequest("hehehe");
        UserResponse userResponse = new UserResponse("1", "hehehe");

        given(userService.createUser("hehehe")).willReturn(userResponse);

        MvcResult result = mvc.perform(post("/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andReturn();

        UserComposite userComposite = objectMapper.readValue(result.getResponse().getContentAsString(),
                UserComposite.class);
        assertThat(userComposite).isEqualTo(UserComposite.fromResponse(userResponse));
    }

    @Test
    public void deleteUserTest() throws Exception {
        List<BorrowingInfoResponse> borrowingInfoResponses = new ArrayList<>();
        borrowingInfoResponses.add(new BorrowingInfoResponse("1", "n00b", "BOOK", "22",
                new Date(11111), new Date(99999)));
        borrowingInfoResponses.add(new BorrowingInfoResponse("2", "n00b", "BOOK-2", "2222",
                new Date(11111), new Date(99999)));

        given(borrowingInfoService.getAllBorrowedBookInfoByUser("n00b")).willReturn(borrowingInfoResponses);

        mvc.perform(delete("/user/n00b")).andExpect(status().isNoContent());

        verify(borrowingInfoService, times(1)).returnBook("1");
        verify(borrowingInfoService, times(1)).returnBook("2");
        verify(userService, times(1)).deleteUser("n00b");
    }
}

