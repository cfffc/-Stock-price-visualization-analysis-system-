package com.stock.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stock.annotation.RoleEnum;
import com.stock.annotation.Roles;
import com.stock.entity.RandomForestArgument;
import com.stock.entity.Result;
import com.stock.service.IRandomForestArgumentService;
import com.stock.util.GetCurrentTime;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.Map;


@RestController
@RequestMapping("/randomForestArgument")
@Tag(name = "随机森林参数控制接口接口")
public class RandomForestArgumentController {
    @Resource
    private IRandomForestArgumentService randomForestArgumentService;
    @Resource
    private GetCurrentTime getCurrentTime;

    @Roles(RoleEnum.Admin)
    @GetMapping("/list")
    public Result list() {
        QueryWrapper<RandomForestArgument> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("id");
        return Result.success(randomForestArgumentService.list(wrapper), "参数数据列表获取成功");
    }

    @Roles(RoleEnum.Admin)
    @PostMapping("/add")
    public Result add(@RequestBody Map<String, String> map) {
        Integer estimatorsCount = Integer.valueOf(map.get("estimatorsCount"));
        Integer randomStates = Integer.valueOf(map.get("randomStates"));
        Integer nJobs = Integer.valueOf(map.get("nJobs"));
        Double meanAbsoluteError = Double.valueOf(map.get("meanAbsoluteError"));
        Double meanSquaredError = Double.valueOf(map.get("meanSquaredError"));
        Double rootMeanSquaredError = Double.valueOf(map.get("rootMeanSquaredError"));

        RandomForestArgument randomForestArgument = new RandomForestArgument();
        randomForestArgument.setEstimatorsCount(estimatorsCount);
        randomForestArgument.setRandomStates(randomStates);
        randomForestArgument.setNJobs(nJobs);
        randomForestArgument.setMeanAbsoluteError(meanAbsoluteError);
        randomForestArgument.setMeanSquaredError(meanSquaredError);
        randomForestArgument.setRootMeanSquaredError(rootMeanSquaredError);
        randomForestArgument.setUseState(false);
        randomForestArgument.setCreateTime(getCurrentTime.getCurrentTimeByDay());
        randomForestArgumentService.save(randomForestArgument);

        return Result.success(null, "参数预测记录添加成功");
    }

    @Roles(RoleEnum.Admin)
    @PostMapping("/useArgument")
    public Result useArgument(@RequestBody Map<String, String> map) {
        Integer id = Integer.valueOf(map.get("id"));
        QueryWrapper<RandomForestArgument> wrapper = new QueryWrapper<>();
        wrapper.eq("id", id);
        RandomForestArgument randomForestArgument = randomForestArgumentService.getOne(wrapper);
        if (randomForestArgument == null) {
            return Result.error(null, "该参数记录不存在，使用失败");
        }

        QueryWrapper<RandomForestArgument> resetWrapper = new QueryWrapper<>();
        resetWrapper.eq("use_state", 1);
        RandomForestArgument one = randomForestArgumentService.getOne(resetWrapper);
        if (one != null) {
            one.setUseState(false);
            randomForestArgumentService.update(one, resetWrapper);
        }

        randomForestArgument.setUseState(true);
        randomForestArgumentService.update(randomForestArgument, wrapper);

        return Result.success(null, "参数预测记录使用成功");
    }
}
