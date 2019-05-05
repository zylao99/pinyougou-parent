package com.pinyougou.seckill.service;

import com.pinyougou.pojo.TbSeckillOrder;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.seckill.service.impl *
 * @since 1.0
 */
public interface SeckillOrderService {

    /**
     * 提交订单
     * @param id
     */
    public void addOrder(Long id,String userId);

    //根据用户的id 获取秒杀订单
    TbSeckillOrder getSeckillOrderByUserId(String userId);


    void updateStatus(String out_trade_no, String userId, String transaction_id);

}
