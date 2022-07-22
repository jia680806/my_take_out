package com.jia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jia.domain.Dish;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DishMapper extends BaseMapper<Dish> {
}
