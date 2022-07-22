package com.jia.service;

import com.baomidou.mybatisplus.extension.service.IService;

import com.jia.domain.Category;
import org.springframework.stereotype.Service;


@Service
public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
