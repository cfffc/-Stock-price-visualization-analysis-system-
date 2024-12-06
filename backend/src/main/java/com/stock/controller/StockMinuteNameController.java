package com.stock.controller;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stock.annotation.RoleEnum;
import com.stock.annotation.Roles;
import com.stock.config.WebConfig;
import com.stock.entity.Result;
import com.stock.entity.StockMinuteData;
import com.stock.entity.StockMinuteName;
import com.stock.service.IStockMinuteDataService;
import com.stock.service.IStockMinuteNameService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/stockMinuteName")
@Tag(name = "分钟级股票名称接口")
public class StockMinuteNameController {
    private static final String STOCK_NOT_EXIST = "{\"code\": 500,\"msg\": \"The Stock Not Exist.\"}";

    @Resource
    private IStockMinuteNameService stockMinuteNameService;
    @Resource
    private IStockMinuteDataService stockMinuteDataService;

    @Roles(RoleEnum.Admin)
    @Operation(summary = "获取股票列表，分页显示")
    @PostMapping("/list")
    public Result list(@RequestBody Map<String, String> map) {
        int pageNum = Integer.parseInt(map.get("pageNum"));
        String searchStockName = map.get("searchStockName");
        String searchTime = map.get("searchTime");

        QueryWrapper<StockMinuteName> wrapper = new QueryWrapper<>();
        wrapper.like("stock_name", searchStockName).like("create_time", searchTime);

        Page<StockMinuteName> page = new Page<>(pageNum, WebConfig.PAGE_SIZE);
        Page<StockMinuteName> stockNamePage = stockMinuteNameService.page(page, wrapper);
        List<StockMinuteName> stockNameList = stockNamePage.getRecords();
        long total = stockNamePage.getTotal();
        JSONObject jsonObject = new JSONObject();
        jsonObject.putOpt("stockNameList", stockNameList);
        jsonObject.putOpt("total", total);

        return Result.success(jsonObject, "股票列表获取成功");
    }

    @Operation(summary = "获取股票列表")
    @GetMapping("/listAll")
    public Result listAll() {
        return Result.success(stockMinuteNameService.list(), "股票列表获取成功");
    }

    @Roles(RoleEnum.Admin)
    @Operation(summary = "导出股票数据")
    @GetMapping("/exportData")
    public void exportData(HttpServletResponse response, @RequestParam String stockId) throws Exception {
        QueryWrapper<StockMinuteName> wrapper = new QueryWrapper<>();
        wrapper.eq("stock_id", stockId);
        StockMinuteName stockName = stockMinuteNameService.getOne(wrapper);
        if (stockName == null) {
            response.getOutputStream().print(STOCK_NOT_EXIST);
            return;
        }

        QueryWrapper<StockMinuteData> stockDataQueryWrapper = new QueryWrapper<>();
        stockDataQueryWrapper.eq("stock_id", stockId);
        List<StockMinuteData> stockDataList = stockMinuteDataService.list(stockDataQueryWrapper);

        StringBuilder builder = new StringBuilder();
        builder.append(stockName.getStockId()).append("\t").append(stockName.getStockName());
        builder.append("\t").append("日线").append("\t").append("不复权").append("\n");
        builder.append("日期").append("\t").append("时间").append("\t").append("开盘").append("\t").append("最高").append("\t").append("最低").append("\t").append("收盘")
                .append("\t").append("成交量").append("\t").append("成交额").append("\n");
        for (StockMinuteData stockMinuteData : stockDataList) {
            builder.append(stockMinuteData.getStockDate()).append("\t");
            builder.append(stockMinuteData.getMinute()).append("\t");
            builder.append(stockMinuteData.getStartPrice()).append("\t");
            builder.append(stockMinuteData.getHighPrice()).append("\t");
            builder.append(stockMinuteData.getLowPrice()).append("\t");
            builder.append(stockMinuteData.getEndPrice()).append("\t");
            builder.append(stockMinuteData.getDealNumber()).append("\t");
            builder.append(stockMinuteData.getDealMoney()).append("\n");
        }
        builder.append("数据来源:通达信").append("\n");

        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(stockName.getStockName(), "gbk"));
        ServletOutputStream outputStream = response.getOutputStream();
        outputStream.write(builder.toString().getBytes());
        outputStream.flush();
    }

    @Roles(RoleEnum.Admin)
    @Operation(summary = "删除股票数据")
    @DeleteMapping("/deleteStock")
    public Result deleteStock(@RequestParam String stockId) {
        QueryWrapper<StockMinuteName> wrapper = new QueryWrapper<>();
        wrapper.eq("stock_id", stockId);
        StockMinuteName stockName = stockMinuteNameService.getOne(wrapper);
        if (stockName == null) {
            return Result.error(null, "该股票不存在");
        }

        QueryWrapper<StockMinuteData> stockMinuteDataQueryWrapper = new QueryWrapper<>();
        stockMinuteDataQueryWrapper.eq("stock_id", stockId);
        stockMinuteDataService.remove(stockMinuteDataQueryWrapper);
        stockMinuteNameService.remove(wrapper);

        return Result.success(null, "股票删除成功");
    }
}
