package ru.nikolay.service.gateway.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import ru.nikolay.service.gateway.security.AuthenticationInterceptor;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {
    private AuthenticationInterceptor interceptor = new AuthenticationInterceptor();

    @Override
    public void addInterceptors(InterceptorRegistry registrty) {
        registrty.addInterceptor(interceptor)
                .addPathPatterns("./**");
    }
}

