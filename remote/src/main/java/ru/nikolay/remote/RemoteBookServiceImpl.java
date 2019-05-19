package ru.nikolay.remote;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import ru.nikolay.auth.ServiceCredentials;
import ru.nikolay.auth.ServiceTokens;
import ru.nikolay.requests.BookRequest;
import ru.nikolay.responses.BookResponse;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

@Component
public class RemoteBookServiceImpl implements RemoteBookService {
    final static private String defaultUrl = "http://localhost:8085/";

    final private RemoteServiceImpl<BookRequest, BookResponse> bookService;

    @Autowired
    private ServiceCredentials myCredentials;

    @Autowired
    @Qualifier(value = "bookTokens")
    private ServiceTokens bookTokens;

    public RemoteBookServiceImpl() {
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
            baseUrl = prop.getProperty("urls.services.book");
        }
        catch (Exception e) {
            baseUrl = defaultUrl;
        }
        bookService = new RemoteServiceImpl<BookRequest, BookResponse>(baseUrl, myCredentials, bookTokens, BookResponse.class,
                BookResponse[].class);
    }

    @Override
    public Page<BookResponse> getBooksPaged(Pageable pageable) {
        Page<HashMap<String, Object>> page = bookService.findAllPaged("book", pageable);
        List<HashMap<String, Object>> content = page.getContent();
        List<BookResponse> responses = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        for(HashMap<String, Object> map : content) {
            responses.add(objectMapper.convertValue(map, BookResponse.class));
        }
        return new PageImpl<BookResponse>(responses, pageable, page.getTotalElements());
    }

    @Override
    public BookResponse getBook(String bookId) {
        return bookService.findOne("book/{id}", bookId);
    }

    @Override
    public BookResponse createBook(String title, String author, String description) {
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
}
