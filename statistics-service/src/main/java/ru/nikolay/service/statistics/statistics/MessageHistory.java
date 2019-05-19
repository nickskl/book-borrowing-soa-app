package ru.nikolay.service.statistics.statistics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

public class MessageHistory {
    private static final Logger logger = LoggerFactory.getLogger(MessageHistory.class);
    private final int requestExpirationMilliseconds = 100;

    private ConcurrentHashMap<Integer, Long> processedEvents = new ConcurrentHashMap<>();

    public void add(int id) {
        processedEvents.put(id, System.currentTimeMillis());
    }

    public boolean messageExists(int id) {
        return processedEvents.get(id) != null;
    }

    public void removeExpired() {
        processedEvents.forEach((id, expirationTime) -> {
            if(System.currentTimeMillis() >= expirationTime + requestExpirationMilliseconds) {
                logger.info("Statistics: removing event [" + id + "]");
                remove(id);
            }
        });
    }

    public void remove(int id) {
        processedEvents.remove(id);
    }

}
