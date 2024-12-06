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
@TableName("stock_minute_name")
public class StockMinuteName implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 股票ID
     */
    private String stockId;

    /**
     * 股票名称
     */
    private String stockName;

    /**
     * 创建时间（年-月-日）
     */
    @TableField(fill = FieldFill.INSERT)
    private String createTime;
}
