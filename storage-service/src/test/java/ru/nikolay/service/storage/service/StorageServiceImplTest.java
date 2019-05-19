package ru.nikolay.service.storage.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.nikolay.requests.BookInformationRequest;
import ru.nikolay.requests.StorageRequest;
import ru.nikolay.service.storage.domain.BookInformation;
import ru.nikolay.service.storage.domain.Storage;
import ru.nikolay.service.storage.repository.BookInformationRepository;
import ru.nikolay.service.storage.repository.StorageRepository;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class StorageServiceImplTest {
    @MockBean
    private StorageRepository mockedStorageRepository;

    @MockBean
    private BookInformationRepository mockedBookInformationRepository;

    @Autowired
    private StorageServiceImpl storageService;

    @Test
    public void getStorageByIdTest() {
        List<String> bookInfoIds = new ArrayList<>();
        bookInfoIds.add("1111");
        bookInfoIds.add("111");
        bookInfoIds.add("11111");
        bookInfoIds.add("11");

        Storage expectedUserToFind = new Storage()
                .setId("0")
                .setLocation("0xBADC0DE")
                .setBookInformationResponseIds(bookInfoIds);
        given(mockedStorageRepository.findOne("0")).willReturn(expectedUserToFind);

        Storage actualUserFound = storageService.getById("0");

        assertEquals(expectedUserToFind, actualUserFound);
    }

    @Test(expected = NullPointerException.class)
    public void getUserByIdThrowsOnNonexistentUserTest() {
        given(mockedStorageRepository.findOne(anyString())).willReturn(null);
        storageService.getById("0");
    }

    @Test
    public void getAllStoragesPagedTest() {
        PageRequest pageRequest = new PageRequest(1, 2);
        List<String> bookInfoIds = new ArrayList<>();
        bookInfoIds.add("1111");
        bookInfoIds.add("111");
        bookInfoIds.add("11111");
        bookInfoIds.add("11");
        List<Storage> expectedStorages = new ArrayList<>();
        expectedStorages .add(new Storage("0", "location-0", bookInfoIds));
        expectedStorages .add(new Storage("1", "location-1", new ArrayList<>()));
        given(mockedStorageRepository.findAll(pageRequest)).
                willReturn(new PageImpl<Storage>(expectedStorages, pageRequest, 20));

        Page<Storage> actualStorages = storageService.getAllStoragesPaged(pageRequest);

        assertThat(actualStorages).containsExactlyElementsOf(expectedStorages);
    }

    @Test
    public void findStoragesForBookIdTest() {
        List<BookInformation> bookInformationList = new ArrayList<>();
        bookInformationList.add(new BookInformation("09090", 12).setId("111"));
        List<String> bookInfoIds = new ArrayList<>();
        bookInfoIds.add("1111");
        bookInfoIds.add("111");
        List<Storage> expectedStorages = new ArrayList<>();
        expectedStorages .add(new Storage("0", "location-0", bookInfoIds));
        expectedStorages .add(new Storage("1", "location-1", bookInfoIds));

        given(mockedBookInformationRepository.findAllByBookId("09090")).willReturn(bookInformationList);
        given(mockedStorageRepository.findAllByBookInformationResponseIdsIn(bookInfoIds.subList(1,2)))
                .willReturn(expectedStorages);

        List<Storage> actualStorages = storageService.findStoragesByBookId("09090");

        assertThat(actualStorages).containsExactlyElementsOf(expectedStorages);
    }

    @Test
    public void getBookInfoForStorageTest() {
        List<BookInformation> expectedBookInformationList = new ArrayList<>();
        expectedBookInformationList.add(new BookInformation("09090", 12).setId("111"));
        expectedBookInformationList.add(new BookInformation("qwerty", 1).setId("1111"));
        List<String> bookInfoIds = new ArrayList<>();
        bookInfoIds.add("1111");
        bookInfoIds.add("111");

        Storage storage = new Storage().setLocation("somewhere").setBookInformationResponseIds(bookInfoIds);

        given(mockedStorageRepository.findOne("12345")).willReturn(storage);
        given(mockedBookInformationRepository.findAll(bookInfoIds)).willReturn(expectedBookInformationList);

        List<BookInformation> actualBookInformationList = storageService.getBookInformationForStorageId("12345");

        assertThat(actualBookInformationList).containsExactlyElementsOf(expectedBookInformationList);
    }

    @Test(expected = NullPointerException.class)
    public void getBookInfoForStorageThrowsOnNullStorageTest() {
        given(mockedStorageRepository.findOne("12345")).willReturn(null);
        storageService.getBookInformationForStorageId("12345");
    }

    @Test
    public void getBookInfoForStorageIdAndBookInfoIdTest() {
        BookInformation expectedBookInformation = new BookInformation()
                .setId("0")
                .setBookId("12345")
                .setNumberLeft(123);
        List<String> bookInfoIds = new ArrayList<>();
        bookInfoIds.add("0");

        Storage storage = new Storage().setLocation("somewhere").setBookInformationResponseIds(bookInfoIds);

        given(mockedStorageRepository.findOne("121")).willReturn(storage);
        given(mockedBookInformationRepository.findOne("0")).willReturn(expectedBookInformation);

        BookInformation actualBookInformation = storageService
                .getBookInformationForStorageIdAndBookInformationId("121", "0");

        assertEquals(expectedBookInformation, actualBookInformation);
    }

    @Test(expected = NullPointerException.class)
    public void getBookInfoForStorageIdAndBookInfoIdThrowsOnNullStorageTest() {
        given(mockedStorageRepository.findOne("121")).willReturn(null);
        storageService.getBookInformationForStorageIdAndBookInformationId("121", "0");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getBookInfoForStorageIdAndBookInfoIdThrowsOnNotFoundBookInfoIdInStorageTest() {
        List<String> bookInfoIds = new ArrayList<>();

        Storage storage = new Storage().setLocation("somewhere").setBookInformationResponseIds(bookInfoIds);

        given(mockedStorageRepository.findOne("121")).willReturn(storage);
        storageService.getBookInformationForStorageIdAndBookInformationId("121", "0");
    }

    @Test(expected = NullPointerException.class)
    public void getBookInfoForStorageIdAndBookInfoIdThrowsOnNoexistentBookInfoTest() {
        List<String> bookInfoIds = new ArrayList<>();
        bookInfoIds.add("0");

        Storage storage = new Storage().setLocation("somewhere").setBookInformationResponseIds(bookInfoIds);

        given(mockedStorageRepository.findOne("121")).willReturn(storage);
        given(mockedBookInformationRepository.findOne("0")).willReturn(null);

        storageService.getBookInformationForStorageIdAndBookInformationId("121", "0");
    }

    @Test
    public void addStorageNoBookInfoIdsTest() {
        List<String> bookInfoIds = new ArrayList<>();
        Storage expectedStorageToAdd = new Storage()
                .setLocation("somewhere")
                .setBookInformationResponseIds(bookInfoIds);
        StorageRequest request = new StorageRequest("somewhere", null);

        given(mockedStorageRepository.save(expectedStorageToAdd)).willReturn(expectedStorageToAdd);

        Storage actualStorageAdded = storageService.add(request);

        assertEquals(expectedStorageToAdd.getLocation(), actualStorageAdded.getLocation());
        assertThat(actualStorageAdded.getBookInformationResponseIds())
                .containsExactlyElementsOf(expectedStorageToAdd.getBookInformationResponseIds());

        verify(mockedStorageRepository, times(1)).save(expectedStorageToAdd);
    }

    @Test
    public void addStorageTest() {
        BookInformation bookInformation = new BookInformation()
                .setBookId("1")
                .setNumberLeft(10);
        BookInformation returnedBookInformation = new BookInformation()
                .setId("111")
                .setBookId("1")
                .setNumberLeft(10);
        List<BookInformationRequest> bookInformationRequest = new ArrayList<>();
        bookInformationRequest.add(new BookInformationRequest("1", 10));

        List<String> bookInfoIds = new ArrayList<>();
        bookInfoIds.add("111");

        Storage expectedStorageToAdd = new Storage()
                .setLocation("somewhere")
                .setBookInformationResponseIds(bookInfoIds);
        StorageRequest request = new StorageRequest("somewhere", bookInformationRequest);

        given(mockedBookInformationRepository.save(bookInformation)).willReturn(returnedBookInformation);
        given(mockedStorageRepository.save(expectedStorageToAdd)).willReturn(expectedStorageToAdd);

        Storage actualStorageAdded = storageService.add(request);

        assertEquals(expectedStorageToAdd, actualStorageAdded);
        verify(mockedStorageRepository, times(1)).save(expectedStorageToAdd);
        verify(mockedBookInformationRepository, times(1)).save(bookInformation);
    }

    @Test
    public void deleteStorageTest() {
        List<String> bookInfoIds = new ArrayList<>();
        bookInfoIds.add("111");

        Storage storageToDelete = new Storage()
                .setId("Hello world!")
                .setLocation("somewhere")
                .setBookInformationResponseIds(bookInfoIds);
        given(mockedStorageRepository.findOne("Hello world!")).willReturn(storageToDelete);

        storageService.delete("Hello world!");
        verify(mockedStorageRepository, times(1)).delete("Hello world!");
        verify(mockedBookInformationRepository, times(1)).delete("111");
    }

    @Test
    public void deleteAlreadyDeletedStorageTest() {
        given(mockedStorageRepository.findOne("Hello world!")).willReturn(null);

        storageService.delete("Hello world!");
        verify(mockedStorageRepository, times(0)).delete("Hello world!");
    }

    @Test
    public void addBookInfoForStorage() {
        BookInformation bookInformation = new BookInformation()
                .setBookId("12345")
                .setNumberLeft(123);
        BookInformation expectedBookInformation = new BookInformation()
                .setId("0")
                .setBookId("12345")
                .setNumberLeft(123);
        List<String> bookInfoIds = new ArrayList<>();

        BookInformationRequest request = new BookInformationRequest("12345", 123);

        Storage storage = new Storage().setLocation("somewhere").setBookInformationResponseIds(bookInfoIds);
        bookInfoIds.add("0");
        Storage updatedStorage = new Storage().setLocation("somewhere").setBookInformationResponseIds(bookInfoIds);

        given(mockedStorageRepository.findOne("121")).willReturn(storage);
        given(mockedBookInformationRepository.save(bookInformation)).willReturn(expectedBookInformation);
        given(mockedStorageRepository.save(updatedStorage)).willReturn(updatedStorage);

        BookInformation actualBookInformation = storageService.addBookInformationForStorageId("121", request);

        assertEquals(expectedBookInformation, actualBookInformation);
        verify(mockedStorageRepository, times(1)).save(updatedStorage);
        verify(mockedBookInformationRepository, times(1)).save(bookInformation);
    }

    @Test(expected = NullPointerException.class)
    public void addBookInfoForStorageThrowsOnNullStorageTest() {
        given(mockedStorageRepository.findOne("121")).willReturn(null);
        BookInformationRequest request = new BookInformationRequest("12345", 123);
        storageService.addBookInformationForStorageId("121", request);
    }

    @Test
    public void updateBookInfoForStorage() {
        BookInformation bookInformation = new BookInformation()
                .setId("0")
                .setBookId("12345")
                .setNumberLeft(123);
        BookInformation expectedBookInformation = new BookInformation()
                .setId("0")
                .setBookId("12")
                .setNumberLeft(1);
        List<String> bookInfoIds = new ArrayList<>();

        BookInformationRequest request = new BookInformationRequest("12", 1);

        Storage storage = new Storage().setLocation("somewhere").setBookInformationResponseIds(bookInfoIds);
        bookInfoIds.add("0");
        Storage updatedStorage = new Storage().setLocation("somewhere").setBookInformationResponseIds(bookInfoIds);

        given(mockedStorageRepository.findOne("121")).willReturn(storage);
        given(mockedBookInformationRepository.findOne("0")).willReturn(bookInformation);
        given(mockedBookInformationRepository.save(expectedBookInformation)).willReturn(expectedBookInformation);

        BookInformation actualBookInformation = storageService.updateBookInformationForStorageId("121",
                "0", request);

        assertEquals(expectedBookInformation, actualBookInformation);
        verify(mockedBookInformationRepository, times(1)).save(bookInformation);
    }

    @Test(expected = NullPointerException.class)
    public void updateBookInfoForStorageThrowsOnNullStorageTest() {
        given(mockedStorageRepository.findOne("121")).willReturn(null);
        BookInformationRequest request = new BookInformationRequest("12345", 123);
        storageService.updateBookInformationForStorageId("121", "0", request);
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateBookInfoForStorageThrowsOnStorageDoesNotContainBookInfoToUpdateTest() {
        List<String> bookInfoIds = new ArrayList<>();
        Storage storage = new Storage().setLocation("somewhere").setBookInformationResponseIds(bookInfoIds);
        bookInfoIds.add("1111");

        given(mockedStorageRepository.findOne("121")).willReturn(storage);

        BookInformationRequest request = new BookInformationRequest("12345", 123);
        storageService.updateBookInformationForStorageId("121", "0", request);
    }

    @Test(expected = NullPointerException.class)
    public void updateBookInfoForStorageThrowsOnBookInfoDoesNotExistTest() {
        List<String> bookInfoIds = new ArrayList<>();
        Storage storage = new Storage().setLocation("somewhere").setBookInformationResponseIds(bookInfoIds);
        bookInfoIds.add("0");

        given(mockedStorageRepository.findOne("121")).willReturn(storage);
        given(mockedBookInformationRepository.findOne("0")).willReturn(null);

        BookInformationRequest request = new BookInformationRequest("12345", 123);
        storageService.updateBookInformationForStorageId("121", "0", request);
    }

    @Test
    public void deleteBookInformationForStorageTest() {
        List<String> bookInfoIds = new ArrayList<>();
        bookInfoIds.add("111");
        List<String> newBookInfoIds = new ArrayList<>();

        Storage storageToUpdate = new Storage()
                .setId("Hello world!")
                .setLocation("somewhere")
                .setBookInformationResponseIds(bookInfoIds);

        Storage updatedStorage = new Storage()
                .setId("Hello world!")
                .setLocation("somewhere")
                .setBookInformationResponseIds(newBookInfoIds);

        given(mockedStorageRepository.findOne("Hello world!")).willReturn(storageToUpdate);
        given(mockedStorageRepository.save(updatedStorage)).willReturn(updatedStorage);

        storageService.deleteBookInformationForStorageId("Hello world!", "111");
        verify(mockedBookInformationRepository, times(1)).delete("111");
        verify(mockedStorageRepository, times(1)).save(updatedStorage);
    }

    @Test(expected = NullPointerException.class)
    public void deleteBookInfoForStorageThrowsOnNullStorageTest() {
        given(mockedStorageRepository.findOne("121")).willReturn(null);
        storageService.deleteBookInformationForStorageId("121", "0");
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteBookInfoForStorageThrowsOnNoSuchBookInfoInStorageTest() {
        List<String> bookInfoIds = new ArrayList<>();
        bookInfoIds.add("111");
        bookInfoIds.add("112");

        Storage storageToUpdate = new Storage()
                .setId("Hello world!")
                .setLocation("somewhere")
                .setBookInformationResponseIds(bookInfoIds);

        given(mockedStorageRepository.findOne("121")).willReturn(storageToUpdate);
        storageService.deleteBookInformationForStorageId("121", "0");
    }

    @Test
    public void deleteBookInformationForStorageByBookIdTest() {
        List<String> bookInfoIds = new ArrayList<>();
        bookInfoIds.add("111");
        List<String> newBookInfoIds = new ArrayList<>();

        Storage storageToUpdate = new Storage()
                .setId("Hello world!")
                .setLocation("somewhere")
                .setBookInformationResponseIds(bookInfoIds);

        Storage updatedStorage = new Storage()
                .setId("Hello world!")
                .setLocation("somewhere")
                .setBookInformationResponseIds(newBookInfoIds);

        List<BookInformation> bookInformationList = new ArrayList<>();
        bookInformationList.add(new BookInformation().setId("111").setBookId("asdfgh").setNumberLeft(12));
        bookInformationList.add(new BookInformation().setId("122").setBookId("asdfgh").setNumberLeft(2));

        given(mockedStorageRepository.findOne("Hello world!")).willReturn(storageToUpdate);
        given(mockedBookInformationRepository.findAllByBookId("asdfgh")).willReturn(bookInformationList);
        given(mockedStorageRepository.save(updatedStorage)).willReturn(updatedStorage);

        storageService.deleteBookInformationForStorageIdByBookId("Hello world!", "asdfgh");
        verify(mockedBookInformationRepository, times(1)).delete("111");
        verify(mockedBookInformationRepository, times(0)).delete("122");
        verify(mockedStorageRepository, times(1)).save(updatedStorage);
    }

    @Test(expected = NullPointerException.class)
    public void deleteBookInformationForStorageByBookIdThrowsOnNullStorageTest() {
        given(mockedStorageRepository.findOne("121")).willReturn(null);
        storageService.deleteBookInformationForStorageIdByBookId("121", "0");
    }

}
