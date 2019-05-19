package ru.nikolay.service.gateway.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import ru.nikolay.auth.JwtToken;
import ru.nikolay.auth.ServiceCredentials;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Component
public class AuthenticationInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    @Qualifier(value = "allowedCredentials")
    List<ServiceCredentials> allowedCredentials;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
        String token = request.getHeader("Authorization");
        if (token != null || !token.startsWith("Bearer ")) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return false;
        }

        token = token.replaceFirst("^Bearer ", "");

        String type = JwtToken.parseJwtTokenType(token);

        if (type == null || type.equals("Access")) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return false;
        }

        String subject = JwtToken.parseJwtTokenSubject(token);
        if (subject == null) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return false;
        }

        for (ServiceCredentials sc : allowedCredentials) {
            if (subject.equals(sc.getAppId())) {
                return true;
            }
        }

        return false;
    }
}
