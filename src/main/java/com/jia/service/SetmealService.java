package com.jia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jia.domain.Setmeal;
import com.jia.dto.SetmealDto;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    public void saveWithSetmeal(SetmealDto setmealDto);
    public void removeBySetmealIds( List<Long> ids);
    public void updateSetmeal(SetmealDto setmealDto);
    public SetmealDto getSetmealDto(Long id);
    public void setStatus (int status,List<Long> ids);
}
