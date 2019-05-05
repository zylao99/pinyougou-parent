package com.pinyougou.order.service;

import com.pinyougou.pojo.Sale;
import com.pinyougou.pojogroup.SalesSituationVO;
import com.pinyougou.pojogroup.UserOrders;

import java.util.List;

public interface SellerOrderService {
    List<UserOrders> findAllOrders(String sellerId);

    List<Sale> getSalesSituation(SalesSituationVO salesSituationVO);
}
