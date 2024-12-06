package com.stock.interceptor;

import com.stock.util.JwtUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.stock.config.WebConfig.PREFLIGHT;


@Component
public class TokenInterceptor implements HandlerInterceptor {
    @Resource
    private JwtUtils jwtUtils;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) {
        if (PREFLIGHT.equals(request.getMethod())) {
            return true;
        }

        String token = request.getHeader("token");
        if (token == null) {
            return false;
        }
        token = token.replaceAll("\"", "");
        try {
            jwtUtils.verify(token);
        } catch (Exception e) {
            return false;
        }

        String username = request.getHeader("username");
        if (username == null) {
            return false;
        }
        username = username.replaceAll("\"", "");
        String s = stringRedisTemplate.opsForValue().get(username);
        return token.equals(s);
    }
}
