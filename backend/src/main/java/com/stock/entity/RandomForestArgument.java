package com.stock.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@TableName("random_forest_argument")
public class RandomForestArgument implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private Integer estimatorsCount;

    private Integer randomStates;

    private Integer nJobs;

    private Object meanAbsoluteError;

    private Object meanSquaredError;

    private Object rootMeanSquaredError;

    /**
     * 使用状态（0表示未使用，1表示使用，只要有一个为1）
     */
    private Boolean useState;

    /**
     * 模型参数创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private String createTime;
}
