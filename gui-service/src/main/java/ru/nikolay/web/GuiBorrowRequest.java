package ru.nikolay.web;

import lombok.Data;
import ru.nikolay.requests.BorrowingInfoRequest;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

@Data
public class GuiBorrowRequest {
    @NotNull
    private String userId;
    @NotNull
    private String bookId;
    @NotNull
    private String storageId;
    @NotNull
    @Pattern(regexp = "\\d{4}-(?:(?:1[012])|(?:0\\d))-(?:(?:3[01])|(?:[012]\\d))", message = "Date should be in format yyyy-MM-dd")
    private String borrowedDate;
    @NotNull
    @Pattern(regexp = "\\d{4}-(?:(?:1[012])|(?:0\\d))-(?:(?:3[01])|(?:[012]\\d))", message = "Date should be in format yyyy-MM-dd")
    private String dateToReturnBook;

    public BorrowingInfoRequest toBorrowingInfoRequest() throws Exception {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        return new BorrowingInfoRequest(userId, bookId,storageId, format.parse(borrowedDate), format.parse(dateToReturnBook));
    }
}
