package com.jia.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jia.common.CustomException;
import com.jia.domain.Dish;
import com.jia.domain.DishFlavor;
import com.jia.dto.DishDto;
import com.jia.mapper.DishMapper;
import com.jia.service.DishFlavorService;
import com.jia.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    @Transactional
    public void saveWithDish(DishDto dishDto) {
        super.save(dishDto);
        Long dishId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);

    }
    //根据Id查询菜品信息和对应口味信息
    @Override
    public DishDto getByIdWithFlavor(Long id){
        //菜品信息

        Dish dish = super.getById(id);
        //口味信息
        LambdaQueryWrapper<DishFlavor> flavorqueryWrapper = new LambdaQueryWrapper<>();
        flavorqueryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> list = dishFlavorService.list(flavorqueryWrapper);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        dishDto.setFlavors(list);
        return dishDto;
    }

    @Transactional
    public void updateWithDish(DishDto dishDto) {
        this.updateById(dishDto);

        Long dishId = dishDto.getId();

        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishId);


        dishFlavorService.remove(queryWrapper);

        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);


    }

    @Override
    public void statusWithDish(List<Long>ids, int status) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId,ids);

        List<Dish> dishList = this.list(queryWrapper);

        dishList = dishList.stream().map((item)->{
            item.setStatus(status);
            return item;
        }).collect(Collectors.toList());

        this.updateBatchById(dishList);




//        Dish dish;
//        //判断是否包含，
//        if (ids.contains(",")) {
//            //分割，
//            String[] split = ids.split(",");
//            //讲string转换Long 保存Long ist
//            List<Long> idList = new ArrayList<>();
//            for (String s : split) {
//                idList.add(Long.parseLong(s));
//            }
//
//            for (Long id : idList) {
//                dish = super.getById(id);
//                dish.setStatus(status);
//                super.updateById(dish);
//            }
//
//
//        } else {
//            dish = this.getById(ids);
//            dish.setStatus(status);
//            this.updateById(dish);

//        }
    }

    @Transactional
    public void removeByDishIds(List<Long> ids) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.in(Dish::getId, ids);
        queryWrapper.eq(Dish::getStatus, 1);
        int count = this.count(queryWrapper);

        if (count > 0) {
            throw new CustomException("选取的菜品中是起售状态，不能删除");
        }else {
            this.removeByIds(ids);
            LambdaQueryWrapper<DishFlavor> dfQueryWrapper = new LambdaQueryWrapper<>();
            dfQueryWrapper.in(DishFlavor::getDishId,ids);
            dishFlavorService.remove(dfQueryWrapper);
        }




//        if(ids.contains(",")){
//            String[] split = ids.split(",");
//            List<Long> longIds = new ArrayList<>();
//            for (String s : split) {
//                longIds.add(Long.parseLong(s));
//            }
//            for (Long id : longIds) {
//                this.removeById(id);
//            }
//
//
//        }else {
//            this.removeById(ids);
//        }
    }


}

