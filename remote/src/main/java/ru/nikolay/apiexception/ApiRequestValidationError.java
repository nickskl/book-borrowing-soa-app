package ru.nikolay.apiexception;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.io.*;
import java.util.List;

@Data
@AllArgsConstructor
public class ApiRequestValidationError implements Serializable {
    private List<ApiFieldError> fieldErrors;
    private List<ApiGlobalError> globalErrors;

    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (Exception ex) {
            return null;
        }
    }
}
