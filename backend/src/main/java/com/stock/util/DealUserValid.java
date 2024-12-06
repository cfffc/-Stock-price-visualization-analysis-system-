package com.stock.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stock.entity.User;
import com.stock.service.IUserService;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;


@Component
public class DealUserValid {
    @Resource
    private IUserService userService;

    public boolean judgeUserValid(String username, String password) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username).eq("password", password);
        User user = userService.getOne(wrapper);
        return user != null;
    }
}
