package com.pinyougou.order.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.mapper.TbOrderItemMapper;
import com.pinyougou.mapper.TbOrderMapper;
import com.pinyougou.mapper.TbPayLogMapper;
import com.pinyougou.order.service.OrderService;
import com.pinyougou.pojo.*;
import com.pinyougou.pojogroup.Cart;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.order.service.impl *
 * @since 1.0
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private TbOrderMapper orderMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbOrderItemMapper orderItemMapper;

    @Autowired
    private TbPayLogMapper payLogMapper;

    @Override
    public void add(TbOrder tbOrder) {
        //从redis获取购物车列表数据
        List<Cart> cartList = (List<Cart>) redisTemplate.boundHashOps("REDIS_CART_LIST_KEY_").get(tbOrder.getUserId());
        //1.拆单 根据商家 每一个商家都要有一个订单
        double totalAllmoney=0;
        List<String> orderList = new ArrayList<>();
        for (Cart cart : cartList) {
            //一个cart生成一个订单
            //2.订单 的主键必须通过雪花算法来生成
            TbOrder ordernew = new TbOrder();
            IdWorker idWorker = new IdWorker(0, 0);
            long orderId = idWorker.nextId();
            ordernew.setOrderId(orderId);
            orderList.add(orderId+"");

            ordernew.setPaymentType(tbOrder.getPaymentType());
            ordernew.setStatus("1");//状态：1、未付款，2、已付款，3、
            ordernew.setCreateTime(new Date());
            ordernew.setUpdateTime(ordernew.getCreateTime());
            ordernew.setUserId(tbOrder.getUserId());
            ordernew.setReceiverAreaName(tbOrder.getReceiverAreaName());//详细地址
            ordernew.setReceiverMobile(tbOrder.getReceiverMobile());//电话
            ordernew.setReceiver(tbOrder.getReceiver());//收货人
            ordernew.setSourceType("2");//PC端
            ordernew.setSellerId(cart.getSellerId());//商家的ID

            //计算应付金额
            List<TbOrderItem> orderItemList = cart.getOrderItemList();
            double money = 0;
            for (TbOrderItem orderItem : orderItemList) {
                //4.插入数据到订单选项表
                money+=orderItem.getTotalFee().doubleValue();
                IdWorker idWorker1 = new IdWorker(0, 0);
                long orderitemId = idWorker.nextId();
                orderItem.setId(orderitemId);
                orderItem.setOrderId(orderId);

                orderItemMapper.insert(orderItem);
            }
            ordernew.setPayment(new BigDecimal(money));

            totalAllmoney+=money;

            //3.插入数据到订单表
            orderMapper.insert(ordernew);
        }

        //生成支付记录
        TbPayLog payLog = new TbPayLog();
        //设置属性值
        payLog.setOutTradeNo(new IdWorker(1,1).nextId()+"");
        payLog.setCreateTime(new Date());
        long totalFee = (long)(100*totalAllmoney);
        payLog.setTotalFee(totalFee);//支付金额（分）  而且是所有的商家的商品的总金额
        payLog.setUserId(tbOrder.getUserId());
        payLog.setTradeState("0");//未支付
        payLog.setOrderList(orderList.toString().replace("[","").replace("]",""));
        payLog.setPayType("1");//微信支付
        //保存数据库中
        payLogMapper.insert(payLog);
        //把该用户的日志记录存储到redis中

        //TbPayLog
        redisTemplate.boundHashOps(TbPayLog.class.getSimpleName()).put(tbOrder.getUserId(),payLog);


        //清空redis中的购物车
        redisTemplate.boundHashOps("REDIS_CART_LIST_KEY_").delete(tbOrder.getUserId());
    }

    @Override
    public TbPayLog getPayLogFromRedisByUserId(String userId) {
        return (TbPayLog) redisTemplate.boundHashOps(TbPayLog.class.getSimpleName()).get(userId);
    }

    @Override
    public void updatePayLogAndOrder(String transaction_id, String out_trade_no) {
        //1.先根据主键查询支付日志对象
        TbPayLog payLog = payLogMapper.selectByPrimaryKey(out_trade_no);
        //2.更新
        payLog.setPayTime(new Date());
        payLog.setTransactionId(transaction_id);
        payLog.setTradeState("1");//已支付
        payLogMapper.updateByPrimaryKey(payLog);
        //3.获取支付日志关联的订单的ID
        String orderList = payLog.getOrderList();//  37,38
        String[] split = orderList.split(",");
        //4.获取订单对象
        for (String orderId : split) {
            Long orderIdL = Long.valueOf(orderId);
            TbOrder order = orderMapper.selectByPrimaryKey(orderIdL);
            //5.更新订单的状态
            order.setStatus("2");//已经支付
            order.setUpdateTime(new Date());
            order.setPaymentTime(order.getUpdateTime());//设置支付时间
            orderMapper.updateByPrimaryKey(order);
        }

        //6.删除该用户的redis中的日志记录
        redisTemplate.boundHashOps(TbPayLog.class.getSimpleName()).delete(payLog.getUserId());
    }



    /**
     * 查询所有订单
     * @return list<TbOrder>
     */
    @Override
    public List<TbOrder> findAll() {
        List<TbOrder> list = orderMapper.selectByExample(null);
        return list;
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbOrder> page=   (Page<TbOrder>) orderMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public PageResult findPage(TbOrder order, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbOrderExample example=new TbOrderExample();
        TbOrderExample.Criteria criteria = example.createCriteria();

        if(order!=null){

        }

        Page<TbOrder> page= (Page<TbOrder>)orderMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }


    @Override
    public TbOrder findOne(Long id) {
        return orderMapper.selectByPrimaryKey(id);
    }



    public static void main(String[] args) {
        long totalFee = (long)0.01*100;
        System.out.println(totalFee);

        String a = "1";
        String b = "2";
        List<String> list = new ArrayList<>();
        list.add(a);
        list.add(b);
        System.out.println(list.toString().replace("[","").replace("]",""));
    }
}
