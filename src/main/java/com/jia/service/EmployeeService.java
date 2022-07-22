package com.jia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jia.domain.Employee;

import javax.servlet.http.HttpServletRequest;

public interface EmployeeService extends IService<Employee> {
    boolean save(HttpServletRequest request, Employee entity);
}
