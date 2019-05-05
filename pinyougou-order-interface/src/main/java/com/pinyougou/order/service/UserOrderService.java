package com.pinyougou.order.service;

import com.pinyougou.pojogroup.UserOrders;

import java.util.List;

public interface UserOrderService {
    /**
     * 根据用户名查找用户所有的订单
     * @param userId
     * @return
     */
    List<UserOrders> findAllOrders(String userId);
}
