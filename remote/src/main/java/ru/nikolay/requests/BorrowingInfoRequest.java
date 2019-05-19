package ru.nikolay.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BorrowingInfoRequest {
    @NotNull
    private String userId;
    @NotNull
    private String bookId;
    @NotNull
    private String storageId;
    @NotNull
    private Date borrowedDate;
    @NotNull
    private Date dateToReturnBook;
}
