package ru.nikolay.service.gateway.statistics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.nikolay.statistics.StatisticsItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class Statistics {
    private static final Logger logger = LoggerFactory.getLogger(Statistics.class);
    private ObjectMapper objectMapper = new ObjectMapper();

    private StatisticsHistory statisticsHistory = new StatisticsHistory();

    private volatile AtomicInteger idCounter = new AtomicInteger(1);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = "confirmationQueue")
    public void recieveConfirmation(String in) {
        Integer id;
        try {
            id = objectMapper.readValue(in, Integer.class);
        } catch (IOException ex) {
            logger.error("Error: could not parse message in confirmation queue [" + ex.getMessage() + "]");
            return;
        }
        removeItem(id);
    }

    @RabbitListener(queues = "errorQueue")
    public void recieveError(String in) {
        Integer id;
        try {
            id = objectMapper.readValue(in, Integer.class);
        } catch (IOException ex) {
            logger.error("Error: could not parse message in confirmation queue [" + ex.getMessage() + "]");
            return;
        }
        logger.info("Statistics queue: received error from statistics service with id[" + id + "]");
        removeItem(id);
    }

    public void sendMessage(String message, String route) {
        Integer id = addItem(message, route);
        logger.info("Statistics queue: sending message statistics service with id[" + id + "]" + ", message[" + message +
                    "], route[" + route + "]");
        sendMessage(id, message, route);
    }

    public void sendMessage(int id, String message, String route) {
        StatisticsItem item = new StatisticsItem(id, message);
        try {
            String jsonStatisticsItem = objectMapper.writeValueAsString(item);
            rabbitTemplate.convertAndSend("rsoi.statistics.message", route, item);
        } catch (JsonProcessingException ex) {
            logger.error("Error: could not convert object to json. Item id[" + id + "], message[" + message + "]");
        }
    }

    private int addItem(String message, String route) {
        int idToReturn = idCounter.getAndAdd(1);
        statisticsHistory.add(idToReturn, System.currentTimeMillis(), message, route);
        logger.info("Statistics queue: added information about message id[" + idToReturn + "]" + ", message[" +
                    message + "], route[" + route + "] to hash tables");
        return idToReturn;
    }

    @Scheduled(fixedDelay = 10000, initialDelay = 1000)
    public void scanForExpired() {
        List<Integer> idList =statisticsHistory.updateNumberOfExpirations();
        idList.forEach(id -> sendMessage(id, statisticsHistory.getMessage(id), statisticsHistory.getMessageRoute(id)));
    }

    private void removeItem(Integer id) {
        logger.info("Statistics queue: removing information about message with id[" + id + "]" + " from hash tables");
        statisticsHistory.remove(id);
    }
}
