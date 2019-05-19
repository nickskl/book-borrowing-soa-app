package ru.nikolay.service.storage.domain;

import org.junit.Test;
import ru.nikolay.responses.StorageResponse;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class StorageTest {
    @Test
    public void storageToResponseTest() {
        StorageResponse expectedResponse = new StorageResponse ("0", "0xBADC0DE", new ArrayList<>());
        StorageResponse actualResponse = new Storage()
                .setId("0")
                .setLocation("0xBADC0DE")
                .setBookInformationResponseIds(new ArrayList<>())
                .toResponse();
        assertEquals(expectedResponse, actualResponse);
    }
}
