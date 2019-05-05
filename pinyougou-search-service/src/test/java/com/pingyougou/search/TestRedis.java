package com.pingyougou.search;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pingyougou.search *
 * @since 1.0
 */
@ContextConfiguration("classpath:spring/applicationContext-redis.xml")
@RunWith(SpringRunner.class)
public class TestRedis {

    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void tessss(){
        Object o = redisTemplate.boundHashOps("itemCat").get("手机");
        System.out.println(o);
        List itemCat = redisTemplate.boundHashOps("itemCat").values();
        for (Object o1 : itemCat) {
            System.out.println(o1);
        }

    }
}
