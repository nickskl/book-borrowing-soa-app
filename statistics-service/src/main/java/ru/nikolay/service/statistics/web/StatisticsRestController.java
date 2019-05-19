package ru.nikolay.service.statistics.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.WebUtils;
import ru.nikolay.remote.RemoteGatewayService;
import ru.nikolay.service.statistics.repository.BookBorrowingEventRepository;
import ru.nikolay.service.statistics.repository.BookViewedEventRepository;
import ru.nikolay.service.statistics.repository.StorageViewedEventRepository;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.xml.crypto.Data;
import java.util.Date;

@RestController
@RequestMapping("/statistics")
public class StatisticsRestController {
    @Autowired
    BookBorrowingEventRepository bookBorrowingEventRepository;

    @Autowired
    StorageViewedEventRepository storageViewedEventRepository;

    @Autowired
    BookViewedEventRepository bookViewedEventRepository;

    @Autowired
    RemoteGatewayService gatewayService;

    @GetMapping("/booksBorrowedCount")
    public long CountBooksBorrowed() {
        return bookBorrowingEventRepository.count();
    }

    @GetMapping("/storagesViewedTodayCount")
    public long CountStoragesViewed() {
        Date today;
        today = new Date();
        today.setHours(0);
        today.setMinutes(0);
        today.setSeconds(0);
        return storageViewedEventRepository.findAllByTimestampIsGreaterThanEqual(today).size();
    }

    @GetMapping("/booksViewedTodayCount")
    public long CountBooksViewed() {
        Date today = new Date();
        today.setHours(0);
        today.setMinutes(0);
        today.setSeconds(0);
        return bookViewedEventRepository.findAllByTimestampIsGreaterThanEqual(today).size();
    }
}
