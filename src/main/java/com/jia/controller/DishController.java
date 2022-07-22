package com.jia.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jia.common.R;
import com.jia.domain.Category;
import com.jia.domain.Dish;
import com.jia.domain.DishFlavor;
import com.jia.dto.DishDto;
import com.jia.service.CategoryService;
import com.jia.service.DishFlavorService;
import com.jia.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@RestController
@Slf4j
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize,String name){
        Page<Dish> pageInfo =new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();


        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();

//      if (name != null)
        queryWrapper.like(name != null,Dish::getName,name);

        queryWrapper.orderByDesc(Dish::getUpdateTime);

        pageInfo = dishService.page(pageInfo,queryWrapper);


        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");

        List<Dish> records = pageInfo.getRecords();

        List<DishDto> dishDtos =  records.stream().map((item)->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            Category category = categoryService.getById(item.getCategoryId());
            String categoryName = category.getName();
            dishDto.setCategoryName(categoryName);
            return dishDto;
        }).collect(Collectors.toList());

        dishDtoPage.setRecords(dishDtos);



        return R.success(dishDtoPage);

    }

    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        dishService.saveWithDish(dishDto);

        return R.success("菜品保存成功");
    }
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){
        DishDto dishDto= dishService.getByIdWithFlavor(id);
        return R.success(dishDto);
    }

    @PutMapping
    public R<String> updateWithDish(@RequestBody DishDto dishDto){
        dishService.updateWithDish(dishDto);
        String key = "dish_"+dishDto.getCategoryId();
        redisTemplate.delete(key);

        return R.success("菜品内容更新成功");
    }

    @PostMapping("/status/{status}")
    public  R<String> statusWithDish(@RequestParam List<Long> ids,@PathVariable int status){
        dishService.statusWithDish(ids,status);
        return R.success("菜品状态更新成功");

    }


    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        dishService.removeByDishIds(ids);
        return R.success("删除成功");
    }
//    @GetMapping("/list")
//    public R<List<Dish>> addDish(String categoryId){
//        LambdaQueryWrapper<Dish> queryWrapper =new LambdaQueryWrapper<>();
//        queryWrapper.eq(Dish::getCategoryId,categoryId);
//        List<Dish> list = dishService.list(queryWrapper);
//        return R.success(list);
//    }

    @GetMapping("/list")
    public R<List<DishDto>> addDish(Dish dish){
        List<DishDto> dishdtos =null;
        //设置Redis的key
        String key = "dish_"+dish.getCategoryId();
        //通过key取出value
        dishdtos =(List<DishDto>) redisTemplate.opsForValue().get(key);
        //如果value不为空 则直接返回
        if (dishdtos != null){
            return R.success(dishdtos);
        }


        log.info(dish.toString());

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId,dish.getCategoryId());
        queryWrapper.eq(Dish::getStatus,dish.getStatus());
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> dishes= dishService.list(queryWrapper);
        dishdtos = dishes.stream().map((item)->{
            //copy Dish ->DishDto
            DishDto dishDto =new DishDto();
            BeanUtils.copyProperties(item,dishDto);
            //将categoryId赋值给DishDto
            Long categoryId = item.getCategoryId();
            Category category = categoryService.getById(categoryId);
            dishDto.setCategoryName(category.getName());
            //将dishFlavorlist赋值给DishDto
            LambdaQueryWrapper<DishFlavor> dfqueryWrapper = new LambdaQueryWrapper<>();
            dfqueryWrapper.eq(DishFlavor::getDishId,item.getId());
            List<DishFlavor> dishFlavorList = dishFlavorService.list(dfqueryWrapper);
            dishDto.setFlavors(dishFlavorList);
            return dishDto;

        }).collect(Collectors.toList());

        redisTemplate.opsForValue().set(key,dishdtos,1, TimeUnit.HOURS);


        return R.success(dishdtos);
    }


}
