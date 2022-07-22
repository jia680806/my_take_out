package com.jia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jia.domain.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper  extends BaseMapper<User> {
}
