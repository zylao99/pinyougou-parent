package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.seckill.service.SeckillOrderService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.seckill.controller *
 * @since 1.0
 */
@RestController
@RequestMapping("/seckillOrder")
public class seckillOrderController {

    @Reference
    private SeckillOrderService seckillOrderService;

    @RequestMapping("/submitOrder")
    public Result submitOrder(Long id){
        try {
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            if("anonymousUser".equals(userId)){
                //401 表示没有登录的情况
                return new Result(false,"401");
            }
            seckillOrderService.addOrder(id,userId);
            return new Result(true,"下单成功");
        }catch (RuntimeException e) {
            e.printStackTrace();
            return new Result(false,e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            //下单失败
            return new Result(false,"402");
        }
    }
}
