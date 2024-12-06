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
@TableName("stock_data")
public class StockData implements Serializable {

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
     * 股票日期
     */
    private String stockDate;

    /**
     * 开盘价格
     */
    private Double startPrice;

    /**
     * 最高价格
     */
    private Double highPrice;

    /**
     * 最低价格
     */
    private Double lowPrice;

    /**
     * 收盘价格
     */
    private Double endPrice;

    /**
     * 成交量
     */
    private Long dealNumber;

    /**
     * 成交额
     */
    private Double dealMoney;

    /**
     * 创建时间（年-月-日）
     */
    @TableField(fill = FieldFill.INSERT)
    private String createTime;
}
