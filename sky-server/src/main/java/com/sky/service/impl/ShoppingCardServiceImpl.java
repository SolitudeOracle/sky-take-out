package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCardMapper;
import com.sky.service.ShoppingCardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCardServiceImpl implements ShoppingCardService {
    @Autowired
    private ShoppingCardMapper shoppingCardMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public void addShoppingCard(ShoppingCartDTO shoppingCartDTO) {
        // 1.判断当前商品是否在购物车中，select
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCardMapper.list(shoppingCart);

        // 2.如果在，则数量加1，update
        if (list != null && list.size() > 0) {
            ShoppingCart cart = list.get(0);
            cart.setNumber(cart.getNumber() + 1);
            shoppingCardMapper.updateNumber(cart);
        } else {
            // 3.如果不在，则添加到购物车，数量默认为1，insert
            Long dishId = shoppingCart.getDishId();
            if (dishId != null) {
                // 3.1.菜品
                Dish dish = dishMapper.getById(dishId);

                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());

            } else {
                // 3.2.套餐
                Long setmealId = shoppingCart.getSetmealId();

                Setmeal setmeal = setmealMapper.getById(setmealId);

                shoppingCart.setName(setmeal.getName());
                shoppingCart.setImage(setmeal.getImage());
                shoppingCart.setAmount(setmeal.getPrice());
            }
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());

            shoppingCardMapper.insert(shoppingCart);
        }


    }

    @Override
    public List<ShoppingCart> showShoppingCard() {
        // 1.构建shoppingCart对象
        ShoppingCart shoppingCart = ShoppingCart.builder()
                .userId(BaseContext.getCurrentId())
                .build();

        // 2.调用mapper查询
        return shoppingCardMapper.list(shoppingCart);
    }

    @Override
    public void cleanShoppingCard() {
        Long userId = BaseContext.getCurrentId();

        shoppingCardMapper.deleteByUserId(userId);
    }

    @Override
    public void sub(ShoppingCartDTO shoppingCartDTO) {
        // 1.判断当前商品是否在购物车中，select
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(BaseContext.getCurrentId());
        List<ShoppingCart> list = shoppingCardMapper.list(shoppingCart);

        // 2.如果数量大于1则减1，数量等于1则删除，update
        if (list != null && list.size() > 0) {
            ShoppingCart cart = list.get(0);

            if (cart.getNumber() == 1) {
                shoppingCardMapper.deleteById(cart.getId());
                return;
            }

            cart.setNumber(cart.getNumber() - 1);
            shoppingCardMapper.updateNumber(cart);
        }
    }
}
