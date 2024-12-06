package com.stock.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Result {
    public static final Integer SUCCESS = 200;
    public static final Integer ERROR = 500;

    private Object data;
    private Integer code;
    private String msg;

    public static Result success(Object data, String msg) {
        return new Result(data, SUCCESS, msg);
    }

    public static Result error(Object data, String msg) {
        return new Result(data, ERROR, msg);
    }
}
