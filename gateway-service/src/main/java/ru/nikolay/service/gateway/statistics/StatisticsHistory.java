package ru.nikolay.service.gateway.statistics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class StatisticsHistory {
    private static final Logger logger = LoggerFactory.getLogger(StatisticsHistory.class);

    private final int requestExpirationMilliseconds = 20000;
    private final int maxNumberOfExpiredRequestsResending = 5;

    private ConcurrentHashMap<Integer, Long> expirations = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, Integer> expirationNumber = new ConcurrentHashMap<>();

    private ConcurrentHashMap<Integer, String> messages = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, String> messageRoute = new ConcurrentHashMap<>();

    public void add(int id, long expirationTime, String message, String route) {
        expirations.put(id, expirationTime);
        expirationNumber.put(id, 0);
        messages.put(id, message);
        messageRoute.put(id, route);
    }

    public String getMessage(int id) {
        return messages.get(id);
    }

    public String getMessageRoute(int id) {
        return messageRoute.get(id);
    }

    public List<Integer> updateNumberOfExpirations() {
        List<Integer> expiredEntriesToUpdate = new ArrayList<>();

        expirations.forEach((id, expirationTime) -> {
            if(System.currentTimeMillis() < expirationTime + requestExpirationMilliseconds) {
                return;
            }
            int timesEntryExpired = expirationNumber.getOrDefault(id, 0);
            if(timesEntryExpired < maxNumberOfExpiredRequestsResending) {
                logger.info("Statistics queue: message with id[" + id + "]" + " expired "
                        + timesEntryExpired + " times");
                logger.info("Statistics queue: message with id[" + id + "]" + " expired");
                expirationNumber.put(id, timesEntryExpired+1);
                expiredEntriesToUpdate.add(id);
            } else {
                logger.info("Statistics queue: message with id[" + id + "]" + " expired more than "
                        + maxNumberOfExpiredRequestsResending + " times");
                remove(id);
            }

        });
        Long currentTime = System.currentTimeMillis();
        expiredEntriesToUpdate.forEach(id -> expirations.put(id, currentTime));

        return expiredEntriesToUpdate;
    }

    public void remove(int id) {
        expirations.remove(id);
        expirationNumber.remove(id);
        messages.remove(id);
        messageRoute.remove(id);
    }
}
