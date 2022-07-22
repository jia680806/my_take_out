package com.jia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jia.domain.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
