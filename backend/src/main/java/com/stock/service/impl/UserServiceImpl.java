package com.stock.service.impl;

import com.stock.entity.User;
import com.stock.mapper.UserMapper;
import com.stock.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

}
