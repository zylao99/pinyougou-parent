package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.TbOrder;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.cart.controller *
 * @since 1.0
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Reference
    private OrderService orderService;

    @RequestMapping("/add")
    public Result add(@RequestBody TbOrder order) {
        try {
            //1.获取到当前的登录的用户名 设置到order
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            order.setUserId(userId);
            //2.引入服务
            //3.调用服务
            orderService.add(order);
            return new Result(true, "订单创建成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "订单创建失败");
        }
    }

}
