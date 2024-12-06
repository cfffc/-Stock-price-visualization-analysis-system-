package com.stock.service.impl;

import com.stock.entity.StockMinuteName;
import com.stock.mapper.StockMinuteNameMapper;
import com.stock.service.IStockMinuteNameService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class StockMinuteNameServiceImpl extends ServiceImpl<StockMinuteNameMapper, StockMinuteName> implements IStockMinuteNameService {

}
