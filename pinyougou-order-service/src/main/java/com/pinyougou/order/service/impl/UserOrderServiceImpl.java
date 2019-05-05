package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.order.service.UserOrderService;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderExample;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojo.TbOrderItemExample;
import com.pinyougou.pojogroup.UserOrders;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
@Service
public class UserOrderServiceImpl implements UserOrderService {

    @Autowired
    private TbOrderMapper tbOrderMapper;


    @Autowired
    private TbOrderItemMapper tbOrderItemMapper;



    @Override
    public List<UserOrders> findAllOrders(String userId) {



        //创建一个用户订单的集合
        ArrayList<UserOrders> list = new ArrayList<>();
        //根据用户名得到用户对用的订单

        TbOrderExample tbOrderExample = new TbOrderExample();
        TbOrderExample.Criteria criteria = tbOrderExample.createCriteria();
        criteria.andUserIdEqualTo(userId);
        List<TbOrder> tbOrders = tbOrderMapper.selectByExample(tbOrderExample);

        for (TbOrder tbOrder : tbOrders) {

            UserOrders userOrders =new UserOrders();
            userOrders.setTbOrder(tbOrder);
            Long orderId = tbOrder.getOrderId();
            TbOrderItemExample tbOrderItemExample=new TbOrderItemExample();
            TbOrderItemExample.Criteria criteria1 = tbOrderItemExample.createCriteria();
            criteria1.andOrderIdEqualTo(orderId);

            //根据得到订单id得到对应的商品列表 放入
            List<TbOrderItem> tbOrderItems = tbOrderItemMapper.selectByExample(tbOrderItemExample);
            userOrders.setTbOrderItems(tbOrderItems);

            //存入一个用户订单
            list.add(userOrders);
        }
        return list;
    }
}
