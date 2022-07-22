package com.jia.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jia.common.R;
import com.jia.domain.Employee;
import com.jia.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@RestController
@Slf4j
@RequestMapping("/employee")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        //1、将页面提交的密码password进行md5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //2、根据页面提交的用户名username查询数据库
        String username = employee.getUsername();
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        LambdaQueryWrapper<Employee> eq = queryWrapper.eq(Employee::getUsername, username);
        Employee emp = employeeService.getOne(eq);

        //3、如果没有查询到则返回登录失败结果
        if (emp == null) {
            return R.error("用户不存在");
        }
        //4、密码比对，如果不一致则返回登录失败结果
        if (!emp.getPassword().equals(password)) {
            return R.error("用户名或密码错误");
        }
        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus() == 0) {
            return R.error("该用户已冻结");
        }
        //6、登录成功，将员工id存入Session并返回登录成功结果
        request.getSession().setAttribute("employee",emp.getId());

        return R.success(emp);

    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }


    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        employeeService.save(request,employee);

        return R.success("保存成功！");
    }


    @GetMapping("/page")
    public R<Page> page(int page,int pageSize,String name){
    log.info("page ={},pageSize = {},name ={}",page,pageSize,name);
    LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
    Page pageinfo = new Page(page,pageSize);
    //根据姓名 模糊搜索
    if (name!=null) {
        queryWrapper.like(Employee::getName, name);
    }
    //根据更新时间排序
    queryWrapper.orderByDesc(Employee::getUpdateTime);

    //执行查询
    employeeService.page(pageinfo,queryWrapper);


    return R.success(pageinfo);
    }

    @PutMapping
    public R<String> status(@RequestBody Employee employee){
        employeeService.updateById(employee);
        return R.success("修改成功");
    }

    @GetMapping("/{empId}")
    public R<Employee> getById(@PathVariable Long empId){
        Employee employee =  employeeService.getById(empId);
        return R.success(employee);
    }

}
