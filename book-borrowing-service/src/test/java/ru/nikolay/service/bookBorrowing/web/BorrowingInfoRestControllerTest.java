package ru.nikolay.service.bookBorrowing.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.nikolay.remote.RemoteServiceException;
import ru.nikolay.requests.BorrowingInfoRequest;
import ru.nikolay.responses.BorrowingInfoResponse;
import ru.nikolay.service.bookBorrowing.domain.BorrowingInfo;
import ru.nikolay.service.bookBorrowing.service.BookCurrentlyNotAvailableException;
import ru.nikolay.service.bookBorrowing.service.BorrowingInfoService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(BorrowingInfoRestController.class)
public class BorrowingInfoRestControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BorrowingInfoService borrowingInfoService;

    @Test
    public void getExistingBorrowingInfoTest() throws Exception {
        BorrowingInfo expectedBorrowingInfoToFind = new BorrowingInfo()
                .setId("0")
                .setUserId("1234")
                .setBookId("4321")
                .setStorageId("1111")
                .setBorrowedDate(new Date(12345))
                .setDateToReturnBook(new Date(54321));
        BorrowingInfoResponse response = expectedBorrowingInfoToFind.toResponse();
        given(borrowingInfoService.getById("0")).willReturn(expectedBorrowingInfoToFind);

        mvc.perform(get("/borrow/0").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedBorrowingInfoToFind.getId())))
                .andExpect(jsonPath("$.bookId", is(expectedBorrowingInfoToFind.getBookId())))
                .andExpect(jsonPath("$.userId", is(expectedBorrowingInfoToFind.getUserId())))
                .andExpect(jsonPath("$.storageId", is(expectedBorrowingInfoToFind.getStorageId())));
    }

    @Test
    public void getNonexistentBorrowingInfoReturnsErrorTest() throws Exception {
        given(borrowingInfoService.getById("0")).willThrow(new NullPointerException("Borrowing info not found"));

        mvc.perform(get("/borrow/0").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getBorrowingInfoByUserIdTest() throws Exception {
        List<BorrowingInfo> expectedBorrowingInfoToFind = new ArrayList<>();
        expectedBorrowingInfoToFind.add(new BorrowingInfo()
                .setId("0")
                .setUserId("1234")
                .setBookId("4321")
                .setStorageId("1111")
                .setBorrowedDate(new Date(12345))
                .setDateToReturnBook(new Date(54321)));
        expectedBorrowingInfoToFind.add(new BorrowingInfo()
                .setId("10")
                .setUserId("1234")
                .setBookId("14321")
                .setStorageId("11111")
                .setBorrowedDate(new Date(12345))
                .setDateToReturnBook(new Date(54321)));
        given(borrowingInfoService.getByUserId("1234")).willReturn(expectedBorrowingInfoToFind);

        mvc.perform(get("/borrow?userId=1234").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(expectedBorrowingInfoToFind.get(0).getId())))
                .andExpect(jsonPath("$[0].userId", is(expectedBorrowingInfoToFind.get(0).getUserId())))
                .andExpect(jsonPath("$[0].bookId", is(expectedBorrowingInfoToFind.get(0).getBookId())))
                .andExpect(jsonPath("$[0].storageId", is(expectedBorrowingInfoToFind.get(0).getStorageId())))
                .andExpect(jsonPath("$[1].id", is(expectedBorrowingInfoToFind.get(1).getId())))
                .andExpect(jsonPath("$[1].userId", is(expectedBorrowingInfoToFind.get(1).getUserId())))
                .andExpect(jsonPath("$[1].bookId", is(expectedBorrowingInfoToFind.get(1).getBookId())))
                .andExpect(jsonPath("$[1].storageId", is(expectedBorrowingInfoToFind.get(1).getStorageId())));
    }

    @Test
    public void getBorrowingInfoByBookIdTest() throws Exception {
        List<BorrowingInfo> expectedBorrowingInfoToFind = new ArrayList<>();
        expectedBorrowingInfoToFind.add(new BorrowingInfo()
                .setId("0")
                .setUserId("11234")
                .setBookId("4321")
                .setStorageId("1111")
                .setBorrowedDate(new Date(12345))
                .setDateToReturnBook(new Date(54321)));
        expectedBorrowingInfoToFind.add(new BorrowingInfo()
                .setId("10")
                .setUserId("1234")
                .setBookId("4321")
                .setStorageId("11111")
                .setBorrowedDate(new Date(12345))
                .setDateToReturnBook(new Date(54321)));
        given(borrowingInfoService.getByBookId("4321")).willReturn(expectedBorrowingInfoToFind);

        mvc.perform(get("/borrow?bookId=4321").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(expectedBorrowingInfoToFind.get(0).getId())))
                .andExpect(jsonPath("$[0].userId", is(expectedBorrowingInfoToFind.get(0).getUserId())))
                .andExpect(jsonPath("$[0].bookId", is(expectedBorrowingInfoToFind.get(0).getBookId())))
                .andExpect(jsonPath("$[0].storageId", is(expectedBorrowingInfoToFind.get(0).getStorageId())))
                .andExpect(jsonPath("$[1].id", is(expectedBorrowingInfoToFind.get(1).getId())))
                .andExpect(jsonPath("$[1].userId", is(expectedBorrowingInfoToFind.get(1).getUserId())))
                .andExpect(jsonPath("$[1].bookId", is(expectedBorrowingInfoToFind.get(1).getBookId())))
                .andExpect(jsonPath("$[1].storageId", is(expectedBorrowingInfoToFind.get(1).getStorageId())));
    }

    @Test
    public void addNewBorrowingInfoTest() throws Exception {
        BorrowingInfo expectedBorrowingInfoToAdd = new BorrowingInfo()
                .setId("0")
                .setUserId("1234")
                .setBookId("4321")
                .setStorageId("1111")
                .setBorrowedDate(new Date(12345))
                .setDateToReturnBook(new Date(54321));
        BorrowingInfoRequest request = new BorrowingInfoRequest("1234", "4321", "1111",
                new Date(12345), new Date(54321));
        given(borrowingInfoService.borrowBook(request)).willReturn(expectedBorrowingInfoToAdd);

        mvc.perform(post("/borrow")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/borrow/0"));
    }

    @Test
    public void failedToBorrowBookReturnsErrorTest() throws Exception {
        BorrowingInfoRequest request = new BorrowingInfoRequest("1234", "4321", "1111",
                new Date(12345), new Date(54321));
        given(borrowingInfoService.borrowBook(request)).willThrow(new BookCurrentlyNotAvailableException("Ooops!"));

        mvc.perform(post("/borrow")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void remoteServiceFailedReturnsErrorTest() throws Exception {
        BorrowingInfoRequest request = new BorrowingInfoRequest("1234", "4321", "1111",
                new Date(12345), new Date(54321));
        given(borrowingInfoService.borrowBook(request)).willThrow(new RemoteServiceException("Ooops!"));

        mvc.perform(post("/borrow")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void deleteBorrowingInfoTest() throws Exception {
        mvc.perform(delete("/borrow/0")).andExpect(status().isNoContent());
    }

    @Test
    public void onGenericExceptionTest() throws Exception {
        given(borrowingInfoService.getById(anyString())).willThrow(new RuntimeException("Some kind of error"));

        mvc.perform(get("/borrow/12345").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }
}
