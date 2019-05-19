package ru.nikolay.security;

import org.springframework.web.util.WebUtils;
import ru.nikolay.remote.RemoteGatewayService;
import ru.nikolay.remote.TokenPair;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtils {
    private static String getAccessCookie(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, "RsoiAccess");
        if (cookie == null) return null;
        return cookie.getValue();
    }

    private static String getRefreshCookie(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, "RsoiRefresh");
        if (cookie == null) return null;
        return cookie.getValue();
    }

    static public boolean isAuthenticated(HttpServletRequest request, RemoteGatewayService service) {
        String token = getAccessCookie(request);
        if (token == null || token.isEmpty()) return false;
        return service.isAuthenticated(token);
    }

    static public boolean isAdmin(HttpServletRequest request, RemoteGatewayService service) {
        String token = getAccessCookie(request);
        if (token == null || token.isEmpty()) return false;
        return service.isAdmin(token);
    }

    static public void setTokenCookies(String accessToken, String refreshToken, HttpServletResponse servletResponse) {
        Cookie accessCookie = new Cookie("RsoiAccess", accessToken);
        accessCookie.setMaxAge(-1);
        accessCookie.setPath("/");
        Cookie refreshCookie = new Cookie("RsoiRefresh", refreshToken);
        refreshCookie.setMaxAge(-1);
        refreshCookie.setPath("/");
        servletResponse.addCookie(accessCookie);
        servletResponse.addCookie(refreshCookie);
    }

    static public void clearTokenCookies(HttpServletResponse servletResponse) {
        Cookie accessCookie = new Cookie("RsoiAccess", "");
        accessCookie.setMaxAge(0);
        Cookie refreshCookie = new Cookie("RsoiRefresh", "");
        refreshCookie.setMaxAge(0);

        servletResponse.addCookie(accessCookie);
        servletResponse.addCookie(refreshCookie);
    }

    private static boolean tryRefresh(HttpServletRequest request, HttpServletResponse response,
                                      RemoteGatewayService service) {
        String refreshToken = getRefreshCookie(request);
        if (refreshToken != null && refreshToken.isEmpty()) return false;

        TokenPair pair = service.refreshToken(refreshToken);
        if (pair == null) {
            return false;
        }

        setTokenCookies(pair.getAccessToken(), pair.getRefreshToken(), response);
        return true;
    }

    static public boolean checkAuthorizationAndTryRefresh(HttpServletRequest request, HttpServletResponse response,
                                                          RemoteGatewayService service) {
        if (isAuthenticated(request, service)) return true;
        if (tryRefresh(request, response, service)) return true;

        return false;
    }
}
