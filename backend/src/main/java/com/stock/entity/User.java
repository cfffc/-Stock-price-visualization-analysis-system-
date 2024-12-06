package com.stock.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String ADMIN = "admin";
    public static final String NORMAL = "normal";

    public static final String DEFAULT_AVATAR_URL = "https://tse3-mm.cn.bing.net/th/id/OIP-C.PKH2z9SyB0rctZ71VFZvngAAAA?w=201&h=201&c";

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户类型（普通用户、管理员）
     */
    private String userType;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 创建时间（年-月-日）
     */
    @TableField(fill = FieldFill.INSERT)
    private String createTime;

    public static Result judgeValid(String username) {
        if (username.trim().isEmpty() || username.trim().length() > 20) {
            return Result.error(null, "用户名长度最多为20字符且不能为空");
        }
        return Result.success(null, "");
    }
}
