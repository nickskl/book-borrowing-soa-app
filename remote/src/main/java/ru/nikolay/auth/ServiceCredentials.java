package ru.nikolay.auth;

import lombok.Data;
import org.apache.commons.codec.binary.Base64;
import org.springframework.stereotype.Component;

@Data
@Component
public class ServiceCredentials {
    String appId;
    String appSecret;

    public String toString() {
        String result = appId + ":" + appSecret;
        return Base64.encodeBase64String(result.getBytes());
    }
}

