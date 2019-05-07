package com.pinyougou.user.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WXPayService;
import com.pinyougou.pojo.TbOrder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController {

    @Reference
    private WXPayService weixinPayService;

    @Reference
    private OrderService orderService;

    @RequestMapping("/createNative")
    public Map createNative(Long orderId){
        IdWorker idworker=new IdWorker(0,1);
        System.out.println("雪花Id:"+idworker.nextId());
        String A= idworker.nextId()+"";
        System.out.println("A:"+A);
        //根据订单号码查找应付金额
        TbOrder tbOrder = orderService.findOne(orderId);
        System.out.println(tbOrder);

        System.out.println("订单Id:"+orderId);
        int payment = (int) (tbOrder.getPayment().setScale(2).doubleValue()*100);
        System.out.println("应付金额："+payment);
       return weixinPayService.createNative(idworker.nextId()+"",payment+"");
       // Map map = new HashMap();
       // map.put("2525",5000);
       // return map;
    }
}
