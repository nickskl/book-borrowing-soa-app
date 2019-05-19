package ru.nikolay.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import ru.nikolay.remote.RemoteGatewayService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static ru.nikolay.web.GuiController.isAdmin;

@Component
public class AdditionalAuthenticationInterceptor extends HandlerInterceptorAdapter{
    @Autowired
    private RemoteGatewayService gatewayService;

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response,
                           Object object, ModelAndView model) throws Exception {
        boolean isAuthenticated = CookieUtils.checkAuthorizationAndTryRefresh(request, response, gatewayService);
        model.addObject("isAuthenticated", isAuthenticated);
        boolean isAdmin = CookieUtils.isAdmin(request, gatewayService);
        model.addObject("isAdmin", isAdmin);
    }
}
