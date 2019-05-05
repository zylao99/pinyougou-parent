package com.pinyougou.cart.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pay.service.WXPayService;
import com.pinyougou.pojo.TbPayLog;
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
    private OrderService orderService;

    @RequestMapping("/createNative")
    public Map createNative() {
        //1.调用支付服务 生成支付二维码连接
        //1.1 生成商户交易订单号
//        String out_trade_no = new IdWorker(0, 1).nextId() + "";
        //从redis中获取该用户下的支付日志记录
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        TbPayLog payLog= orderService.getPayLogFromRedisByUserId(userId);
        Map resultMap=new HashMap();
        if(payLog!=null) {
           resultMap = wxPayService.createNative(payLog.getOutTradeNo(), payLog.getTotalFee() + "");
        }else{
            System.out.println("没有值");
        }
        //2.获取二维码连接
        //3.返回
        return resultMap;
    }

    @RequestMapping("/queryStatus")
    public Result queryStatus(String out_trade_no){
        try {
            Result result = null;

            //设置超时时间 为5分钟
            int count= 0;
            while(true) {
                //调用方法获取返回结果
                Map<String, String> map = wxPayService.queryStatus(out_trade_no);

                //判断 支付的状态
                if ("SUCCESS".equals(map.get("trade_state"))) {
                   result= new Result(true, "支付成功");
                   //支付成功 修改 支付日志信息  订单信息 清空redis中的支付日志
                    String transaction_id = map.get("transaction_id");
                    orderService.updatePayLogAndOrder(transaction_id,out_trade_no);
                   return result;
                }
                //每隔三秒查询一次

                Thread.sleep(3000);

                count++;
                if(count>=30){
                    result = new Result(false,"支付超时");
                    return result;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"支付失败");
        }
    }
}
