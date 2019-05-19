package ru.nikolay.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class ServiceConfiguration {
    @Bean
    public ServiceCredentials serviceCredentials(@Value("${rsoi.service.appId}") String appId,
                                          @Value("${rsoi.service.appSecret}") String appSecret) {
        ServiceCredentials creds = new ServiceCredentials();
        creds.setAppId(appId);
        creds.setAppSecret(appSecret);
        return creds;
    }

    @Bean(name = "allowedCredentials")
    @Scope("singleton")
    public List<ServiceCredentials> allowedCredentials(
            @Value("#{'${rsoi.services.creds}'.split(';')}") List<String> credentials) {
        List<ServiceCredentials> result = new ArrayList<>();

        for (String s: credentials) {
            String[] c = s.split(":");

            ServiceCredentials cred = new ServiceCredentials();
            cred.setAppId(c[0]);
            cred.setAppSecret(c[1]);

            result.add(cred);
        }

        return result;
    }

    @Bean(name = "gatewayTokens")
    @Scope("singleton")
    public ServiceTokens gatewayTokens() {
        return new ServiceTokens();
    }

    @Bean(name = "bookTokens")
    @Scope("singleton")
    public ServiceTokens bookTokens() {
        return new ServiceTokens();
    }

    @Bean(name = "storageTokens")
    @Scope("singleton")
    public ServiceTokens storageTokens() {
        return new ServiceTokens();
    }

    @Bean(name = "bookBorrowingTokens")
    @Scope("singleton")
    public ServiceTokens bookBorrowingTokens() {
        return new ServiceTokens();
    }
}
