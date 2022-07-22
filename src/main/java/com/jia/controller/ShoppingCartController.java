package com.jia.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.jia.common.BaseContext;
import com.jia.common.R;
import com.jia.domain.ShoppingCart;
import com.jia.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;

    @GetMapping("/list")
    public R<List<ShoppingCart>> list(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCartService.list(queryWrapper);
        return R.success(list);
    }

    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        Long dishId = shoppingCart.getDishId();
        //设置用户Id
        Long userId = BaseContext.getCurrentId();
        //设置User 的id
        shoppingCart.setUserId(userId);


        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();

        //查询这个user下的菜品和套餐
        queryWrapper.eq(ShoppingCart::getUserId,userId);

        if (dishId != null){
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
            queryWrapper.eq(ShoppingCart::getDishFlavor,shoppingCart.getDishFlavor());

        }else {
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());

        }
        ShoppingCart shoppingCartOne = shoppingCartService.getOne(queryWrapper);

        //如果有菜品或者套餐数量加1
        if (shoppingCartOne != null){
            Integer number = shoppingCartOne.getNumber();
            shoppingCartOne.setNumber(number+1);
            shoppingCartService.updateById(shoppingCartOne);
        }else{
            //如果没有就添加数据
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            shoppingCartOne = shoppingCart;
        }
        return R.success(shoppingCartOne);

    }
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){

        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();
        //设置用户Id
        Long userId = BaseContext.getCurrentId();
        //设置User 的id
        shoppingCart.setUserId(userId);


        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);


        if (dishId != null){
            //菜品
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
            queryWrapper.eq(ShoppingCart::getDishFlavor,shoppingCart.getDishFlavor());

        }else {
            //套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,setmealId);

        }
        ShoppingCart serviceOne = shoppingCartService.getOne(queryWrapper);

        Integer number = serviceOne.getNumber();

        if (number == 1){
            shoppingCartService.removeById(serviceOne);
        }else {
            serviceOne.setNumber(number - 1);
            shoppingCartService.updateById(serviceOne);
        }

        return R.success(serviceOne);

    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> clean(){
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartService.remove(queryWrapper);
        return R.success("购物车清空");
    }

}
