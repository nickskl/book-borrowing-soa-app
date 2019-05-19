package ru.nikolay.remote;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import ru.nikolay.auth.ServiceCredentials;
import ru.nikolay.auth.ServiceTokens;
import ru.nikolay.requests.BookRequest;
import ru.nikolay.requests.BorrowingInfoRequest;
import ru.nikolay.requests.StorageRequest;
import ru.nikolay.requests.UserRequest;
import ru.nikolay.service.gateway.web.model.BookComposite;
import ru.nikolay.service.gateway.web.model.BorrowingInfoComposite;
import ru.nikolay.service.gateway.web.model.StorageComposite;
import ru.nikolay.service.gateway.web.model.UserComposite;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Component
public class RemoteGatewayServiceImpl implements RemoteGatewayService {
    final static private String defaultUrl = "http://localhost:8090/";

    final private RemoteServiceImpl<BookRequest, BookComposite> bookService;
    final private RemoteServiceImpl<StorageRequest, StorageComposite> storageService;
    final private RemoteServiceImpl<BorrowingInfoRequest, BorrowingInfoComposite> borrowingService;

    @Autowired
    private ServiceCredentials myCredentials;

    @Autowired
    @Qualifier(value = "gatewayTokens")
    private ServiceTokens gatewayTokens;

    public RemoteGatewayServiceImpl() {
        bookService = new RemoteServiceImpl<>(defaultUrl, myCredentials, gatewayTokens, BookComposite.class,
                BookComposite[].class);
        storageService = new RemoteServiceImpl<>(defaultUrl, myCredentials, gatewayTokens, StorageComposite.class,
                StorageComposite[].class);
        borrowingService = new RemoteServiceImpl<>(defaultUrl, myCredentials, gatewayTokens,
                BorrowingInfoComposite.class, BorrowingInfoComposite[].class);
    }

    @Override
    public Page<BookComposite> getBooksPaged(Pageable pageable) {
        Page<HashMap<String, Object>> page = bookService.findAllPaged("book", pageable);
        List<HashMap<String, Object>> content = page.getContent();
        List<BookComposite> responses = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        for(HashMap<String, Object> map : content) {
            responses.add(objectMapper.convertValue(map, BookComposite.class));
        }
        return new PageImpl<BookComposite>(responses, pageable, page.getTotalElements());
    }

    @Override
    public BookComposite getBook(String bookId) {
        return bookService.findOne("book/{id}", bookId);
    }

    @Override
    public BookComposite createBook(String title, String author, String description) {
        return bookService.create("book", new BookRequest(title, author, description));
    }

    @Override
    public void updateBook(String bookId, String title, String author, String description) {
        bookService.update("book/{bookId}", new BookRequest(title, author, description), bookId);
    }

    @Override
    public void deleteBook(String bookId) {
        bookService.delete("book/{bookId}", bookId);
    }

    @Override
    public BorrowingInfoComposite borrowBook(String userId, String bookId, String storageId, Date borrowedDate,
                                      Date dateToReturnBook) {
        return borrowingService.create("borrow", new BorrowingInfoRequest(userId, bookId, storageId, borrowedDate,
                dateToReturnBook));
    }

    @Override
    public StorageComposite getStorage(String storageId) {
        return storageService.findOne("storage/{id}", storageId);
    }

    @Override
    public StorageComposite createStorage(String location) {
        return storageService.create("/storage", new StorageRequest(location, new ArrayList<>()));
    }

    @Override
    public Page<StorageComposite> getAllStoragesPaged(Pageable pageable) {
        Page<HashMap<String, Object>> page = bookService.findAllPaged("storage", pageable);
        List<HashMap<String, Object>> content = page.getContent();
        List<StorageComposite> responses = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        for(HashMap<String, Object> map : content) {
            responses.add(objectMapper.convertValue(map, StorageComposite.class));
        }
        return new PageImpl<StorageComposite>(responses, pageable, page.getTotalElements());
    }

    @Override
    public boolean isAuthenticated(String token) {
        RestTemplate rt = new RestTemplate();
        try {
            ResponseEntity<Void> response = rt.postForEntity(defaultUrl+"/user/authenticated", token, Void.class);
            return true;
        } catch (RestClientException e) {
            return false;
        }
    }

    @Override
    public boolean isAdmin(String token) {
        RestTemplate rt = new RestTemplate();
        try {
            ResponseEntity<Boolean> response = rt.getForEntity(defaultUrl+"/user/isAdmin/" + token, Boolean.class);
            return response.getBody();
        } catch (RestClientException e) {
            return false;
        }
    }

    @Override
    public TokenPair refreshToken(String refreshToken) {
        RestTemplate rt = new RestTemplate();
        try {
            ResponseEntity<TokenPair> response = rt.postForEntity(defaultUrl+"/user/refresh",
                    refreshToken, TokenPair.class);

            return response.getBody();
        } catch (RestClientException e) {
            return null;
        }
    }

    @Override
    public int countBookBorrowings() {
        RestTemplate rt = new RestTemplate();
        try {
            return rt.getForEntity("http://localhost:8090/statistics/booksBorrowedCount", Integer.class).getBody();
        } catch (RestClientException e) {
            throw new RemoteServiceAccessException("Statistics service not available", "http://localhost:8090/statistics/booksBorrowedCount");
        }
    }

    @Override
    public int countStoragesViewed() {
        RestTemplate rt = new RestTemplate();
        try {
            return rt.getForEntity("http://localhost:8090/statistics/storagesViewedTodayCount", Integer.class).getBody();
        } catch (RestClientException e) {
            throw new RemoteServiceAccessException("Statistics service not available", "http://localhost:8090/statistics/storagesViewedTodayCount");
        }
    }

    @Override
    public int countBookViewed() {
        RestTemplate rt = new RestTemplate();
        try {
            return rt.getForEntity("http://localhost:8090/statistics/booksViewedTodayCount", Integer.class).getBody();
        } catch (RestClientException e) {
            throw new RemoteServiceAccessException("Statistics service not available", "http://localhost:8090/statistics/booksViewedTodayCount");
        }
    }
}
