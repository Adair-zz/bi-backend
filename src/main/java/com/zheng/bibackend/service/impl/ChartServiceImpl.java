package com.zheng.bibackend.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zheng.bibackend.mapper.ChartMapper;
import com.zheng.bibackend.model.entity.Chart;
import com.zheng.bibackend.service.ChartService;
import org.springframework.stereotype.Service;

/**
* @author Zheng Zhang
* @description 针对表【chart(chart)】的数据库操作Service实现
* @createDate 2023-08-04 17:14:07
*/
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, Chart>
    implements ChartService {

}




