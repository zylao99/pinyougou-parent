package com.pinyougou.seckill.task;

import com.pinyougou.common.util.SysConstants;
import com.pinyougou.mapper.TbSeckillGoodsMapper;
import com.pinyougou.pojo.TbSeckillGoods;
import com.pinyougou.pojo.TbSeckillGoodsExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.seckill.task *
 * @since 1.0
 */
@Component
public class GoodsTask {

    @Autowired
    private TbSeckillGoodsMapper goodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    //反复被执行的方法
    //用到CRON表达式 用于 设置何年何月何时何分何秒去执行
    @Scheduled(cron = "0/5 * * * * ? ")
    public void selectGoodsToRedis(){
        //1.查询所有的秒杀商品列表   状态审核 没有超过活动期间  库存大于0
        TbSeckillGoodsExample exmaple = new TbSeckillGoodsExample();
        TbSeckillGoodsExample.Criteria criteria = exmaple.createCriteria();
        criteria.andStatusEqualTo("1");
        criteria.andStockCountGreaterThan(0);//库存大于0

        //    start_time<当前的时间<end_time
        Date date = new Date();
        criteria.andStartTimeLessThan(date);
        criteria.andEndTimeGreaterThan(date);

        //排除 已经在redis中存在商品
        Set<Long> seckillGoods = redisTemplate.boundHashOps("seckillGoods").keys();

        if(seckillGoods!=null &&  seckillGoods.size()>0) {
            List<Long> goodsids = new ArrayList<>();
            for (Long seckillGood : seckillGoods) {
                goodsids.add(seckillGood);
            }
            criteria.andIdNotIn(goodsids);
        }


        List<TbSeckillGoods> tbSeckillGoods = goodsMapper.selectByExample(exmaple);
        //2.商品的列表存储redis中

        for (TbSeckillGoods tbSeckillGood : tbSeckillGoods) {
            pushGoodsToRedisList(tbSeckillGood);
            redisTemplate.boundHashOps(SysConstants.SEC_KILL_GOODS_REDIS_KEY).put(tbSeckillGood.getId(),tbSeckillGood);
        }
    }

    private void pushGoodsToRedisList(TbSeckillGoods tbSeckillGood){
        Integer stockCount = tbSeckillGood.getStockCount();//5
        for (Integer i = 0; i < stockCount; i++) {//队列有5个元素  一个商品就是一个队列 队列的元素大小和库存数据一致
            redisTemplate.boundListOps(SysConstants.SEC_KILL_GOODS_PREFIX+tbSeckillGood.getId()).leftPush(tbSeckillGood.getId());
        }

    }
}
