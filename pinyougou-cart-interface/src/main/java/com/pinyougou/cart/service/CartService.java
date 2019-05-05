package com.pinyougou.cart.service;

import com.pinyougou.pojogroup.Cart;

import java.util.List; /**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.cart.service *
 * @since 1.0
 */
public interface CartService {
    /**
     * 向已有的购物车列表中添加商品
     * @param cartList 已有的购物车列表
     * @param itemId  要添加的商品的ID
     * @param num  要添加的商品的数量
     * @return
     */
    List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num);

    //设置购物车到redis中

    /**
     *
     * @param cartList 购物车数据
     * @param username   登录用户名的账号
     */
    void addCartListToRedis(List<Cart> cartList,String username);

    /**
     * 获取登录用户的购物车数据
     * @param username
     * @return
     */
    List<Cart> findCartListFromRedis(String username);

    /**
     *合并cookie中的购物车到redis购物车中  返回一个最新的列表
     * @param cookieCartList  cookie的购物车数据
     * @param redisCartList  redis中的购物车数据
     * @return
     */
    List<Cart> merge(List<Cart> cookieCartList, List<Cart> redisCartList);

}
