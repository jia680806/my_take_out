package com.jia.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jia.common.R;
import com.jia.domain.*;
import com.jia.dto.DishDto;
import com.jia.dto.SetmealDto;
import com.jia.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private DishService dishService;

//    @Autowired
//    private DishFlavorService dishFlavorService;


    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {

        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.like(name != null, Setmeal::getName, name);

        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        setmealService.page(setmealPage, queryWrapper);
        //将处理好的Dish分页复制到DishDto，除Records
        BeanUtils.copyProperties(setmealPage, setmealDtoPage, "records");
        //提取setmealPage的Records
        List<Setmeal> records = setmealPage.getRecords();
        //改造Records
        List<SetmealDto> setmealDtos = records.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            //复制Setmeal到SetmealDto
            BeanUtils.copyProperties(item, setmealDto);
            //查询category，将categoryName赋值给SetmealDto
            Category category = categoryService.getById(item.getCategoryId());
            String categoryName = category.getName();
            setmealDto.setCategoryName(categoryName);
            return setmealDto;
        }).collect(Collectors.toList());

        setmealDtoPage.setRecords(setmealDtos);

        return R.success(setmealDtoPage);

    }

    @PostMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithSetmeal(setmealDto);
        return R.success("保存成功！");
    }

    @DeleteMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> remove(@RequestParam List<Long> ids) {
        setmealService.removeBySetmealIds(ids);
        return R.success("删除成功！");
    }

    @PutMapping
    @CacheEvict(value = "setmealCache",key = "'setmeal_'+#setmealDto.categoryId")
    public R<String> update(@RequestBody SetmealDto setmealDto) {
        setmealService.updateSetmeal(setmealDto);
        return R.success("删除成功！");
    }

    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id) {
        SetmealDto setmealDto = setmealService.getSetmealDto(id);
        return R.success(setmealDto);

    }

    @PostMapping("//status/{status}")
    public R<String> status(@PathVariable int status, @RequestParam List<Long> ids) {
        setmealService.setStatus(status, ids);
        return R.success("状态更新成功");
    }

    @GetMapping("/list")
    @Cacheable(value = "setmealCache" ,key = "'setmeal_'+#setmeal.categoryId")
    public R<List<SetmealDto>> list(Setmeal setmeal) {

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getCategoryId, setmeal.getCategoryId());
        queryWrapper.eq(Setmeal::getStatus,setmeal.getStatus());
        List<Setmeal> list = setmealService.list(queryWrapper);


        List<SetmealDto> setmealDtos = list.stream().map((item) -> {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item, setmealDto);

            Category category = categoryService.getById(item.getCategoryId());
            setmealDto.setCategoryName(category.getName());

            LambdaQueryWrapper<SetmealDish> squeryWrapper = new LambdaQueryWrapper<>();
            squeryWrapper.eq(SetmealDish::getDishId, item.getId());
            List<SetmealDish> SetmealDishList = setmealDishService.list(squeryWrapper);
            setmealDto.setSetmealDishes(SetmealDishList);
            return setmealDto;
        }).collect(Collectors.toList());

        return R.success(setmealDtos);
    }


    @GetMapping("/dish/{id}")
    public R<List<DishDto>> getDish(@PathVariable Long id){
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        List<DishDto> dishDtoList= list.stream().map((item)->{
            DishDto dishDto = new DishDto();
            Dish dish = dishService.getById(item.getDishId());
            BeanUtils.copyProperties(dish,dishDto);

            dishDto.setCopies(item.getCopies());

//            LambdaQueryWrapper<DishFlavor> dfqueryWrapper = new LambdaQueryWrapper<>();
//            dfqueryWrapper.eq(DishFlavor::getDishId,item.getDishId());
//            List<DishFlavor> dishFlavorList = dishFlavorService.list(dfqueryWrapper);
//            dishDto.setFlavors(dishFlavorList);

            return dishDto;

        }).collect(Collectors.toList());
        return R.success(dishDtoList);

    }


}
