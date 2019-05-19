package ru.nikolay.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtToken {
    private static final String SECRET = "UlNPSVNFQ1JFVA=="; // RSOISECRET

    private static String IssueJwt(String name, String type, Date expiryDate) {

        Claims claims = Jwts.claims()
                .setSubject(name)
                .setExpiration(expiryDate);
        claims.put("type", type);

        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    public static String IssueAccessJwt(String name) {
        Date expiryDate = new Date(System.currentTimeMillis() + 120_000);
        return IssueJwt(name, "Access", expiryDate);
    }

    public static String IssueRefreshJwt(String name) {
        Date expiryDate = new Date(System.currentTimeMillis() + 3_000_000);
        return IssueJwt(name, "Refresh", expiryDate);
    }

    public static String parseJwtTokenType(String jwt) {
        Jws<Claims> type;
        try {
            type = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(jwt);
        }
        catch (Exception e) {
            return null;
        }

        if ((new Date(System.currentTimeMillis())).after(type.getBody().getExpiration())) {
            return null;
        }

        return type.getBody().get("type", String.class);
    }

    public static String parseJwtTokenSubject(String jwt) {
        Jws<Claims> type;
        try {
            type = Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(jwt);
        }
        catch (Exception e) {
            return null;
        }

        if ((new Date(System.currentTimeMillis())).after(type.getBody().getExpiration())) {
            return null;
        }

        return type.getBody().getSubject();
    }

}
