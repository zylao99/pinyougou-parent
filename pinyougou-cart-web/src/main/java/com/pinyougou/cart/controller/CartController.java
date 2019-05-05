package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.common.util.CookieUtil;
import com.pinyougou.pojogroup.Cart;
import entity.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.cart.controller *
 * @since 1.0
 */
@RestController
@RequestMapping("/cart")

public class CartController {

    @Reference
    private CartService cartService;

    /**
     * @param itemId 要购买的SKU的iD
     * @param num    要购买的数量
     * @return
     */
    @CrossOrigin(origins = "http://localhost:9105",allowCredentials = "true")
    @RequestMapping("/addGoodsToCartList")
    public Result addGoodsToCartList(HttpServletRequest request, HttpServletResponse response,Long itemId, Integer num) {
        try {
          /*  //服务端 设置头  允许 9105的域（系统）来跨域请求资源。
            response.setHeader("Access-Control-Allow-Origin", "http://localhost:9105");
            //允许客户端9105 携带cookie
            response.setHeader("Access-Control-Allow-Credentials", "true");*/
            //1.获取当前的用户的用户名
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            System.out.println("===="+username);//anonymousUser
            if("anonymousUser".equals(username)) {//是匿名用户就表示是用户没登录
                //2.如果没有登录操作的是 cookie
                System.out.println("===="+username);

                //2.1 先获取cookie中购物车列表
                String cartListstring = CookieUtil.getCookieValue(request, "cartList", true);
                List<Cart> cookieList = new ArrayList<>();
                if(StringUtils.isNotBlank(cartListstring)){
                    cookieList=JSON.parseArray(cartListstring, Cart.class);
                }
                //2.2 向已有的购物车中添加商品  返回一个 最新的购物车列表
                List<Cart> cartnew =  cartService.addGoodsToCartList(cookieList,itemId,num);
                //2.3 把最新的购物车列表向cookie更新购物车列表
                CookieUtil.setCookie(request,response,"cartList", JSON.toJSONString(cartnew),7*24*3600,true);
            }else {
                //3.判断如果已经登录  操作的是redis
                //3.1 先从redis中获取已有的购物车的列表数据
                List<Cart> cartListFromRedisold = cartService.findCartListFromRedis(username);

                //3.2 向已有的购物车列表中添加商品  返回一个最新的购物车列表

                List<Cart> cartnew =  cartService.addGoodsToCartList(cartListFromRedisold,itemId,num);
                //3.3 重新设置回redis中
                cartService.addCartListToRedis(cartnew,username);

            }
            return new Result(true,"操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"操作失败");
        }

    }

    //合并
    @RequestMapping("/findCartList")
    public List<Cart> findCartList(HttpServletRequest request,HttpServletResponse response) {
        //1.获取当前的用户的用户名
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        //2.如果没有登录操作的是 cookie  从cookie取购物车列表
        if("anonymousUser".equals(username)) {//是匿名用户就表示是用户没登录
            String cartListstring = CookieUtil.getCookieValue(request, "cartList", true);
            List<Cart> cookieList = new ArrayList<>();
            if(StringUtils.isNotBlank(cartListstring)){
                cookieList=JSON.parseArray(cartListstring, Cart.class);
            }
            return cookieList;
        }else {
            //3.判断如果已经登录  操作的是redis  从redis取购物车列表
            List<Cart> cartListFromRedisold = cartService.findCartListFromRedis(username);

            //合并
            //1.获取cookie中的购物车数据
            String cartListstring = CookieUtil.getCookieValue(request, "cartList", true);
            List<Cart> cookieList = new ArrayList<>();
            if(StringUtils.isNotBlank(cartListstring)){
                cookieList=JSON.parseArray(cartListstring, Cart.class);
            }
            //2.获取redis中的购物车数据
            //List<Cart> cartListFromRedisold = cartService.findCartListFromRedis(username);

            //3.合并（调用合并的业务方法 返回一个最新的购物车列表）
           List<Cart> cartMostNew= cartService.merge(cookieList,cartListFromRedisold);
            //4.将最新的购物车列表数据 存储到redis中
            cartService.addCartListToRedis(cartMostNew,username);
            //5.重新查询redis中的数据返回
            //cartMostNew
            //6.清空cookie中的购物车数据
            CookieUtil.deleteCookie(request,response,"cartList");

            return cartMostNew;
        }
    }


}
