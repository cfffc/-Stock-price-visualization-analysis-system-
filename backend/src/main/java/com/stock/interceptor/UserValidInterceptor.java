package com.stock.interceptor;

import com.stock.util.DealUserValid;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.stock.config.WebConfig.PREFLIGHT;


@Component
public class UserValidInterceptor implements HandlerInterceptor {
    @Resource
    private DealUserValid dealUserValid;

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        if (PREFLIGHT.equals(request.getMethod())) {
            return true;
        }

        String username = request.getHeader("username");
        String password = request.getHeader("password");
        if (username == null || password == null) {
            return false;
        }
        username = username.replaceAll("\"", "");
        password = password.replaceAll("\"", "");

        if (!dealUserValid.judgeUserValid(username, password)) {
            try {
                response.getWriter().println("Authentication Error");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return false;
        }
        return true;
    }
}
