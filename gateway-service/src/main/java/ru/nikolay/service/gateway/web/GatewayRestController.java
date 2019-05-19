package ru.nikolay.service.gateway.web;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.nikolay.remote.*;
import ru.nikolay.requests.*;
import ru.nikolay.responses.*;
import ru.nikolay.service.gateway.queue.TaskQueue;
import ru.nikolay.service.gateway.statistics.Statistics;
import ru.nikolay.service.gateway.web.model.*;
import ru.nikolay.statistics.BookBorrowingEventRequest;
import ru.nikolay.statistics.BookDetailsViewedEventRequest;
import ru.nikolay.statistics.StorageDetailsViewedEventRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
public class GatewayRestController {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionController.class);

    @Autowired
    RemoteStorageService storageService;

    @Autowired
    RemoteBookService bookService;

    @Autowired
    RemoteBorrowingInfoService borrowingInfoService;

    @Autowired
    RemoteUserService userService;

    @Autowired
    @Qualifier("retryRequestQueue")
    TaskQueue queue;

    @Autowired
    @Qualifier("statisticsQueue")
    TaskQueue statisticsQueue;

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    Statistics statistics;

    @RequestMapping(value = "/book", method = RequestMethod.GET)
    public Page<BookComposite> getBooks(Pageable pageable) {
        logger.debug("Gateway: getting all books with page[" + pageable + "]");
        return bookService.getBooksPaged(pageable).map(BookComposite::fromResponse);
    }

    @RequestMapping(value = "/book/{bookId}", method = RequestMethod.GET)
    public BookComposite getBookById(@PathVariable String bookId) {
        logger.debug("Gateway: getting book with id[" + bookId + "]");
        BookResponse response = bookService.getBook(bookId);
        statisticsQueue.addTask(() -> {
            BookDetailsViewedEventRequest eventRequest = new BookDetailsViewedEventRequest(bookId, new Date());
            String jsonEventRequest;
            try {
                jsonEventRequest = objectMapper.writeValueAsString(eventRequest);
            } catch (JsonProcessingException ex) {
                logger.error("Error: failed to convert request to json");
                return true;
            }
            statistics.sendMessage(jsonEventRequest, "bookViewed");
            return true;
        });

        return BookComposite.fromResponse(response);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/book", method = RequestMethod.POST)
    public BookComposite createBook(@RequestBody BookRequest bookRequest, HttpServletResponse response) {
        logger.debug("Gateway: creating book with request[" + bookRequest + "]");
        BookResponse bookResponse = bookService.createBook(bookRequest.getTitle(),
                bookRequest.getAuthor(), bookRequest.getDescription());
        response.addHeader(HttpHeaders.LOCATION, "/book/" + bookResponse.getId());
        return BookComposite.fromResponse(bookResponse);
    }

    @RequestMapping(value = "/book/{bookId}", method = RequestMethod.PATCH)
    public void updateBook(@PathVariable String bookId, @RequestBody BookRequest bookRequest) {
        logger.debug("Gateway: updating book with id["+ bookId + " ] and request[" + bookRequest + "]");
        bookService.updateBook(bookId, bookRequest.getTitle(), bookRequest.getAuthor(), bookRequest.getDescription());
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/book/{bookId}", method = RequestMethod.DELETE)
    public void deleteBook(@PathVariable String bookId) {
        logger.debug("Gateway: deleting book with id[" + bookId + "]");
        List<String> storageIds = storageService.findStorageByBook(bookId)
                .stream()
                .map(StorageResponse::getId)
                .collect(Collectors.toList());

        for (String storageId : storageIds) {
                storageService.deleteBookInformationFromStorageByBookId(storageId, bookId);
        }

        List<String> borrowingInfoIds = borrowingInfoService
                .getAllBorrowedBookInfoByBook(bookId)
                .stream()
                .map(BorrowingInfoResponse::getId)
                .collect(Collectors.toList());
        for (String borrowingInfoId : borrowingInfoIds) {
            borrowingInfoService.returnBook(borrowingInfoId);
        }
        bookService.deleteBook(bookId);
    }

    @RequestMapping(value = "/storage", method = RequestMethod.GET)
    public Page<StorageComposite> getStorages(Pageable pageable) {
        logger.debug("Gateway: getting all storages with page[" + pageable + "]");
        return storageService.getAllStoragesPaged(pageable).map(StorageComposite::fromResponse);
    }

    @RequestMapping(value = "/storage/{storageId}", method = RequestMethod.GET)
    public StorageComposite getStorageById(@PathVariable String storageId) {
        logger.debug("Gateway: getting storage with id[" + storageId + "]");
        statisticsQueue.addTask(() -> {
            StorageDetailsViewedEventRequest eventRequest = new StorageDetailsViewedEventRequest(storageId, new Date());
            String jsonEventRequest;
            try {
                jsonEventRequest = objectMapper.writeValueAsString(eventRequest);
            } catch (JsonProcessingException ex) {
                logger.error("Error: failed to convert request to json");
                return true;
            }
            statistics.sendMessage(jsonEventRequest, "storageViewed");
            return true;
        });

        return StorageComposite.fromResponse(storageService.getStorage(storageId));
    }

    @RequestMapping(value = "/storage/{storageId}/books/{bookInformationId}", method = RequestMethod.GET)
    public BookInformationComposite getBookInformationByStorageAndBookId(@PathVariable String storageId,
                                                                         @PathVariable String bookInformationId) {
        logger.debug("Gateway: getting book information with id["+ bookInformationId +
                "] for storage with id[" + storageId + "]");
        BookInformationResponse response = storageService.getBookInformationForStorage(storageId, bookInformationId);
        return BookInformationComposite.fromResponse(response, bookService.getBook(response.getBookId()));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/storage", method = RequestMethod.POST)
    public StorageComposite createStorage(@RequestBody StorageRequest storageRequest, HttpServletResponse response) {
        logger.debug("Gateway: creating storage with request[" + storageRequest + "]");
        StorageResponse storageResponse = storageService.createStorage(storageRequest.getLocation());
        response.addHeader(HttpHeaders.LOCATION, "/storage/" + storageResponse.getId());
        return StorageComposite.fromResponse(storageResponse);
    }

    @RequestMapping(value = "/borrow", method = RequestMethod.GET, params = "bookId")
    public List<BorrowingInfoComposite> getBorrowingInfoForBookId(@Param(value = "bookId") String bookId) {
        logger.debug("Gateway: getting book borrowing information for book with id["+ bookId + "]");
        List<BorrowingInfoResponse> borrowingInfoResponses = borrowingInfoService.getAllBorrowedBookInfoByBook(bookId);
        List<BorrowingInfoComposite> result = new ArrayList<>();
        for(BorrowingInfoResponse borrowingInfoResponse : borrowingInfoResponses) {
            UserResponse userResponse = null;
            StorageResponse storageResponse;
            BookResponse bookResponse;

            try {
                storageResponse = storageService.getStorage(borrowingInfoResponse.getStorageId());
            }
            catch (Exception e) {
                storageResponse = null;
            }

            try {
                bookResponse = bookService.getBook(borrowingInfoResponse.getBookId());
            }
            catch (Exception e) {
                bookResponse = null;
            }

            result.add(BorrowingInfoComposite.fromResponse(borrowingInfoResponse,
                    userResponse, bookResponse, storageResponse));
        }

        return result;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/borrow", method = RequestMethod.POST)
    public BorrowingInfoComposite createBorrowingInfo(@RequestBody BorrowingInfoRequest request,
                                                                  HttpServletResponse response) {
        logger.debug("Gateway: creating borrowing info with request["+ request + "]");
        BorrowingInfoResponse borrowingInfoResponse = borrowingInfoService.borrowBook(request.getUserId(),
                request.getBookId(), request.getStorageId(), request.getBorrowedDate(), request.getDateToReturnBook());
        response.addHeader(HttpHeaders.LOCATION, "/borrow/" + borrowingInfoResponse.getId());
        statisticsQueue.addTask(() -> {
            BookBorrowingEventRequest eventRequest = new BookBorrowingEventRequest(request.getStorageId(), request.getBookId());
            String jsonEventRequest;
            try {
                jsonEventRequest = objectMapper.writeValueAsString(eventRequest);
            } catch (JsonProcessingException ex) {
                logger.error("Error: failed to convert request to json");
                return true;
            }
            statistics.sendMessage(jsonEventRequest, "bookBorrowed");
            return true;
        });
        return BorrowingInfoComposite.fromResponse(borrowingInfoResponse, null,
                null, null);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/user/authenticated", method = RequestMethod.POST)
    public ResponseEntity<Void> isAuthenticated(@RequestBody String token) {
        if (!userService.isAuthenticated(token)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.OK)
    @RequestMapping(value = "/user/refresh", method = RequestMethod.POST)
    public ResponseEntity<TokenPair> refresh(@RequestBody String token) {
        TokenPair tokens = userService.refreshToken(token);
        if (tokens == null) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return new ResponseEntity<>(tokens, HttpStatus.OK);
    }

    @GetMapping("/user/isAdmin/{token}")
    public boolean isAdmin(@PathVariable String token) {
        return userService.isAdmin(token);
    }

    private RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/statistics/booksBorrowedCount")
    public long CountBooksBorrowed() {
        return restTemplate.getForEntity("http://localhost:8100/statistics/booksBorrowedCount", Integer.class).getBody();
    }

    @GetMapping("/statistics/storagesViewedTodayCount")
    public long CountStoragesViewed() {
        return restTemplate.getForEntity("http://localhost:8100/statistics/storagesViewedTodayCount", Integer.class).getBody();
    }

    @GetMapping("/statistics/booksViewedTodayCount")
    public long CountBooksViewed() {
        return restTemplate.getForEntity("http://localhost:8100/statistics/booksViewedTodayCount", Integer.class).getBody();
    }
}
