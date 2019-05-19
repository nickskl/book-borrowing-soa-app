package ru.nikolay.apiexception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ApiFieldError implements Serializable{
    private String field;
    private String code;
    private Object rejectedValue;
    private String message;
}
