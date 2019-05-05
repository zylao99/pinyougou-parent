package com.pinyougou.order.service;

import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbOrder;
import com.pinyougou.pojo.TbPayLog;
import entity.PageResult;

import java.util.List;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.order.service *
 * @since 1.0
 */
public interface OrderService {



    public void add(TbOrder tbOrder);

    TbPayLog getPayLogFromRedisByUserId(String userId);

    /**
     *  更新支付日志记录
     *  更新订单的状态
     *  删除redis中的支付日志
     * @param transaction_id 支付成功传递过来的流水号
     * @param out_trade_no  要更新的支付日志记录的主键
     */
    void updatePayLogAndOrder(String transaction_id, String out_trade_no);


    /**
     * 查询并返回所有订单
     * @return
     */
    List<TbOrder> findAll();

    PageResult findPage(int page, int rows);

    /**
     * 分页
     * @param pageNum 当前页 码
     * @param pageSize 每页记录数
     * @return
     */
    public PageResult findPage(TbOrder order, int pageNum, int pageSize);

    TbOrder findOne(Long id);

}
