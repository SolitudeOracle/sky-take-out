package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCardService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/shoppingCart")
@Slf4j
public class ShoppingCardController {
    @Autowired
    private ShoppingCardService shoppingCardService;

    @PostMapping("/add")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        log.info("添加购物车，商品为：{}", shoppingCartDTO);
        shoppingCardService.addShoppingCard(shoppingCartDTO);
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<ShoppingCart>> list() {
        List<ShoppingCart> list = shoppingCardService.showShoppingCard();
        return Result.success(list);
    }

    @DeleteMapping("/clean")
    public Result clean() {
        shoppingCardService.cleanShoppingCard();
        return Result.success();
    }

    @PostMapping("/sub")
    public Result sub(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        shoppingCardService.sub(shoppingCartDTO);
        return Result.success();
    }
}
