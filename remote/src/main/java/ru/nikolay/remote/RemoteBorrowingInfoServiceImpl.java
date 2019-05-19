package ru.nikolay.remote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.nikolay.auth.ServiceCredentials;
import ru.nikolay.auth.ServiceTokens;
import ru.nikolay.requests.BorrowingInfoRequest;
import ru.nikolay.responses.BorrowingInfoResponse;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;

@Component
public class RemoteBorrowingInfoServiceImpl implements RemoteBorrowingInfoService {
    final static private String defaultUrl = "http://localhost:8084/";

    final private RemoteServiceImpl<BorrowingInfoRequest, BorrowingInfoResponse> borrowingService;

    @Autowired
    private ServiceCredentials myCredentials;

    @Autowired
    @Qualifier(value = "bookBorrowingTokens")
    private ServiceTokens bookTokens;

    public RemoteBorrowingInfoServiceImpl() {
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
            baseUrl = prop.getProperty("urls.services.borrowing");
        }
        catch (Exception e) {
            baseUrl = defaultUrl;
        }
        borrowingService = new RemoteServiceImpl<BorrowingInfoRequest, BorrowingInfoResponse>(baseUrl,
                myCredentials, bookTokens, BorrowingInfoResponse.class, BorrowingInfoResponse[].class);
    }

    @Override
    public BorrowingInfoResponse getBorrowingInfo(String borrowingInfoId) {
        return borrowingService.findOne("borrow/{borrowingInfoId}/", borrowingInfoId);
    }

    @Override
    public List<BorrowingInfoResponse> getAllBorrowedBookInfoByBook(String bookId) {
        return borrowingService.findAll("borrow?bookId={bookId}", bookId);
    }

    @Override
    public List<BorrowingInfoResponse> getAllBorrowedBookInfoByUser(String userId) {
        return borrowingService.findAll("borrow?userId={userId}", userId);
    }

    @Override
    public BorrowingInfoResponse borrowBook(String userId, String bookId, String storageId,
                                            Date borrowedDate, Date dateToReturnBook) {
        return borrowingService.create("borrow", new BorrowingInfoRequest(userId, bookId, storageId, borrowedDate,
                dateToReturnBook));
    }

    @Override
    public void returnBook(String borrowingInfoId) {
        borrowingService.delete("borrow/{borrowingInfoId}", borrowingInfoId);
    }
}
