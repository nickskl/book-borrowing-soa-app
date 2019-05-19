package ru.nikolay.service.statistics.statistics;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.nikolay.service.statistics.repository.BookBorrowingEventRepository;
import ru.nikolay.service.statistics.repository.BookViewedEventRepository;
import ru.nikolay.service.statistics.repository.StorageViewedEventRepository;
import ru.nikolay.statistics.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.Semaphore;

@Component
public class Statistics {
    private static final Logger logger = LoggerFactory.getLogger(Statistics.class);
    private ObjectMapper objectMapper = new ObjectMapper();

    private MessageHistory messageHistory = new MessageHistory();
    @Autowired
    BookBorrowingEventRepository bookBorrowingEventRepository;

    @Autowired
    StorageViewedEventRepository storageViewedEventRepository;

    @Autowired
    BookViewedEventRepository bookViewedEventRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "bookBorrowedQueue")
    public void recieveBookBorrowed(StatisticsItem in) {
        BookBorrowingEvent bookBorrowingEvent;
        logger.info("Statistics: received [" + in + "]");
        if (messageHistory.messageExists(in.getId())) {
            logger.info("Statistics: event [" + in.getId() + "] already processed");
        } else {
            try {
                BookBorrowingEventRequest request = objectMapper.readValue(in.data, BookBorrowingEventRequest.class);
                bookBorrowingEvent = new BookBorrowingEvent();
                validateBookBorrowingRequest(request);
                bookBorrowingEvent.setBookId(request.getBookId());
                bookBorrowingEvent.setStorageId(request.getStorageId());
                bookBorrowingEventRepository.save(bookBorrowingEvent);
                addToProcessedEvents(in.getId());
            } catch (Exception ex) {
                sendError(in.getId());
            }
        }
        sendAcknowledge(in.getId());
    }

    private void validateBookBorrowingRequest(BookBorrowingEventRequest request) {
        if (request.getBookId() == null) {
            logger.error("Error: validation of book borrowing event request failed[BookId == null]");
            throw new IllegalArgumentException("Error: validation of book borrowing event request failed[BookId == null]");
        }
        if (Objects.equals(request.getBookId().trim(), "")) {
            logger.error("Error: validation of book borrowing event request failed[BookId is empty]");
            throw new IllegalArgumentException("Error: validation of book borrowing event request failed[BookId is empty]");
        }
    }

    @RabbitListener(queues = "bookViewedQueue")
    public void recieveBookViewed(StatisticsItem in) {
        BookDetailsViewedEvent bookViewedEvent;
        logger.info("Statistics: received [" + in + "]");
        if (messageHistory.messageExists(in.getId())) {
            logger.info("Statistics: event [" + in.getId() + "] already processed");
        } else {
            try {
                BookDetailsViewedEventRequest request = objectMapper.readValue(in.data, BookDetailsViewedEventRequest.class);
                bookViewedEvent = new BookDetailsViewedEvent();
                validateBookDetailsViewedEventRequest(request);
                bookViewedEvent.setBookId(request.getBookId());
                bookViewedEvent.setTimestamp(request.getTimestamp());
                bookViewedEventRepository.save(bookViewedEvent);
                addToProcessedEvents(in.getId());
            } catch (Exception ex) {
                sendError(in.getId());
            }
        }
        sendAcknowledge(in.getId());
    }

    private void validateBookDetailsViewedEventRequest(BookDetailsViewedEventRequest request) {
        if (request.getBookId() == null) {
            logger.error("Error: validation of book details viewed event request failed[BookId == null]");
            throw new IllegalArgumentException("Error: validation of book details viewed event request failed[BookId == null]");
        }
        if (Objects.equals(request.getBookId().trim(), "")) {
            logger.error("Error: validation of book details viewed event request failed[BookId is empty]");
            throw new IllegalArgumentException("Error: validation of book details viewed event request failed[BookId is empty]");
        }
        if(request.getTimestamp() == null) {
            logger.error("Error: validation of book details viewed event request failed[Timestamp == null]");
            throw new IllegalArgumentException("Error: validation of book details viewed event request failed[Timestamp == null]");
        }
        if(Objects.equals(request.getBookId(), "5a3e9a3e368a1e15c4f4071b")) {
            logger.error("Error: validation of book details viewed event request failed[test]");
            throw new IllegalArgumentException("Error: validation of book details viewed event request failed[test]");
        }
    }

    @RabbitListener(queues = "storageViewedQueue")
    public void recieveStorageViewed(StatisticsItem in) {
        StorageDetailsViewedEvent storageDetailsViewedEvent;
        logger.info("Statistics: received [" + in + "]");
        if (messageHistory.messageExists(in.getId())) {
            logger.info("Statistics: event [" + in.getId() + "] already processed");
        } else {
            try {
                StorageDetailsViewedEventRequest request = objectMapper.readValue(in.data, StorageDetailsViewedEventRequest.class);
                storageDetailsViewedEvent = new StorageDetailsViewedEvent();
                validateStorageDetailsViewedEventRequest(request);
                storageDetailsViewedEvent.setStorageId(request.getStorageId());
                storageDetailsViewedEvent.setTimestamp(request.getTimestamp());
                storageViewedEventRepository.save(storageDetailsViewedEvent);
                addToProcessedEvents(in.getId());
            } catch (Exception ex) {
                sendError(in.getId());
            }
        }
        sendAcknowledge(in.getId());
    }

    private void validateStorageDetailsViewedEventRequest(StorageDetailsViewedEventRequest request) {
        if (request.getStorageId() == null) {
            logger.error("Error: validation of storage details viewed event request failed[StorageId == null]");
            throw new IllegalArgumentException("Error: validation of storage details viewed event request failed[StorageId == null]");
        }
        if (Objects.equals(request.getStorageId().trim(), "")) {
            logger.error("Error: validation of storage details viewed event request failed[StorageId is empty]");
            throw new IllegalArgumentException("Error: validation of storage details viewed event request failed[StorageId is empty]");
        }
        if(request.getTimestamp() == null) {
            logger.error("Error: validation of storage details viewed event request failed[Timestamp == null]");
            throw new IllegalArgumentException("Error: validation of storage borrowing event request failed[Timestamp == null]");
        }
    }

    private void sendAcknowledge(int id) {
        rabbitTemplate.convertAndSend("rsoi.statistics.status", "confirmation", id);
    }

    private void sendError(int id) {
        rabbitTemplate.convertAndSend("rsoi.statistics.status", "error", id);
    }

    private void addToProcessedEvents(int id) {
        logger.info("Statistics: acknowledged id[" + id + "]" + " from hash tables");
        messageHistory.add(id);
    }

    @Scheduled(fixedDelay = 100, initialDelay = 100)
    public void scanForExpired() {
        messageHistory.removeExpired();
    }
}
