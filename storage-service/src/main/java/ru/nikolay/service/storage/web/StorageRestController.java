package ru.nikolay.service.storage.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.nikolay.service.storage.domain.BookInformation;
import ru.nikolay.service.storage.domain.Storage;
import ru.nikolay.service.storage.service.StorageService;
import ru.nikolay.requests.BookInformationRequest;
import ru.nikolay.responses.BookInformationResponse;
import ru.nikolay.requests.StorageRequest;
import ru.nikolay.responses.StorageResponse;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/storage")
public class StorageRestController {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionController.class);

    @Autowired
    private StorageService storageService;

    @RequestMapping(method = RequestMethod.GET)
    public Page<Storage> getAllStoragePaged(Pageable pageable) {
        logger.debug("Storage: getting all storages with page[" + pageable + "]");
        return storageService.getAllStoragesPaged(pageable);
    }

    @RequestMapping(method = RequestMethod.GET, params = "bookId")
    public List<Storage> getStoragesByBookId(@RequestParam(value = "bookId") String bookId) {
        logger.debug("Storage: getting all storages with bookId[" + bookId + "]");
        return storageService.findStoragesByBookId(bookId);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public StorageResponse getStorageById(@PathVariable String id) {
        logger.debug("Storage: getting storage with id[" + id + "]");
        return storageService.getById(id).toResponse();
    }

    @RequestMapping(value = "/{id}/books", method = RequestMethod.GET)
    public List<BookInformationResponse> getBooksFromStorageByStorageId(@PathVariable String id) {
        logger.debug("Storage: getting all books for storage with id[" + id + "]");
        return storageService.getBookInformationForStorageId(id).
                stream().
                map(BookInformation::toResponse).
                collect(Collectors.toList());
    }

    @RequestMapping(value = "/{storageId}/books/{bookInformationId}", method = RequestMethod.GET)
    public BookInformationResponse getBookInformationByBookInformationIdFromStorage(@PathVariable String storageId,
                                                                          @PathVariable String bookInformationId) {
        logger.debug("Storage: getting book information with id[" + bookInformationId +
                "] for storage with id[" + storageId + "]");
        return storageService.getBookInformationForStorageIdAndBookInformationId(storageId, bookInformationId)
                .toResponse();
    }

    @RequestMapping(value = "/{storageId}/books", method = RequestMethod.GET, params = "bookId")
    public BookInformationResponse getBookInformationByBookIdFromStorage(@PathVariable String storageId,
                                                                         @RequestParam("bookId") String bookId) {
        logger.debug("Storage: getting book with id[" + bookId +
                "] for storage with id[" + storageId + "]");
        return storageService.getBookInformationForStorageIdAndBookId(storageId, bookId)
                .toResponse();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public StorageResponse addStorage(@Valid @RequestBody StorageRequest storageRequest, HttpServletResponse response) {
        logger.debug("Storage: creating storage with request[" + storageRequest + "]");
        Storage storage = storageService.add(storageRequest);
        response.addHeader(HttpHeaders.LOCATION, "/storage/" + storage.getId());
        return storage.toResponse();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteStorage(@PathVariable String id) {
        logger.debug("Storage: deleting storage with id[" + id + "]");
        storageService.delete(id);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/{id}/books", method = RequestMethod.POST)
    public BookInformationResponse AddBookInformationToStorage(@PathVariable String id,
                                            @Valid @RequestBody BookInformationRequest bookInformationRequest,
                                            HttpServletResponse response) {
        logger.debug("Storage: creating a book information with request[" + bookInformationRequest + "] " +
                "for storage with id[" + id + "]");
        BookInformation bookInformation = storageService.addBookInformationForStorageId(id, bookInformationRequest);
        response.addHeader(HttpHeaders.LOCATION, "/storage/" + id + "/books/" + bookInformation.getId());
        return  bookInformation.toResponse();
    }

    @RequestMapping(value = "/{storageId}/books/{bookInformationId}", method = RequestMethod.PATCH)
    public BookInformationResponse updateBookInformationByBookIdFromStorage(@PathVariable String storageId,
                                                    @PathVariable String bookInformationId,
                                                    @Valid @RequestBody BookInformationRequest bookInformationRequest) {
        logger.debug("Storage: updating a book information with id [" + bookInformationId +"] and request[" +
                bookInformationRequest + "] for storage with id[" + storageId + "]");
        return storageService.updateBookInformationForStorageId(storageId, bookInformationId,
                bookInformationRequest).toResponse();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{storageId}/books/{bookIndormationId}", method = RequestMethod.DELETE)
    public void deleteBookInformationByBookIdFromStorage(@PathVariable String storageId,
                                                         @PathVariable String bookInformationId) {
        logger.debug("Storage: deleting a book information with id [" + bookInformationId +"] "
                + "for storage with id[" + storageId + "]");
        storageService.deleteBookInformationForStorageId(storageId, bookInformationId);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{storageId}/books", method = RequestMethod.DELETE, params = "bookId")
    public void deleteBookInformationByBookIdFromStorageByBookId(@PathVariable String storageId,
                                                         @RequestParam(value = "bookId") String bookId) {
        logger.debug("Storage: deleting a book information with bookId [" + bookId +"] "
                + "for storage with id[" + storageId + "]");
        storageService.deleteBookInformationForStorageIdByBookId(storageId, bookId);
    }
}
