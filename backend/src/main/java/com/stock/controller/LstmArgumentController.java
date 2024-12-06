package com.stock.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stock.annotation.RoleEnum;
import com.stock.annotation.Roles;
import com.stock.entity.LstmArgument;
import com.stock.entity.Result;
import com.stock.service.ILstmArgumentService;
import com.stock.util.GetCurrentTime;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.Map;


@RestController
@RequestMapping("/lstmArgument")
@Tag(name = "LSTM参数控制接口")
public class LstmArgumentController {
    @Resource
    private ILstmArgumentService lstmArgumentService;
    @Resource
    private GetCurrentTime getCurrentTime;

    @Roles(RoleEnum.Admin)
    @GetMapping("/list")
    public Result list() {
        QueryWrapper<LstmArgument> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("id");
        return Result.success(lstmArgumentService.list(wrapper), "参数数据列表获取成功");
    }

    @Roles(RoleEnum.Admin)
    @PostMapping("/add")
    public Result add(@RequestBody Map<String, String> map) {
        Integer lstmLayers = Integer.valueOf(map.get("lstmLayers"));
        Integer denseLayers = Integer.valueOf(map.get("denseLayers"));
        Integer units = Integer.valueOf(map.get("units"));
        Double loss = Double.valueOf(map.get("loss"));
        Double mape = Double.valueOf(map.get("mape"));
        Double rootMeanSquaredError = Double.valueOf(map.get("rootMeanSquaredError"));

        LstmArgument lstmArgument = new LstmArgument();
        lstmArgument.setLstmLayers(lstmLayers);
        lstmArgument.setDenseLayers(denseLayers);
        lstmArgument.setUnits(units);
        lstmArgument.setLoss(loss);
        lstmArgument.setMape(mape);
        lstmArgument.setRootMeanSquaredError(rootMeanSquaredError);
        lstmArgument.setUseState(false);
        lstmArgument.setCreateTime(getCurrentTime.getCurrentTimeByDay());
        lstmArgumentService.save(lstmArgument);

        return Result.success(null, "参数预测记录添加成功");
    }

    @Roles(RoleEnum.Admin)
    @PostMapping("/useArgument")
    public Result useArgument(@RequestBody Map<String, String> map) {
        Integer id = Integer.valueOf(map.get("id"));
        QueryWrapper<LstmArgument> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id);
        LstmArgument lstmArgument = lstmArgumentService.getOne(wrapper);
        if (lstmArgument == null) {
            return Result.error(null, "该参数记录不存在，使用失败");
        }

        QueryWrapper<LstmArgument> resetWrapper = new QueryWrapper<>();
        resetWrapper.eq("use_state", 1);
        LstmArgument one = lstmArgumentService.getOne(resetWrapper);
        if (one != null) {
            one.setUseState(false);
            lstmArgumentService.update(one, resetWrapper);
        }

        lstmArgument.setUseState(true);
        lstmArgumentService.update(lstmArgument, wrapper);

        return Result.success(null, "参数预测记录使用成功");
    }
}
