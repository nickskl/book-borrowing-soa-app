package ru.nikolay.service.storage.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ru.nikolay.requests.BookInformationRequest;
import ru.nikolay.requests.StorageRequest;
import ru.nikolay.service.storage.domain.BookInformation;
import ru.nikolay.service.storage.domain.Storage;
import ru.nikolay.service.storage.service.StorageService;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(StorageRestController.class)
public class StorageRestControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private StorageService storageService;

    @Test
    public void getStoragesByBookIdTest() throws Exception {
        List<String> bookInfoIds = new ArrayList<>();
        bookInfoIds.add("111");
        bookInfoIds.add("121");
        bookInfoIds.add("133");

        List<Storage> storages = new ArrayList<>();
        storages.add(new Storage("1", "here-and-there", bookInfoIds));
        storages.add(new Storage("2", "there", bookInfoIds));

        given(storageService.findStoragesByBookId("12345")).willReturn(storages);

        mvc.perform(get("/storage?bookId=12345").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(storages.get(0).getId())))
                .andExpect(jsonPath("$[0].location", is(storages.get(0).getLocation())))
                .andExpect(jsonPath("$[1].id", is(storages.get(1).getId())))
                .andExpect(jsonPath("$[1].location", is(storages.get(1).getLocation())));
    }

    @Test
    public void getStorageByIdTest() throws Exception {
        Storage storageToGet = new Storage("121212", "pizza", new ArrayList<>());

        given(storageService.getById("121212")).willReturn(storageToGet);

        mvc.perform(get("/storage/121212").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(storageToGet.getId())))
                .andExpect(jsonPath("$.location", is(storageToGet.getLocation())));
    }

    @Test
    public void getNonexistentStorageReturnsErrorTest() throws Exception {
        given(storageService.getById("121212")).willThrow(new NullPointerException("Ooops!"));
        mvc.perform(get("/storage/121212").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAllBookIdsForStorageTest() throws Exception {
        List<BookInformation> bookInformationList = new ArrayList<>();
        bookInformationList.add(new BookInformation().setId("1111").setBookId("1").setNumberLeft(10));
        bookInformationList.add(new BookInformation().setId("1112").setBookId("11").setNumberLeft(1));
        bookInformationList.add(new BookInformation().setId("1113").setBookId("111").setNumberLeft(0));

        given(storageService.getBookInformationForStorageId("121212")).willReturn(bookInformationList);

        mvc.perform(get("/storage/121212/books").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(bookInformationList.get(0).getId())))
                .andExpect(jsonPath("$[0].bookId", is(bookInformationList.get(0).getBookId())))
                .andExpect(jsonPath("$[1].numberLeft", is(bookInformationList.get(1).getNumberLeft())))
                .andExpect(jsonPath("$[2].id", is(bookInformationList.get(2).getId())));
    }

    @Test
    public void getBooksFromNonexistentStorageReturnsErrorTest() throws Exception {
        given(storageService.getBookInformationForStorageId("121212")).willThrow(new NullPointerException("Ooops!"));
        mvc.perform(get("/storage/121212/books").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getBookInformationByBookIdFromStorageTest() throws Exception {
        BookInformation bookInformation = new BookInformation().setId("1").setBookId("222").setNumberLeft(0);
        given(storageService.getBookInformationForStorageIdAndBookInformationId("121212",
                "1")).willReturn(bookInformation);
        mvc.perform(get("/storage/121212/books/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookInformation.getId())))
                .andExpect(jsonPath("$.bookId", is(bookInformation.getBookId())))
                .andExpect(jsonPath("$.numberLeft", is(bookInformation.getNumberLeft())));
    }

    @Test
    public void getBookInformationByBookIdFromStorageReturnsErrorOnNotFoundTest() throws Exception {
        given(storageService.getBookInformationForStorageIdAndBookInformationId("121212", "0"))
                .willThrow(new NullPointerException("Ooops!"));
        mvc.perform(get("/storage/121212/books/0").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void addStorageTest() throws Exception {
        Storage storageToAdd = new Storage()
                .setId("123456")
                .setLocation("farawaysland")
                .setBookInformationResponseIds(new ArrayList<>());
        StorageRequest request = new StorageRequest("farawaysland", new ArrayList<>());

        given(storageService.add(request)).willReturn(storageToAdd);

        mvc.perform(post("/storage").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/storage/123456"));
    }

    @Test
    public void deleteStorageTest() throws Exception {
        mvc.perform(delete("/storage/0")).andExpect(status().isNoContent());
    }

    @Test
    public void addBookInfoToStorageTest() throws Exception {
        BookInformation bookInformationToAdd = new BookInformation()
                .setId("123456")
                .setBookId("farawaysland")
                .setNumberLeft(1);
        BookInformationRequest request = new BookInformationRequest("farawaysland", 1);

        given(storageService.addBookInformationForStorageId("11", request)).willReturn(bookInformationToAdd);

        mvc.perform(post("/storage/11/books").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/storage/11/books/123456"));
    }

    @Test
    public void addBookInfoToStorageReturnsErrorOnNotFoundTest() throws Exception {
        BookInformationRequest request = new BookInformationRequest("farawaysland", 1);

        given(storageService.addBookInformationForStorageId("11", request))
                .willThrow(new NullPointerException("Ooops!"));

        mvc.perform(post("/storage/11/books").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateBookInformationByBookIdFromStorageTest() throws Exception {
        BookInformation bookInformationToUpdate = new BookInformation()
                .setId("123456")
                .setBookId("farawaysland")
                .setNumberLeft(1);
        BookInformationRequest request = new BookInformationRequest("farawaysland", 1);
        given(storageService.updateBookInformationForStorageId("1", "123456", request))
                .willReturn(bookInformationToUpdate);

        mvc.perform(patch("/storage/1/books/123456")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    public void updateBookInformationByBookIdFromStorageReturnsErrorOnNullTest() throws Exception {
        BookInformationRequest request = new BookInformationRequest("farawaysland", 1);
        given(storageService.updateBookInformationForStorageId("1", "123456", request))
                .willThrow(new NullPointerException("Oooops!"));

        mvc.perform(patch("/storage/1/books/123456")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void updateBookInformationByBookIdFromStorageReturnsErrorOnIllegalArgumentTest() throws Exception {
        BookInformationRequest request = new BookInformationRequest("farawaysland", 1);
        given(storageService.updateBookInformationForStorageId("1", "123456", request))
                .willThrow(new IllegalArgumentException("Oooops!"));

        mvc.perform(patch("/storage/1/books/123456")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void onGenericExceptionTest() throws Exception {
        given(storageService.getById(anyString())).willThrow(new RuntimeException("Some kind of error"));

        mvc.perform(get("/storage/12345").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    public void deleteBookInformationFromStorageByBookIdTest() throws Exception {
        mvc.perform(delete("/storage/0/books?bookId=1")).andExpect(status().isNoContent());
    }
}