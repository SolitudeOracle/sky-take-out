package com.sky.service.impl;

import com.sky.dto.SetmealDTO;
import com.sky.entity.Setmeal;
import com.sky.mapper.SetmealMapper;
import com.sky.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Override
    public void save(SetmealDTO setmealDTO) {
        // 1.封装到一个Entity对象里
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        // 2.调用mapper
        setmealMapper.insert(setmeal);
    }
}
