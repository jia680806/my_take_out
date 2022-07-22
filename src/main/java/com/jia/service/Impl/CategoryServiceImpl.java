package com.jia.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jia.common.CustomException;
import com.jia.common.R;
import com.jia.domain.Category;
import com.jia.domain.Dish;
import com.jia.domain.Setmeal;
import com.jia.mapper.CategoryMapper;
import com.jia.service.CategoryService;

import com.jia.service.DishService;
import com.jia.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService ;

    @Autowired
    private SetmealService setmealService;

    public void remove(Long id) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId,id);
        Map<String, Object> map = dishService.getMap(queryWrapper);

        if(map!=null){
            throw new CustomException("该分类下有菜品，删除失败");
//            return R.error("该套餐有菜品，删除失败");

        }

        LambdaQueryWrapper<Setmeal> squeryWrapper = new LambdaQueryWrapper<>();
        squeryWrapper.eq(Setmeal::getCategoryId,id);
        Map<String, Object> smap = setmealService.getMap(squeryWrapper);
        if (smap != null){
            throw new CustomException("该套餐有餐品，删除失败");
//            return R.error("该套餐有餐品，删除失败");
        }
        super.removeById(id);
//        return R.success("删除成功");

    }
}
