package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {
    @Autowired
    private OrderMapper orderMapper;

    @Scheduled(cron = "0 1 * * * ? ")
    public void processTimeoutOrder() {
        log.info("处理支付超时订单：{}", LocalDateTime.now());

        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);

        List<Orders> list = orderMapper.getByStatusAndOrderTimeOut(Orders.PENDING_PAYMENT, time);

        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(orders -> {
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("支付超时");
                orders.setCancelTime(LocalDateTime.now());
                orderMapper.update(orders);
            });
        }
    }

    @Scheduled(cron = "0 0 1 * * ? ")
    public void processDeliveryOrder() {
        log.info("处理处于待派送状态的订单：{}", LocalDateTime.now());

        LocalDateTime time = LocalDateTime.now().plusMinutes(-60);

        List<Orders> list = orderMapper.getByStatusAndOrderTimeOut(Orders.DELIVERY_IN_PROGRESS, time);

        if (!CollectionUtils.isEmpty(list)) {
            list.forEach(orders -> {
                orders.setStatus(Orders.COMPLETED);
                orderMapper.update(orders);
            });
        }
    }
}
