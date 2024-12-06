package com.stock.service.impl;

import com.stock.entity.StockData;
import com.stock.mapper.StockDataMapper;
import com.stock.service.IStockDataService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class StockDataServiceImpl extends ServiceImpl<StockDataMapper, StockData> implements IStockDataService {

}
