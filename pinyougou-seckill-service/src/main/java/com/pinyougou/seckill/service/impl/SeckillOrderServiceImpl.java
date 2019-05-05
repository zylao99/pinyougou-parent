package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.common.util.IdWorker;
import com.pinyougou.common.util.SysConstants;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.mapper.TbSeckillOrderMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillOrder;
import com.pinyougou.seckill.service.SeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Date;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.seckill.service.impl *
 * @since 1.0
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private TbSeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    private TbSeckillOrderMapper orderMapper;

    @Autowired
    private IdWorker idWorker;

    @Override
    public void addOrder(Long id,String userId) {
        //1.根据商品的ID 从redis中获取秒杀商品的数据
        TbSeckillGoods tbSeckillGoods = (TbSeckillGoods) redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS_REDIS_KEY).get(id);

        //先进县出的特点
        Object o = redisTemplate.boundListOps(SysConstants.SEC_KILL_GOODS_PREFIX + id).rightPop();

        if(o==null){
            throw new RuntimeException("已经售罄");

        }
       /* if(tbSeckillGoods==null || tbSeckillGoods.getStockCount()<=0) {
            //2.判断商品是否存在  如果不存在  售罄
            //3判断商品的库存为0  售罄
            throw new RuntimeException("已经售罄");
        }*/
        //4.商品进行减库存  如果减到0 更新到数据库中 删除redis中的商品

        tbSeckillGoods.setStockCount(tbSeckillGoods.getStockCount()-1);
        redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS_REDIS_KEY).put(id,tbSeckillGoods);

        // 如果减到0 更新到数据库中 删除redis中的商品
        if(tbSeckillGoods.getStockCount()<=0){
            seckillGoodsMapper.updateByPrimaryKey(tbSeckillGoods);
            redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS_REDIS_KEY).delete(id);
        }
        //5.向redis中添加秒杀订单
        TbSeckillOrder order = new TbSeckillOrder();

        order.setId(idWorker.nextId());
        order.setSeckillId(id);//秒杀商品的ID
        order.setMoney(tbSeckillGoods.getCostPrice());//金额
        order.setUserId(userId);
        order.setSellerId(tbSeckillGoods.getSellerId());
        order.setCreateTime(new Date());
        order.setStatus("0");//未支付
        //存储某一个用户的订单
        redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER_REDIS_KEY).put(userId,order);

    }

    @Override
    public TbSeckillOrder getSeckillOrderByUserId(String userId) {
        return (TbSeckillOrder) redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER_REDIS_KEY).get(userId);
    }

    @Override
    public void updateStatus(String out_trade_no, String userId, String transaction_id) {
        //1.先获取订单
        TbSeckillOrder seckillOrder = (TbSeckillOrder) redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER_REDIS_KEY).get(userId);
        if(seckillOrder==null){
            throw new RuntimeException("订单不存在");
        }
        if(!out_trade_no.equals(seckillOrder.getId().toString())){
            throw new RuntimeException("订单不一致");
        }
        //2.更新订单的状态
        seckillOrder.setPayTime(new Date());//支付时间
        seckillOrder.setStatus("1");//已经支付
        seckillOrder.setTransactionId(transaction_id);

        //3.保存到数据库中
        orderMapper.insert(seckillOrder);
        //4.清空订单
        redisTemplate.boundHashOps(SysConstants.SEC_KILL_ORDER_REDIS_KEY).delete(userId);

    }
}
