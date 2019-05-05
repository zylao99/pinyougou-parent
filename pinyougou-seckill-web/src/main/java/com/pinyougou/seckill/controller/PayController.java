package com.pinyougou.seckill.controller;

import com.alibaba.dubbo.config.annotation.Reference;

import com.pinyougou.pay.service.WXPayService;
import com.pinyougou.pojo.TbPayLog;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import entity.Result;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.cart.controller *
 * @since 1.0
 */
@RestController
@RequestMapping("/pay")
public class PayController {
    @Reference
    private WXPayService wxPayService;

    @Reference
    private SeckillOrderService seckillOrderService;



    @RequestMapping("/createNative")
    public Map createNative() {
        //1.调用支付服务 生成支付二维码连接
        //1.1 生成商户交易订单号
//        String out_trade_no = new IdWorker(0, 1).nextId() + "";
        //从redis中获取该用户下的支付日志记录
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();

        //应该从redis中获取当前的用户的秒杀订单的订单号和支付的金额
        TbSeckillOrder seckillOrder = seckillOrderService.getSeckillOrderByUserId(userId);

        Map resultMap=new HashMap();

        if(seckillOrder!=null) {
           double money = seckillOrder.getMoney().doubleValue()*100;
            Long fen=  (long)money;
            resultMap = wxPayService.createNative(seckillOrder.getId()+"", fen + "");
        }else{
            System.out.println("没有值");
        }
        //2.获取二维码连接
        //3.返回
        return resultMap;
    }

    @RequestMapping("/queryStatus")
    public Result queryStatus(String out_trade_no) {
        try {
            String userId = SecurityContextHolder.getContext().getAuthentication().getName();
            //调用方法获取返回结果
            Map<String, String> map = wxPayService.queryStatus(out_trade_no);
            //判断 支付的状态
            if ("SUCCESS".equals(map.get("trade_state"))) {
                //更新redis中的预订单的状态
                //保存到数据库中
                //清空redis中的预订单数据
                seckillOrderService.updateStatus(out_trade_no,userId,map.get("transaction_id"));
               return  new Result(true, "支付成功");
            }else{
                return new Result(false, "401");//支付失败
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "402");//异常支付失败
        }
    }
}
