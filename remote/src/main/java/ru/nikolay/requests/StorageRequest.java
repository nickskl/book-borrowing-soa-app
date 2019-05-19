package ru.nikolay.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StorageRequest {
    @NotNull
    @Size(min=1, max=64)
    private String location;
    private List<BookInformationRequest> bookInformationRequestList;
}
