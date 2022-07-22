package com.jia.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jia.common.R;
import com.jia.domain.User;
import com.jia.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;


@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;



    @PostMapping("/login")
    public R<String> login(HttpServletRequest Request, @RequestBody User user){

        String phone = user.getPhone();
        if (phone != null) {
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);
            User getUser = userService.getOne(queryWrapper);
            if (getUser==null) {
                userService.save(user);
            }
            User User1 = userService.getOne(queryWrapper);
            Request.getSession().setAttribute("user",User1.getId());

            return R.success("登录成功");

        }

        return R.error("登录失败");


    }
}
