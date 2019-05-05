package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.order.service.SellerOrderService;
import com.pinyougou.pojo.*;
import com.pinyougou.pojogroup.SalesSituationVO;
import com.pinyougou.pojogroup.SalesSituationVOSQL;
import com.pinyougou.pojogroup.UserOrders;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
@Service
public class SellerOrderServiceImpl implements SellerOrderService {

    @Autowired
    private TbOrderMapper tbOrderMapper;


    @Autowired
    private TbOrderItemMapper tbOrderItemMapper;

    @Override
    public List<UserOrders> findAllOrders(String sellerId) {

        //创建一个用户订单的集合
        ArrayList<UserOrders> list = new ArrayList<>();
        //根据商户名得到用户对用的订单

        TbOrderExample tbOrderExample = new TbOrderExample();
        TbOrderExample.Criteria criteria = tbOrderExample.createCriteria();
        criteria.andSellerIdEqualTo(sellerId);
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

    @Override
    public List<Sale> getSalesSituation(SalesSituationVO salesSituationVO) {
        System.out.println("============"+salesSituationVO.getEndTime());
        System.out.println("============"+salesSituationVO.getGoodsId());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String startTime = df.format(salesSituationVO.getStartTime());
        String endTime =df.format(salesSituationVO.getEndTime());

        SalesSituationVOSQL salesSituationVOSQL = new SalesSituationVOSQL(startTime,endTime,salesSituationVO.getGoodsId());


        List<Sale> salesSituations = tbOrderMapper.getSalesSituation(salesSituationVOSQL);
        return salesSituations;
    }
}
