package ru.nikolay.service.bookBorrowing.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.nikolay.service.bookBorrowing.domain.BorrowingInfo;
import ru.nikolay.service.bookBorrowing.service.BorrowingInfoService;
import ru.nikolay.requests.BorrowingInfoRequest;
import ru.nikolay.responses.BorrowingInfoResponse;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/borrow")
public class
BorrowingInfoRestController {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionController.class);

    @Autowired
    BorrowingInfoService borrowingInfoService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public BorrowingInfoResponse getBorrowingInfo(@PathVariable String id) {
        logger.debug("Borrow: getting borrowing information with id[" + id + "]");
        return borrowingInfoService.getById(id).toResponse();
    }

    @RequestMapping(method = RequestMethod.GET, params = "userId")
    public List<BorrowingInfoResponse> getBorrowingInfoByUser(@RequestParam(value = "userId") String id) {
        logger.debug("Borrow: getting all borrowing information for user with id[" + id + "]");
        return borrowingInfoService.
                getByUserId(id).
                stream().
                map(BorrowingInfo::toResponse).
                collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.GET, params = "bookId")
    public List<BorrowingInfoResponse> getBorrowingInfoByBook(@RequestParam(value = "bookId") String id) {
        logger.debug("Borrow: getting all borrowing information for book with id[" + id + "]");
        return borrowingInfoService.
                getByBookId(id).
                stream().
                map(BorrowingInfo::toResponse).
                collect(Collectors.toList());
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(method = RequestMethod.POST)
    public BorrowingInfoResponse borrowBook(@Valid @RequestBody BorrowingInfoRequest borrowingInfoRequest,
                                            HttpServletResponse response) {
        logger.debug("Borrow: creating borrowing information with request[" + borrowingInfoRequest + "]");
        BorrowingInfo borrowingInfo = borrowingInfoService.borrowBook(borrowingInfoRequest);
        response.addHeader(HttpHeaders.LOCATION, "/borrow/" + borrowingInfo.getId());
        return borrowingInfo.toResponse();
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void returnBook(@PathVariable String id) {
        logger.debug("Borrow: deleting borrowing information with id[" + id + "]");
        borrowingInfoService.returnBook(id);
    }
}
