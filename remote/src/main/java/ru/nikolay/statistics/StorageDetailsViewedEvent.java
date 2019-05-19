package ru.nikolay.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StorageDetailsViewedEvent {
    @Id
    private String id;
    private String storageId;
    private Date timestamp;
}
