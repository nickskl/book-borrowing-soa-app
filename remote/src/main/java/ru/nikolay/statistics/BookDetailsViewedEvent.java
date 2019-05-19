package ru.nikolay.statistics;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookDetailsViewedEvent {
    @Id
    private String id;
    private String bookId;
    private Date timestamp;

}
