package com.jia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jia.domain.Dish;
import com.jia.dto.DishDto;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

public interface DishService extends IService<Dish> {
    public void saveWithDish(DishDto dishDto);
    public DishDto getByIdWithFlavor(Long id);
    public void updateWithDish(DishDto dishDto);
    public void statusWithDish(List<Long> ids,int status);
    public void removeByDishIds(List<Long> ids);
}
