package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.UserOrderService;
import com.pinyougou.pojogroup.UserOrders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/findOrders")
public class FindOrderController {
    @Reference
    private UserOrderService userOrderService;

    @RequestMapping("/findAllOrders")
    public List<UserOrders> findAllOrders() {
        //查找出该用户的所有订单返回给页面
        //获得用户的登陆名
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        //根据用户名查找得到对应的UserOrders
        List<UserOrders> allOrders = userOrderService.findAllOrders(userId);

        for (UserOrders allOrder : allOrders) {
            allOrder.setId(allOrder.getTbOrder().getOrderId()+"");
        }

        return allOrders;
    }

}
