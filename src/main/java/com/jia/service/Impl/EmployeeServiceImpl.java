package com.jia.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jia.domain.Employee;
import com.jia.mapper.EmployeeMapper;
import com.jia.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;


@Service
@Slf4j
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper,Employee> implements EmployeeService {
    @Override
    public boolean save(HttpServletRequest request, Employee entity) {
        log.info("保存员工：{}",entity.getUsername());
        entity.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        Long userId=(long) request.getSession().getAttribute("employee");
        entity.setCreateTime(LocalDateTime.now());
        entity.setUpdateTime(LocalDateTime.now());
        entity.setCreateUser(userId);
        entity.setUpdateUser(userId);
        return super.save(entity);
    }
}
