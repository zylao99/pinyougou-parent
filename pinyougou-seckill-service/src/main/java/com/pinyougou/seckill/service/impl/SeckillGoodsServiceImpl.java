package com.pinyougou.seckill.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.common.util.SysConstants;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.seckill.service.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.seckill.service.impl *
 * @since 1.0
 */
@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    @Autowired
    private RedisTemplate redisTemplate;
    @Override
    public List<TbSeckillGoods> findAll() {
        //从redis中获取所有的商品的数据
        List<TbSeckillGoods> goods = redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS_REDIS_KEY).values();
        return goods;
    }

    @Override
    public TbSeckillGoods findOne(Long id) {
        return (TbSeckillGoods) redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS_REDIS_KEY).get(id);
    }
}
