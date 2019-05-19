package ru.nikolay.remote;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.nikolay.auth.ServiceCredentials;
import ru.nikolay.auth.ServiceTokens;
import ru.nikolay.requests.BookInformationRequest;
import ru.nikolay.requests.StorageRequest;
import ru.nikolay.responses.BookInformationResponse;
import ru.nikolay.responses.StorageResponse;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

@Component
public class RemoteStorageServiceImpl implements RemoteStorageService {
    final static private String defaultUrl = "http://localhost:8086/";

    final private RemoteServiceImpl<StorageRequest, StorageResponse> storageService;

    final private RemoteServiceImpl<BookInformationRequest, BookInformationResponse> bookInformationService;

    @Autowired
    private ServiceCredentials myCredentials;

    @Autowired
    @Qualifier(value = "storageTokens")
    private ServiceTokens storageTokens;

    public RemoteStorageServiceImpl() {
        String baseUrl;

        Properties prop = new Properties();
        String propFileName = "configuration.properties";
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);

            if (inputStream != null) {
                prop.load(inputStream);
            } else {
                throw new FileNotFoundException("property file '" + propFileName + "' not found in the classpath");
            }
            baseUrl = prop.getProperty("urls.services.storage");
        }
        catch (Exception e) {
            baseUrl = defaultUrl;
        }

        storageService = new RemoteServiceImpl<StorageRequest, StorageResponse>(baseUrl, myCredentials, storageTokens, StorageResponse.class,
                StorageResponse[].class);
        bookInformationService = new RemoteServiceImpl<BookInformationRequest, BookInformationResponse>(baseUrl,
                myCredentials, storageTokens, BookInformationResponse.class, BookInformationResponse[].class);
    }

    @Override
    public BookInformationResponse addBookInformationToStorage(String storageId, String bookId,
                                                               Integer numberOfBooksLeft) {
        return bookInformationService.create("storage/{storageId}/books",
                new BookInformationRequest(bookId, numberOfBooksLeft), storageId);
    }

    @Override
    public Page<StorageResponse> getAllStoragesPaged(Pageable pageable) {
        Page<HashMap<String, Object>> page = storageService.findAllPaged("storage", pageable);
        List<HashMap<String, Object>> content = page.getContent();
        List<StorageResponse> responses = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        for(HashMap<String, Object> map : content) {
            responses.add(objectMapper.convertValue(map, StorageResponse.class));
        }
        return new PageImpl<StorageResponse>(responses, pageable, page.getTotalElements());
    }

    @Override
    public List<StorageResponse> findStorageByBook(String bookId) {
        return storageService.findAll("storage?bookId={bookId}", bookId);
    }

    @Override
    public BookInformationResponse getBookInformationForStorage(String storageId, String bookInformationId) {
        return bookInformationService.findOne("storage/{storageId}/books/{bookId}", storageId, bookInformationId);
    }

    @Override
    public BookInformationResponse getBookInformationForStorageAndBookId(String storageId, String bookId) {
        return bookInformationService.findOne("storage/{storageId}/books?bookId={bookId}", storageId, bookId);
    }

    @Override
    public void updateBookInformationInStorage(String storageId, String bookInformationId,
                                               String bookId, Integer numberOfBooksLeft) {
        bookInformationService.update("storage/{storageId}/books/{bookInformationId}",
                new BookInformationRequest(bookId, numberOfBooksLeft), storageId, bookInformationId);
    }

    @Override
    public List<BookInformationResponse> getAllBookInformationForStorage(String storageId) {
        return bookInformationService.findAll("storage/{storageId}/books/", storageId);
    }

    @Override
    public Page<BookInformationResponse> getAllBookInformationForStoragePaged(String storageId, Pageable pageable) {
        Page<HashMap<String, Object>> page = bookInformationService.findAllPaged("storage/{storageId}/books/",
                pageable, storageId);
        List<HashMap<String, Object>> content = page.getContent();
        List<BookInformationResponse> responses = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        for(HashMap<String, Object> map : content) {
            responses.add(objectMapper.convertValue(map, BookInformationResponse.class));
        }
        return new PageImpl<BookInformationResponse>(responses, pageable, page.getTotalElements());
    }

    @Override
    public StorageResponse createStorage(String location) {
        return storageService.create("/storage", new StorageRequest(location, new ArrayList<>()));
    }

    @Override
    public StorageResponse getStorage(String storageId) {
        return storageService.findOne("storage/{storageId}/", storageId);
    }

    @Override
    public void deleteBookInformationFromStorage(String storageId, String bookInformationId) {
        bookInformationService.delete("storage/{storageId}/books/{bookInformationId}",
                storageId, bookInformationId);
    }

    @Override
    public void deleteBookInformationFromStorageByBookId(String storageId, String bookId) {
        bookInformationService.delete("storage/{storageId}/books?bookId={bookId}", storageId, bookId);
    }

    @Override
    public void deleteStorage(String storageId) {
        storageService.delete("storage/{storageId}", storageId);
    }
}
