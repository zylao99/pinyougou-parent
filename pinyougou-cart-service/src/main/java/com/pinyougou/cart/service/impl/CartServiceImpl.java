package com.pinyougou.cart.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.cart.service.impl *
 * @since 1.0
 */
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private TbItemMapper itemMapper;
    @Override
    public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
        //1.先获取商品的数据(SKU的数据)
        TbItem tbItem = itemMapper.selectByPrimaryKey(itemId);
        //2.先获取商品所属的商家的ID
        String sellerId = tbItem.getSellerId();

       Cart cart =  searchCartBySellerId(cartList,sellerId);

       if(cart==null) {
           //3.判断商家的ID 是否在购物车列表中存在  如果 没有存在 ，直接添加商品
           cart = new Cart();
           cart.setSellerId(sellerId);
           cart.setSellerName(tbItem.getSeller());
           List<TbOrderItem> orderitemlist = new ArrayList<>();
           TbOrderItem tbOrderItem = new TbOrderItem();//补充值
           tbOrderItem.setItemId(itemId);
           tbOrderItem.setGoodsId(tbItem.getGoodsId());
           tbOrderItem.setNum(num);
           tbOrderItem.setPicPath(tbItem.getImage());//图片
           tbOrderItem.setPrice(tbItem.getPrice());//价格
           tbOrderItem.setSellerId(sellerId);//卖家
           tbOrderItem.setTitle(tbItem.getTitle());//商品的标题
           double v = num * (tbItem.getPrice().doubleValue());
           tbOrderItem.setTotalFee(new BigDecimal(v));//小计

           orderitemlist.add(tbOrderItem);
           cart.setOrderItemList(orderitemlist);

           cartList.add(cart);
       }else {
           //4.判断商家的ID 是否在购物车列表中存在  如果 存在


           List<TbOrderItem> orderItemList = cart.getOrderItemList();//明细列表
           //从明细中判断 是否有要添加的商品
           TbOrderItem orderItem=searchOrderItemByItemId(orderItemList,itemId);
           if(orderItem==null){
               //4.1 判断 要添加的商品 是否在该商家的购物车明细列表存在  如果 没有 ，直接添加商品
               orderItem= new TbOrderItem();
               orderItem.setItemId(itemId);
               orderItem.setGoodsId(tbItem.getGoodsId());
               orderItem.setNum(num);
               orderItem.setPicPath(tbItem.getImage());//图片
               orderItem.setPrice(tbItem.getPrice());//价格
               orderItem.setSellerId(sellerId);//卖家
               orderItem.setTitle(tbItem.getTitle());//商品的标题
               double v = num * (tbItem.getPrice().doubleValue());
               orderItem.setTotalFee(new BigDecimal(v));//小计
               orderItemList.add(orderItem);
           }else {

               //4.2 判断 要添加的商品 是否在该商家的购物车明细列表存在  如果 有,数量相加
               //数量相加
               orderItem.setNum(orderItem.getNum()+num);
               //金额重新计算
               double v = orderItem.getNum() * (orderItem.getPrice().doubleValue());
               orderItem.setTotalFee(new BigDecimal(v));//重新计算小计

               if(orderItem.getNum()<=0){
                   //移除
                   orderItemList.remove(orderItem);
               }

               //如果 商家没有任何商品 把商家购物车对象删除掉
               if(orderItemList.size()<=0){
                   cartList.remove(cart);
               }


           }
       }
        return cartList;
    }

    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public void addCartListToRedis(List<Cart> cartList, String username) {
        redisTemplate.boundHashOps("REDIS_CART_LIST_KEY_").put(username,cartList);
    }

    @Override
    public List<Cart> findCartListFromRedis(String username) {

        List<Cart> cartList1 = (List<Cart>) redisTemplate.boundHashOps("REDIS_CART_LIST_KEY_").get(username);

        if(cartList1==null ){
            return new ArrayList<>();
        }
        return cartList1;
    }

    @Override
    public List<Cart> merge(List<Cart> cookieCartList, List<Cart> redisCartList) {

        for (Cart cart : cookieCartList) {

            List<TbOrderItem> orderItemList = cart.getOrderItemList();

            for (TbOrderItem orderItem : orderItemList) {
                //orderItem 要添加的商品
                redisCartList = addGoodsToCartList(redisCartList,orderItem.getItemId(),orderItem.getNum());
            }

        }

        return redisCartList;
    }

    private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItemList, Long itemId) {
        for (TbOrderItem orderItem : orderItemList) {
            if(orderItem.getItemId()==itemId.longValue()){
                return orderItem;
            }
        }
        return null;
    }

    private Cart searchCartBySellerId(List<Cart> cartList, String sellerId) {
        for (Cart cart : cartList) {
            if(cart.getSellerId().equals(sellerId)){//找到
                return cart;
            }
        }
        return null;
    }
}
