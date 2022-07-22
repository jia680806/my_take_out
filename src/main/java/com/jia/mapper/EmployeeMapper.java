package com.jia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jia.domain.Employee;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
}
