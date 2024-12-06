package com.stock.service.impl;

import com.stock.entity.StockMinuteData;
import com.stock.mapper.StockMinuteDataMapper;
import com.stock.service.IStockMinuteDataService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class StockMinuteDataServiceImpl extends ServiceImpl<StockMinuteDataMapper, StockMinuteData> implements IStockMinuteDataService {

}
