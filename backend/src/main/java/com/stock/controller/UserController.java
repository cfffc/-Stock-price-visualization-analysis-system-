package com.stock.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stock.annotation.RoleEnum;
import com.stock.annotation.Roles;
import com.stock.config.WebConfig;
import com.stock.entity.Result;
import com.stock.entity.User;
import com.stock.service.IUserService;
import com.stock.util.GetCurrentTime;
import com.stock.util.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.stock.entity.User.DEFAULT_AVATAR_URL;


@RestController
@RequestMapping("/user")
@Tag(name = "用户接口")
public class UserController {
    @Resource
    private IUserService userService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private JwtUtils jwtUtils;
    @Resource
    private GetCurrentTime getCurrentTime;

    @Operation(summary = "用户登录接口")
    @PostMapping("/login")
    public Result login(@RequestBody Map<String, String> map) {
        String username = map.get("username");
        String password = map.get("password");

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username).eq("password", password);
        User user = userService.getOne(wrapper);
        if (user == null) {
            return Result.error(null, "账号或密码错误");
        }

        String token = jwtUtils.getToken();
        stringRedisTemplate.opsForValue().set(username, token, 14, TimeUnit.DAYS);

        JSONObject jsonObject = new JSONObject();
        jsonObject.putOpt("token", token);
        jsonObject.putOpt("username", username);
        jsonObject.putOpt("password", password);
        jsonObject.putOpt("userType", user.getUserType());
        jsonObject.putOpt("avatarUrl", user.getAvatarUrl());

        return Result.success(jsonObject, "用户登陆成功");
    }

    @Operation(summary = "注册接口(暂时只供管理员使用)")
    @PostMapping("/register")
    public Result register(@RequestBody Map<String, String> map) {
        String username = map.get("username");
        String password = map.get("password");

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        User user = userService.getOne(wrapper);
        if (user != null) {
            return Result.error(null, "该账号已存在, 请重新注册");
        }

        Result result = User.judgeValid(username);
        if (Objects.equals(result.getCode(), Result.ERROR)) {
            return result;
        }

        user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setAvatarUrl(DEFAULT_AVATAR_URL);
        user.setUserType(User.NORMAL);
        user.setCreateTime(getCurrentTime.getCurrentTimeByDay());
        if (!userService.save(user)) {
            return Result.error(null, "用户信息保存失败, 用户注册失败");
        }
        return Result.success(null, "用户注册成功");
    }

    @Operation(summary = "退出登录接口")
    @GetMapping("/logout")
    public Result logout(@RequestParam String username) {
        Boolean delete = stringRedisTemplate.delete(username);
        if (Boolean.FALSE.equals(delete)) {
            return Result.error(null, "token删除失败, 退出登录出现错误");
        }
        return Result.success(null, "退出登陆成功");
    }

    @Roles(RoleEnum.Admin)
    @Operation(summary = "获取用户列表")
    @PostMapping("/list")
    public Result list(@RequestBody Map<String, String> map) {
        int pageNum = Integer.parseInt(map.get("pageNum"));
        String searchUsername = map.get("searchUsername");
        String searchTime = map.get("searchTime");

        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.like("username", searchUsername).like("create_time", searchTime);

        Page<User> page = new Page<>(pageNum, WebConfig.PAGE_SIZE);
        Page<User> userPage = userService.page(page, wrapper);
        List<User> userList = userPage.getRecords();
        long total = userPage.getTotal();
        JSONObject jsonObject = new JSONObject();
        jsonObject.putOpt("userList", userList);
        jsonObject.putOpt("total", total);

        return Result.success(jsonObject, "用户列表获取成功");
    }

    @Operation(summary = "修改用户头像")
    @PutMapping("/editAvatarUrl")
    public Result editAvatarUrl(@RequestBody Map<String, String> map) {
        String username = map.get("username");
        String editAvatarUrl = map.get("editAvatarUrl");
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        User user = userService.getOne(wrapper);
        if (user == null) {
            return Result.error(null, "该账号不存在");
        }
        user.setAvatarUrl(editAvatarUrl);
        if (!userService.update(user, wrapper)) {
            return Result.error(null, "用户头像修改失败");
        }
        return Result.success(null, "用户头像修改成功");
    }
}
