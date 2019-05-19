package ru.nikolay.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.util.WebUtils;
import ru.nikolay.remote.RemoteGatewayService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AdminOnlyCheckInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    RemoteGatewayService gatewayService;

    private static String getAccessCookie(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, "RsoiAccess");
        if (cookie == null) return null;
        return cookie.getValue();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(!CookieUtils.isAdmin(request, gatewayService)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.sendRedirect("/forbidden");
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object object, ModelAndView model) throws Exception {
        model.addObject("isAdmin", true);
    }
}
