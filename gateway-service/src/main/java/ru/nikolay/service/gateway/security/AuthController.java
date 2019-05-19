package ru.nikolay.service.gateway.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.nikolay.auth.JwtToken;
import ru.nikolay.auth.ServiceCredentials;
import ru.nikolay.remote.TokenPair;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequestMapping("/")
public class AuthController {
    @Autowired
    @Qualifier(value = "allowedCredentials")
    List<ServiceCredentials> allowedCredentials;

    @RequestMapping(value = "/auth/token", method = RequestMethod.GET)
    public TokenPair GetToken(HttpRequest request, HttpServletResponse response) {
        String creds;
        creds = request.getHeaders().getFirst("Authorization");
        if (!creds.startsWith("Basic ")) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return null;
        }

        creds = creds.replaceFirst("^Basic ", "");

        for (ServiceCredentials sc : allowedCredentials) {
            String hex;
            hex = sc.toString();

            if (hex.equals(creds)) {
                TokenPair tp = new TokenPair();
                tp.setAccessToken(JwtToken.IssueAccessJwt(sc.getAppId()));
                tp.setRefreshToken(JwtToken.IssueRefreshJwt(sc.getAppId()));
                return tp;
            }
        }

        response.setStatus(HttpStatus.FORBIDDEN.value());
        return null;
    }

    @RequestMapping(value = "/auth/token", method = RequestMethod.POST)
    public TokenPair RefreshToken(HttpRequest request, HttpServletResponse response, @RequestBody String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return null;
        }

        String creds;
        creds = request.getHeaders().getFirst("Authorization");

        if (!creds.startsWith("Bearer ")) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return null;
        }

        creds = creds.replaceFirst("^Bearer ", "");
        String type = JwtToken.parseJwtTokenType(creds);

        if (type == null || type.equals("Refresh")) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return null;
        }

        String subject = JwtToken.parseJwtTokenSubject(creds);
        if (subject == null) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            return null;
        }

        for (ServiceCredentials sc : allowedCredentials) {
            if (subject.equals(sc.getAppId())) {
                TokenPair tp = new TokenPair();
                tp.setAccessToken(JwtToken.IssueAccessJwt(sc.getAppId()));
                tp.setRefreshToken(JwtToken.IssueRefreshJwt(sc.getAppId()));
            }
        }

        response.setStatus(HttpStatus.FORBIDDEN.value());
        return null;
    }
}