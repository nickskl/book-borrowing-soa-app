package ru.nikolay.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookBorrowingEvent {
    @Id
    private String id;
    private String storageId;
    private String bookId;
}
