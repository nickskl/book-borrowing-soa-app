package ru.nikolay.service.bookBorrowing.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import ru.nikolay.remote.RemoteStorageService;
import ru.nikolay.requests.BorrowingInfoRequest;
import ru.nikolay.responses.BookInformationResponse;
import ru.nikolay.service.bookBorrowing.domain.BorrowingInfo;
import ru.nikolay.service.bookBorrowing.repository.BorrowingInfoRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class BorrowingInfoServiceImplTest {
    @MockBean
    private BorrowingInfoRepository mockedBorrowingInfoRepository;

    @MockBean
    private RemoteStorageService mockedRemoteStorageRepository;

    @Autowired
    private BorrowingInfoService borrowingInfoService;

    @Test
    public void getBorrowingInfoByIdTest() {
        BorrowingInfo expectedBorrowingInfoToFind = new BorrowingInfo()
                .setId("0")
                .setUserId("1234")
                .setBookId("4321")
                .setStorageId("1111")
                .setBorrowedDate(new Date(12345))
                .setDateToReturnBook(new Date(54321));
        given(mockedBorrowingInfoRepository.findOne("0")).willReturn(expectedBorrowingInfoToFind);

        BorrowingInfo actualBorrowingInfoFound = borrowingInfoService.getById("0");

        assertEquals(expectedBorrowingInfoToFind, actualBorrowingInfoFound);
    }

    @Test(expected = NullPointerException.class)
    public void getBorrowingInfoByIdThrowsOnNonexistentBorrowingInfoTest() {
        given(mockedBorrowingInfoRepository.findOne(anyString())).willReturn(null);
        borrowingInfoService.getById("0");
    }

    @Test
    public void getBorrowingInfoByUserIdTest() {
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

        given(mockedBorrowingInfoRepository.findAllByUserId("1234")).willReturn(expectedBorrowingInfoToFind);

        List<BorrowingInfo> actualBorrowingInfoFound = borrowingInfoService.getByUserId("1234");

        assertEquals(expectedBorrowingInfoToFind, actualBorrowingInfoFound);
    }

    @Test
    public void getBorrowingInfoByBookIdTest() {
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
                .setUserId("11234")
                .setBookId("4321")
                .setStorageId("11111")
                .setBorrowedDate(new Date(12345))
                .setDateToReturnBook(new Date(54321)));

        given(mockedBorrowingInfoRepository.findAllByBookId("4321")).willReturn(expectedBorrowingInfoToFind);

        List<BorrowingInfo> actualBorrowingInfoFound = borrowingInfoService.getByBookId("4321");

        assertEquals(expectedBorrowingInfoToFind, actualBorrowingInfoFound);
    }


    @Test
    public void borrowBookTest() {
        BorrowingInfo expectedBorrowingInfoToBeAdded = new BorrowingInfo()
                .setUserId("1234")
                .setBookId("4321")
                .setStorageId("1111")
                .setBorrowedDate(new Date(12345))
                .setDateToReturnBook(new Date(54321));
        BookInformationResponse bookInformationResponse = new BookInformationResponse("333", "4321", 10);
        BorrowingInfoRequest request = new BorrowingInfoRequest("1234","4321", "1111",
                new Date(12345), new Date(54321));
        given(mockedBorrowingInfoRepository.save(expectedBorrowingInfoToBeAdded))
                .willReturn(expectedBorrowingInfoToBeAdded);
        given(mockedRemoteStorageRepository.getBookInformationForStorage("1111", "4321"))
                .willReturn(bookInformationResponse);
        BorrowingInfo actualBorrowingInfoAdded = borrowingInfoService.borrowBook(request);

        assertEquals("1234", actualBorrowingInfoAdded.getUserId());
        assertEquals("4321", actualBorrowingInfoAdded.getBookId());
        assertEquals("1111", actualBorrowingInfoAdded.getStorageId());
        assertEquals(new Date(12345), actualBorrowingInfoAdded.getBorrowedDate());
        assertEquals(new Date(54321), actualBorrowingInfoAdded.getDateToReturnBook());

        verify(mockedBorrowingInfoRepository, times(1)).save(expectedBorrowingInfoToBeAdded);
    }

    @Test(expected = BookCurrentlyNotAvailableException.class)
    public void borrowBookThrowsOnBookNotAvaliableTest() {
        BorrowingInfo expectedBorrowingInfoToBeAdded = new BorrowingInfo()
                .setUserId("1234")
                .setBookId("4321")
                .setStorageId("1111")
                .setBorrowedDate(new Date(12345))
                .setDateToReturnBook(new Date(54321));
        BookInformationResponse bookInformationResponse = new BookInformationResponse("333", "4321", 0);
        BorrowingInfoRequest request = new BorrowingInfoRequest("1234","4321", "1111",
                new Date(12345), new Date(54321));
        given(mockedBorrowingInfoRepository.save(expectedBorrowingInfoToBeAdded))
                .willReturn(expectedBorrowingInfoToBeAdded);
        given(mockedRemoteStorageRepository.getBookInformationForStorage("1111", "4321"))
                .willReturn(bookInformationResponse);
        borrowingInfoService.borrowBook(request);
    }

    @Test
    public void returnBookTest() {
        borrowingInfoService.returnBook("12345");
        verify(mockedBorrowingInfoRepository, times(1)).delete("12345");
    }
}
