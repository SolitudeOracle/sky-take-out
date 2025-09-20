package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.DishFlavor;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Override
    @Transactional
    public void save(SetmealDTO setmealDTO) {
        // 1.封装到一个Entity对象里
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        // 2.调用mapper插入1条套餐数据
        setmealMapper.insert(setmeal);

        // 3.调用mapper插入n条菜品数据
        List<SetmealDish> setmealDishList = setmealDTO.getSetmealDishes();
        if (setmealDishList != null && setmealDishList.size() > 0) {
            setmealDishList.forEach(sd -> {
                sd.setSetmealId(setmeal.getId());
            });
            setmealDishMapper.insertBatch(setmealDishList);
        }

    }

    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        // 1.开启分页
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());

        // 2.封装page对象
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);

        // 3.返回PageResult对象
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void deleteBatch(List<Long> ids) {
        // 1.删除套餐表中的数据
        setmealMapper.deleteBatch(ids);

        // 2.删除套餐与菜品的关联数据
        setmealDishMapper.deleteBySetmealIds(ids);
    }

    @Override
    public SetmealVO getById(Long id) {
        // 1.查询套餐数据
        Setmeal setmeal = setmealMapper.getById(id);

        // 2.查询套餐关联的菜品数据
        List<SetmealDish> setmealDishes = setmealDishMapper.getBySetmealId(id);

        // 3.封装到一个Entity里
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);

        return setmealVO;
    }

    @Override
    @Transactional
    public void update(SetmealDTO setmealDTO) {
        // 1.删除旧套餐
        // 1.1.根据id删除现在的套餐
        ArrayList<Long> ids = new ArrayList<>();
        Long id = setmealDTO.getId();
        ids.add(id);

        setmealMapper.deleteBatch(ids);

        // 1.2.在删除该套餐在关系表中的数据
        setmealDishMapper.deleteBySetmealId(id);

        // 2.插入新套餐
        // 2.1.封装到一个Entity对象里
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        // 2.2.调用mapper插入1条套餐数据
        setmealMapper.insert(setmeal);

        // 2.3.调用mapper插入n条菜品数据
        List<SetmealDish> setmealDishList = setmealDTO.getSetmealDishes();
        if (setmealDishList != null && setmealDishList.size() > 0) {
            setmealDishList.forEach(sd -> {
                sd.setSetmealId(setmeal.getId());
            });
            setmealDishMapper.insertBatch(setmealDishList);
        }
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        // 1.创建一个Entity对象
        Setmeal setmeal = Setmeal.builder()
                .status(status)
                .id(id)
                .build();

        // 2.调用mapper修改
        setmealMapper.update(setmeal);
    }
}
