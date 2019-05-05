package com.pinyougou.pay.service;

import java.util.Map;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.pay.service *
 * @since 1.0
 */
public interface WXPayService {
    /**
     *  生成支付二维码连接 返回
     * @param out_trade_no  商户交易订单号
     * @param total_fee  支付金额
     * @return
     */
    Map createNative(String out_trade_no, String total_fee);

    /**
     * 根据支付的订单号来查询支付的状态
     * @param out_trade_no
     * @return
     */
    Map<String,String> queryStatus(String out_trade_no);

}
