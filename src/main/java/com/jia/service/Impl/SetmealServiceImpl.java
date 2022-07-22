package com.jia.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jia.common.CustomException;
import com.jia.domain.Setmeal;
import com.jia.domain.SetmealDish;
import com.jia.dto.SetmealDto;
import com.jia.mapper.SetmealMapper;
import com.jia.service.SetmealDishService;
import com.jia.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;
    @Transactional
    public void saveWithSetmeal(SetmealDto setmealDto) {
        this.save(setmealDto);
        Long setmealId = setmealDto.getId();
        //先保存到Setmeal表
        //取出setmealDto的SetmealDish
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        //在SetmealDish存上setmealId
        setmealDishes =  setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealId);
            return item;
        }).collect(Collectors.toList());
        //将结果存到SetmealDish表上
        setmealDishService.saveBatch(setmealDishes);

    }

    /**
     * 删除套餐同时删除套餐下的菜品表
     * @param ids
     */
    @Transactional
    public void removeBySetmealIds(List<Long> ids) {
        //查询套餐状态是否可以删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);

        int count = this.count(queryWrapper);

        if (count>0){
            throw new CustomException("选取的套餐中是起售状态，不能删除");
        }
        else
            this.removeByIds(ids);
        LambdaQueryWrapper<SetmealDish> sdqueryWrapper = new LambdaQueryWrapper<>();
        sdqueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(sdqueryWrapper);
    }

    @Transactional
    public void updateSetmeal(SetmealDto setmealDto) {

        this.updateById(setmealDto);

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.in(SetmealDish::getDishId,setmealDto.getId());

        setmealDishService.remove(queryWrapper);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(setmealDishes);


    }

    @Override
    public SetmealDto getSetmealDto(Long id) {
        Setmeal setmeal = this.getById(id);

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setmealDishService.list(queryWrapper);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);
        setmealDto.setSetmealDishes(list);
        return setmealDto;


    }

    @Override
    public void setStatus(int status, List<Long> ids) {
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper();

        queryWrapper.in(Setmeal::getId,ids);
        List<Setmeal> setmealList = this.list(queryWrapper);

//        for (Setmeal setmeal : setmealList) {
//            setmeal.setStatus(status);
//        }

        setmealList = setmealList.stream().map((item)->{
            item.setStatus(status);
            return item;
        }).collect(Collectors.toList());


        this.updateBatchById(setmealList);





    }
}
