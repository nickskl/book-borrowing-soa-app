package ru.nikolay.service.storage.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import ru.nikolay.responses.StorageResponse;
import java.util.List;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class Storage {
    @Id private String id;
    private String location;
    private List<String> bookInformationResponseIds;

    public StorageResponse toResponse() {
        return new StorageResponse(id, location, bookInformationResponseIds);
    }
}
