package com.stock.controller;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.stock.annotation.RoleEnum;
import com.stock.annotation.Roles;
import com.stock.entity.*;
import com.stock.service.ILstmArgumentService;
import com.stock.service.IRandomForestArgumentService;
import com.stock.service.IStockDataService;
import com.stock.service.IStockNameService;
import com.stock.util.GetCurrentTime;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.stock.config.WebConfig.dataDir;
import static com.stock.config.WebConfig.flaskUrl;


@RestController
@RequestMapping("/stockData")
@Tag(name = "股票数据接口")
@Slf4j
public class StockDataController {
    @Resource
    private IStockDataService stockDataService;
    @Resource
    private IStockNameService stockNameService;
    @Resource
    private ILstmArgumentService lstmArgumentService;
    @Resource
    private IRandomForestArgumentService randomForestArgumentService;
    @Resource
    private GetCurrentTime getCurrentTime;

    public static final OkHttpClient HTTP_CLIENT = new OkHttpClient().newBuilder()
            .connectTimeout(60 * 10, TimeUnit.SECONDS)
            .writeTimeout(60 * 10, TimeUnit.SECONDS)
            .readTimeout(60 * 10, TimeUnit.SECONDS).build();

    private final MediaType mediaType = MediaType.parse("application/json");

    @Roles(RoleEnum.Admin)
    @Operation(summary = "添加股票数据，同时会自动添加股票名称信息")
    @PostMapping("/add")
    public Result add(MultipartFile file) throws Exception {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.endsWith(".txt")) {
            return Result.error(null, "文件需要为txt文件");
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "gbk"));
        BufferedReader reader2 = new BufferedReader(new InputStreamReader(file.getInputStream(), "gbk"));
        StringBuilder builder = new StringBuilder();
        String readLine;
        while ((readLine = reader2.readLine()) != null && !readLine.isEmpty()) {
            builder.append(readLine).append("\n");
        }

        String firstLine = reader.readLine();

        QueryWrapper<StockName> wrapper = new QueryWrapper<>();
        String stockId = firstLine.substring(0, 6);
        wrapper.eq("stock_id", stockId);
        StockName stockName = stockNameService.getOne(wrapper);
        String name = "";

        if (stockName == null) {
            stockName = new StockName();
            stockName.setStockId(stockId);
            name = firstLine.substring(7, 12);
            if (name.endsWith("日")) {
                name = name.replace("日", "");
            }
            name = name.replace(" ", "");
            stockName.setStockName(name);
            stockName.setCreateTime(getCurrentTime.getCurrentTimeByDay());
            stockNameService.save(stockName);
        }

        // 调用flask接口，即python脚本，保存文件
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("stockName", name);
        jsonObject.put("dataDir", dataDir);
        jsonObject.put("text", builder.toString());
        RequestBody body = RequestBody.Companion.create(jsonObject.toJSONString(), mediaType);
        Request request = new Request.Builder()
                .url(flaskUrl + "/addStock")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            JSONObject map = JSONObject.parseObject(Objects.requireNonNull(response.body()).string());
            if (map.getInteger("code") != 200) {
                return Result.error(null, "文件保存失败");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(null, "接口服务调用出错");
        }

        QueryWrapper<StockData> stockDataQueryWrapper = new QueryWrapper<>();
        stockDataQueryWrapper.eq("stock_id", stockId);
        stockDataService.remove(stockDataQueryWrapper);

        // 去除第二行
        reader.readLine();
        String line = reader.readLine();
        String[] splits = line.split("\t");

        ArrayList<StockData> stockDataList = new ArrayList<>();
        while (splits.length > 6) {
            StockData stockData = new StockData();
            stockData.setStockId(stockName.getStockId());
            stockData.setStockDate(splits[0]);
            stockData.setStartPrice(Double.valueOf(splits[1]));
            stockData.setHighPrice(Double.valueOf(splits[2]));
            stockData.setLowPrice(Double.valueOf(splits[3]));
            stockData.setEndPrice(Double.valueOf(splits[4]));
            stockData.setDealNumber(Long.valueOf(splits[5]));
            stockData.setDealMoney(Double.valueOf(splits[6]));
            stockData.setCreateTime(getCurrentTime.getCurrentTimeByDay());
            stockDataList.add(stockData);

            line = reader.readLine();
            splits = line.split("\t");
        }
        stockDataService.saveBatch(stockDataList);

        return Result.success(null, "股票数据添加成功");
    }

    @Operation(summary = "获取股票基本数据")
    @PostMapping("/getBasicData")
    public Result getBasicData(@org.springframework.web.bind.annotation.RequestBody Map<String, String> map) {
        String stockId = map.get("stockId");
        String startDate = map.get("startDate");
        String endDate = map.get("endDate");
        QueryWrapper<StockData> wrapper = new QueryWrapper<>();
        wrapper.eq("stock_id", stockId).between("stock_date", startDate, endDate);
        return Result.success(stockDataService.list(wrapper), "股票基本数据获取成功");
    }

    @Operation(summary = "获取股票均线数据")
    @PostMapping("/getAverageData")
    public Result getAverageData(@org.springframework.web.bind.annotation.RequestBody Map<String, String> map) {
        String stockName = map.get("stockName");
        String startDate = map.get("startDate");
        String endDate = map.get("endDate");

        // 调用flask接口，即python脚本，获取均线数据
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("stockName", stockName);
        jsonObject.put("dataDir", dataDir);
        jsonObject.put("startDate", startDate);
        jsonObject.put("endDate", endDate);
        RequestBody body = RequestBody.Companion.create(jsonObject.toJSONString(), mediaType);
        Request request = new Request.Builder()
                .url(flaskUrl + "/generateMAData")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            JSONObject jsonObjectRes = JSONObject.parseObject(Objects.requireNonNull(response.body()).string());
            if (jsonObjectRes.getInteger("code") != 200) {
                return Result.error(null, "股票均线数据获取失败");
            }

            ArrayList<AverageData> averageDataList = new ArrayList<>();
            JSONArray jsonArray = jsonObjectRes.getJSONArray("data");
            for (Object o : jsonArray) {
                JSONArray tempArr = (JSONArray) o;
                AverageData averageData = new AverageData();
                averageData.setStockDate((String) tempArr.get(0));
                averageData.setM5(Double.valueOf(String.valueOf(tempArr.get(1))));
                averageData.setM10(Double.valueOf(String.valueOf(tempArr.get(2))));
                averageData.setM20(Double.valueOf(String.valueOf(tempArr.get(3))));
                averageData.setM30(Double.valueOf(String.valueOf(tempArr.get(4))));
                averageData.setM60(Double.valueOf(String.valueOf(tempArr.get(5))));
                averageData.setM120(Double.valueOf(String.valueOf(tempArr.get(6))));
                averageData.setM250(Double.valueOf(String.valueOf(tempArr.get(7))));
                averageDataList.add(averageData);
            }
            return Result.success(averageDataList, "股票均线数据获取成功");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(null, "接口服务调用出错");
        }
    }

    @Operation(summary = "最小二乘法")
    @PostMapping("/leastSquares")
    public Result leastSquares(@org.springframework.web.bind.annotation.RequestBody Map<String, String> map) {
        String stockName = map.get("stockName");
        String predictType = map.get("predictType");
        String[] predictTypes = new String[]{"S", "M", "L", "SM", "ML", "SML"};
        if (Arrays.stream(predictTypes).noneMatch(s -> Objects.equals(s, predictType))) {
            return Result.error(null, "预测类型错误");
        }
        String fileName = dataDir + stockName + ".xls";

        // 调用flask接口，即python脚本，采用最小二乘法预测结果并返回数据
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("fileName", fileName);
        jsonObject.put("predictType", predictType);
        RequestBody body = RequestBody.Companion.create(jsonObject.toJSONString(), mediaType);
        Request request = new Request.Builder()
                .url(flaskUrl + "/leastSquares")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            JSONObject jsonObjectRes = JSONObject.parseObject(Objects.requireNonNull(response.body()).string());
            if (jsonObjectRes.getInteger("code") != 200) {
                return Result.error(null, "均线比较数据获取失败");
            }

            ArrayList<EndPrice> endPriceList = new ArrayList<>();
            JSONArray jsonArray = jsonObjectRes.getJSONArray("data");
            for (Object o : jsonArray) {
                JSONArray tempArr = (JSONArray) o;
                EndPrice endPrice = new EndPrice();
                endPrice.setStockDate((String) tempArr.get(0));
                endPrice.setOriginPrice(Double.valueOf((String) tempArr.get(1)));
                endPrice.setPredictPrice(Double.valueOf((String) tempArr.get(2)));
                endPriceList.add(endPrice);
            }
            return Result.success(endPriceList, "均线比较数据获取成功");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(null, "接口服务调用出错");
        }
    }

    @Operation(summary = "随机森林法")
    @PostMapping("/randomForest")
    public Result randomForest(@org.springframework.web.bind.annotation.RequestBody Map<String, String> map) {
        String stockName = map.get("stockName");
        String predictType = map.get("predictType");
        Integer preDays = Integer.valueOf(map.get("preDays"));
        String[] predictTypes = new String[]{"S", "M", "L", "SM", "ML", "SML"};
        if (Arrays.stream(predictTypes).noneMatch(s -> Objects.equals(s, predictType))) {
            return Result.error(null, "预测类型错误");
        }

        String fileName = dataDir + stockName + ".xls";

        QueryWrapper<RandomForestArgument> resetWrapper = new QueryWrapper<>();
        resetWrapper.eq("use_state", 1);
        RandomForestArgument randomForestArgument = randomForestArgumentService.getOne(resetWrapper);
        if (randomForestArgument == null) {
            return Result.error(null, "系统未设置默认参数，预测失败");
        }

        // 调用flask接口，即python脚本，采用最小二乘法预测结果并返回数据
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("fileName", fileName);
        jsonObject.put("predictType", predictType);
        jsonObject.put("preDays", preDays);
        jsonObject.put("estimatorsCount", randomForestArgument.getEstimatorsCount());
        jsonObject.put("randomStates", randomForestArgument.getRandomStates());
        jsonObject.put("nJobs", randomForestArgument.getNJobs());
        RequestBody body = RequestBody.Companion.create(jsonObject.toJSONString(), mediaType);
        Request request = new Request.Builder()
                .url(flaskUrl + "/randomForest")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            JSONObject jsonObjectRes = JSONObject.parseObject(Objects.requireNonNull(response.body()).string());
            if (jsonObjectRes.getInteger("code") != 200) {
                return Result.error(null, "预测数据获取失败");
            }
            return Result.success(jsonObjectRes.get("data"), "预测数据获取成功");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(null, "接口服务调用出错");
        }
    }

    @Operation(summary = "随机森林法")
    @PostMapping("/randomForestManage")
    public Result randomForestManage(@org.springframework.web.bind.annotation.RequestBody Map<String, String> map) {
        String stockName = map.get("stockName");
        String predictType = map.get("predictType");
        Integer preDays = Integer.valueOf(map.get("preDays"));
        Integer estimatorsCount = Integer.valueOf(map.get("estimatorsCount"));
        Integer randomStates = Integer.valueOf(map.get("randomStates"));
        Integer nJobs = Integer.valueOf(map.get("nJobs"));
        String[] predictTypes = new String[]{"S", "M", "L", "SM", "ML", "SML"};
        if (Arrays.stream(predictTypes).noneMatch(s -> Objects.equals(s, predictType))) {
            return Result.error(null, "预测类型错误");
        }

        String fileName = dataDir + stockName + ".xls";

        // 调用flask接口，即python脚本，采用最小二乘法预测结果并返回数据
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("fileName", fileName);
        jsonObject.put("predictType", predictType);
        jsonObject.put("preDays", preDays);
        jsonObject.put("estimatorsCount", estimatorsCount);
        jsonObject.put("randomStates", randomStates);
        jsonObject.put("nJobs", nJobs);
        RequestBody body = RequestBody.Companion.create(jsonObject.toJSONString(), mediaType);
        Request request = new Request.Builder()
                .url(flaskUrl + "/randomForest")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            JSONObject jsonObjectRes = JSONObject.parseObject(Objects.requireNonNull(response.body()).string());
            if (jsonObjectRes.getInteger("code") != 200) {
                return Result.error(null, "预测数据获取失败");
            }
            return Result.success(jsonObjectRes.get("data"), "预测数据获取成功");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(null, "接口服务调用出错");
        }
    }

    @Operation(summary = "LSTM")
    @PostMapping("/lstm")
    public Result lstm(@org.springframework.web.bind.annotation.RequestBody Map<String, String> map) {
        String stockName = map.get("stockName");
        String predictType = map.get("predictType");
        Integer preDays = Integer.valueOf(map.get("preDays"));
        String[] predictTypes = new String[]{"S", "M", "L", "SM", "ML", "SML"};
        if (Arrays.stream(predictTypes).noneMatch(s -> Objects.equals(s, predictType))) {
            return Result.error(null, "预测类型错误");
        }

        String fileName = dataDir + stockName + ".xls";

        QueryWrapper<LstmArgument> resetWrapper = new QueryWrapper<>();
        resetWrapper.eq("use_state", 1);
        LstmArgument lstmArgument = lstmArgumentService.getOne(resetWrapper);
        if (lstmArgument == null) {
            return Result.error(null, "系统未设置默认参数，预测失败");
        }

        // 调用flask接口，即python脚本，采用最小二乘法预测结果并返回数据
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("fileName", fileName);
        jsonObject.put("predictType", predictType);
        jsonObject.put("preDays", preDays);
        jsonObject.put("lstmLayers", lstmArgument.getLstmLayers());
        jsonObject.put("denseLayers", lstmArgument.getDenseLayers());
        jsonObject.put("units", lstmArgument.getUnits());
        RequestBody body = RequestBody.Companion.create(jsonObject.toJSONString(), mediaType);
        Request request = new Request.Builder()
                .url(flaskUrl + "/lstm")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            JSONObject jsonObjectRes = JSONObject.parseObject(Objects.requireNonNull(response.body()).string());
            if (jsonObjectRes.getInteger("code") != 200) {
                return Result.error(null, "预测数据获取失败");
            }
            return Result.success(jsonObjectRes.get("data"), "预测数据获取成功");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(null, "接口服务调用出错");
        }
    }

    @Operation(summary = "LSTMManage")
    @PostMapping("/lstmManage")
    public Result lstmManage(@org.springframework.web.bind.annotation.RequestBody Map<String, String> map) {
        String stockName = map.get("stockName");
        String predictType = map.get("predictType");
        Integer preDays = Integer.valueOf(map.get("preDays"));
        Integer lstmLayers = Integer.valueOf(map.get("lstmLayers"));
        Integer denseLayers = Integer.valueOf(map.get("denseLayers"));
        Integer units = Integer.valueOf(map.get("units"));
        String[] predictTypes = new String[]{"S", "M", "L", "SM", "ML", "SML"};
        if (Arrays.stream(predictTypes).noneMatch(s -> Objects.equals(s, predictType))) {
            return Result.error(null, "预测类型错误");
        }

        String fileName = dataDir + stockName + ".xls";

        // 调用flask接口，即python脚本，采用最小二乘法预测结果并返回数据
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("fileName", fileName);
        jsonObject.put("predictType", predictType);
        jsonObject.put("preDays", preDays);
        jsonObject.put("lstmLayers", lstmLayers);
        jsonObject.put("denseLayers", denseLayers);
        jsonObject.put("units", units);
        RequestBody body = RequestBody.Companion.create(jsonObject.toJSONString(), mediaType);
        Request request = new Request.Builder()
                .url(flaskUrl + "/lstm")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            JSONObject jsonObjectRes = JSONObject.parseObject(Objects.requireNonNull(response.body()).string());
            if (jsonObjectRes.getInteger("code") != 200) {
                return Result.error(null, "预测数据获取失败");
            }
            return Result.success(jsonObjectRes.get("data"), "预测数据获取成功");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(null, "接口服务调用出错");
        }
    }

    @Operation(summary = "KNN")
    @PostMapping("/knn")
    public Result knn(@org.springframework.web.bind.annotation.RequestBody Map<String, String> map) {
        String stockName = map.get("stockName");
        String predictType = map.get("predictType");
        Integer preDays = Integer.valueOf(map.get("preDays"));
        String[] predictTypes = new String[]{"S", "M", "L", "SM", "ML", "SML"};
        if (Arrays.stream(predictTypes).noneMatch(s -> Objects.equals(s, predictType))) {
            return Result.error(null, "预测类型错误");
        }

        String fileName = dataDir + stockName + ".xls";

        // 调用flask接口，即python脚本，采用最小二乘法预测结果并返回数据
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("fileName", fileName);
        jsonObject.put("predictType", predictType);
        jsonObject.put("preDays", preDays);
        RequestBody body = RequestBody.Companion.create(jsonObject.toJSONString(), mediaType);
        Request request = new Request.Builder()
                .url(flaskUrl + "/knn")
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();
        try (Response response = HTTP_CLIENT.newCall(request).execute()) {
            JSONObject jsonObjectRes = JSONObject.parseObject(Objects.requireNonNull(response.body()).string());
            if (jsonObjectRes.getInteger("code") != 200) {
                return Result.error(null, "预测数据获取失败");
            }
            return Result.success(jsonObjectRes.get("data"), "预测数据获取成功");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error(null, "接口服务调用出错");
        }
    }
}
