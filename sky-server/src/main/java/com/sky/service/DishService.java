package com.sky.service;

import com.sky.dto.DishDTO;

public interface DishService {
    /**
     * 新增菜品，同时插入口味数据
     * @param dishDTO
     */
    public void saveWithFlavor(DishDTO dishDTO);
}
