package com.pinyougou.pojogroup;

import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbOrderItem;

import java.io.Serializable;
import java.util.List;

public class UserOrders implements Serializable {
    private String id; //数字范围超过了jeson最大表示的范围后使用String保存
    private TbOrder tbOrder; //用户下的订单
    private List<TbOrderItem> tbOrderItems;//订单所对应的商品详情

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public TbOrder getTbOrder() {
        return tbOrder;
    }

    public void setTbOrder(TbOrder tbOrder) {
        this.tbOrder = tbOrder;
    }

    public List<TbOrderItem> getTbOrderItems() {
        return tbOrderItems;
    }

    public void setTbOrderItems(List<TbOrderItem> tbOrderItems) {
        this.tbOrderItems = tbOrderItems;
    }
}
