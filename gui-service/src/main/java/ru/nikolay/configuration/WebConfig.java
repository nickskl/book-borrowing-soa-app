package ru.nikolay.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import ru.nikolay.security.AdditionalAuthenticationInterceptor;
import ru.nikolay.security.AdminOnlyCheckInterceptor;
import ru.nikolay.security.AuthenticationInterceptor;

import java.util.List;

@Configuration
public class WebConfig extends WebMvcConfigurerAdapter {
    @Autowired
    private AuthenticationInterceptor interceptor;

    @Autowired
    private AdditionalAuthenticationInterceptor additionalAuthenticationInterceptor;

    @Autowired
    private AdminOnlyCheckInterceptor adminOnlyCheckInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registrty) {
        registrty.addInterceptor(interceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/authorized", "/logout", "/forbidden");
        registrty.addInterceptor(additionalAuthenticationInterceptor)
                .addPathPatterns("/", "/authorized", "/logout", "/forbidden");
        registrty.addInterceptor(adminOnlyCheckInterceptor)
                .addPathPatterns("/statistics/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver();
        resolver.setFallbackPageable(new PageRequest(0, 5));
        argumentResolvers.add(resolver);
        super.addArgumentResolvers(argumentResolvers);
    }

}