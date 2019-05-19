package ru.nikolay.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class StorageResponse {
    private String id;
    private String location;
    private List<String> bookInformationResponseIds;
}
