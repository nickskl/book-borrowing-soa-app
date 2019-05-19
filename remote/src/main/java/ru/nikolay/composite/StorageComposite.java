package ru.nikolay.service.gateway.web.model;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.nikolay.responses.StorageResponse;

import java.util.List;

@Data
@Accessors(chain = true)
public class StorageComposite {
    private String storageId;
    private String location;
    private List<String> bookInformationResponseIds;

    public static StorageComposite fromResponse(StorageResponse response) {
        return new StorageComposite()
                .setStorageId(response.getId())
                .setLocation(response.getLocation())
                .setBookInformationResponseIds(response.getBookInformationResponseIds());
    }
}
