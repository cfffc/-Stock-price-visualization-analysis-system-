package com.stock.service.impl;

import com.stock.entity.StockName;
import com.stock.mapper.StockNameMapper;
import com.stock.service.IStockNameService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class StockNameServiceImpl extends ServiceImpl<StockNameMapper, StockName> implements IStockNameService {

}
