package ru.nikolay.apiexception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class ApiGlobalError implements Serializable{
    private String code;
    private String message;
}
