package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCardService {
    void addShoppingCard(ShoppingCartDTO shoppingCartDTO);

    List<ShoppingCart> showShoppingCard();

    void cleanShoppingCard();
}
