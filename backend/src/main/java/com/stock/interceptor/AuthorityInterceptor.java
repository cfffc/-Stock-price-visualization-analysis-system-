package com.stock.interceptor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stock.annotation.RoleEnum;
import com.stock.annotation.Roles;
import com.stock.entity.User;
import com.stock.service.IUserService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static com.stock.config.WebConfig.PREFLIGHT;


@Component
public class AuthorityInterceptor implements HandlerInterceptor {
    @Resource
    private IUserService userService;

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

        if (handler.getClass().isAssignableFrom(HandlerMethod.class)) {
            Roles methodAnnotation = ((HandlerMethod) handler).getMethodAnnotation(Roles.class);
            if (methodAnnotation != null && methodAnnotation.value() == RoleEnum.Admin) {
                QueryWrapper<User> wrapper = new QueryWrapper<>();
                wrapper.eq("username", username).eq("password", password).eq("user_type", User.ADMIN);
                return userService.getOne(wrapper) != null;
            }
        }

        return true;
    }
}
