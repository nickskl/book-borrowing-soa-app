package ru.nikolay.service.bookBorrowing.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nikolay.remote.RemoteServiceException;
import ru.nikolay.remote.RemoteStorageService;
import ru.nikolay.remote.RemoteStorageServiceImpl;
import ru.nikolay.responses.BookInformationResponse;
import ru.nikolay.service.bookBorrowing.domain.BorrowingInfo;
import ru.nikolay.service.bookBorrowing.repository.BorrowingInfoRepository;
import ru.nikolay.requests.BorrowingInfoRequest;

import java.util.List;

@Service
public class BorrowingInfoServiceImpl implements BorrowingInfoService {
    @Autowired
    private BorrowingInfoRepository borrowingInfoRepository;

    @Autowired
    private RemoteStorageService storageService;

    @Override
    @Transactional(readOnly = true)
    public BorrowingInfo getById(String id) {
        BorrowingInfo borrowingInfo = borrowingInfoRepository.findOne(id);
        if(borrowingInfo == null) {
            throw new NullPointerException("BorrowingInfo[" + id + "] not found in the database");
        }
        return borrowingInfo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowingInfo> getByBookId(String bookId) {
        return borrowingInfoRepository.findAllByBookId(bookId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BorrowingInfo> getByUserId(String userId) {
        return borrowingInfoRepository.findAllByUserId(userId);
    }

    @Override
    @Transactional
    public BorrowingInfo borrowBook(BorrowingInfoRequest borrowingInfoRequest) {
        BookInformationResponse bookInformation = storageService.getBookInformationForStorageAndBookId(
                borrowingInfoRequest.getStorageId(), borrowingInfoRequest.getBookId());
        if(bookInformation.getNumberLeft() == 0) {
            throw new BookCurrentlyNotAvailableException("Requested book is not available at the selected storage");
        }
        if(borrowingInfoRequest.getDateToReturnBook().before(borrowingInfoRequest.getBorrowedDate())) {
            throw new IllegalArgumentException("Illegal date to return book");
        }

        BorrowingInfo borrowingInfo = new BorrowingInfo()
                .setBookId(borrowingInfoRequest.getBookId())
                .setStorageId(borrowingInfoRequest.getStorageId())
                .setUserId(borrowingInfoRequest.getUserId())
                .setBorrowedDate(borrowingInfoRequest.getBorrowedDate())
                .setDateToReturnBook(borrowingInfoRequest.getDateToReturnBook());

        storageService.updateBookInformationInStorage(borrowingInfoRequest.getStorageId(), bookInformation.getId(),
                    borrowingInfoRequest.getBookId(), bookInformation.getNumberLeft() - 1);

        return borrowingInfoRepository.save(borrowingInfo);
    }

    @Override
    @Transactional
    public void returnBook(String borrowingId) {
        BorrowingInfo borrowingInfo = borrowingInfoRepository.findOne(borrowingId);
        if(borrowingInfo == null) return;
        BookInformationResponse bookInformation = storageService.getBookInformationForStorageAndBookId(
                borrowingInfo.getStorageId(), borrowingInfo.getBookId());
        storageService.updateBookInformationInStorage(borrowingInfo.getStorageId(), bookInformation.getId(),
                borrowingInfo.getBookId(),bookInformation.getNumberLeft() + 1);
        borrowingInfoRepository.delete(borrowingId);
    }
}
