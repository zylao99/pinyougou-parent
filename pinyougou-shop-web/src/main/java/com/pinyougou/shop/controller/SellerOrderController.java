package com.pinyougou.shop.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.SellerOrderService;
import com.pinyougou.pojo.Sale;
import com.pinyougou.pojogroup.SalesSituationVO;
import com.pinyougou.pojogroup.UserOrders;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/sellerOrder")
public class SellerOrderController {

    @Reference
    private SellerOrderService sellerOrderService;

    @RequestMapping("/findSellerOrders")
    public List<UserOrders> findSellerOrders(){
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<UserOrders> allOrders = sellerOrderService.findAllOrders(sellerId);
        for (UserOrders allOrder : allOrders) {
            allOrder.setId(allOrder.getTbOrder().getOrderId()+"");
        }

        return allOrders;
    }

    @RequestMapping("/salesSituation")
    public List<Sale> getSalesSituation(SalesSituationVO salesSituationVO ){

        return sellerOrderService.getSalesSituation(salesSituationVO);
    }

}
