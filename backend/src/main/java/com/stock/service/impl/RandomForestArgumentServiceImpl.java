package com.stock.service.impl;

import com.stock.entity.RandomForestArgument;
import com.stock.mapper.RandomForestArgumentMapper;
import com.stock.service.IRandomForestArgumentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class RandomForestArgumentServiceImpl extends ServiceImpl<RandomForestArgumentMapper, RandomForestArgument> implements IRandomForestArgumentService {

}
