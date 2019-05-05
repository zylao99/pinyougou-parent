package com.pinyougou.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Set;

/**
 * 描述
 *
 * @author 三国的包子
 * @version 1.0
 * @package com.pinyougou.test *
 * @since 1.0
 */
@ContextConfiguration(locations = "classpath:spring/applicationContext-redis.xml")
@RunWith(SpringRunner.class)
public class SpringDataRedisTest {

    @Autowired
    private RedisTemplate redisTemplate;


    //String
    @Test
    public void valueAdd(){
      /*  //1.加载（初始化spring容器）配置文件
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-redis.xml");

        //2.获取redisTemplate对象
        RedisTemplate bean = context.getBean(RedisTemplate.class);*/

        redisTemplate.boundValueOps("key1").set("123");//set("key1","123")

        Object key1 = redisTemplate.boundValueOps("key1").get();//get("key1")
        System.out.println(key1);

    }
    //hash    hset key1 field1 value
    @Test
    public void hashAdd(){
      /*  //1.加载（初始化spring容器）配置文件
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-redis.xml");

        //2.获取redisTemplate对象
        RedisTemplate bean = context.getBean(RedisTemplate.class);*/

        redisTemplate.boundHashOps("bigkey").put("field1","1233333");
        redisTemplate.boundHashOps("bigkey").put("field2","1233333");

        Object o = redisTemplate.boundHashOps("bigkey").get("field1");
        System.out.println(o);
        Set bigkey = redisTemplate.boundHashOps("bigkey").keys();
        for (Object o1 : bigkey) {
            System.out.println(o1);
        }




    }


    @Test
    public void valueAddx(){
      /*  //1.加载（初始化spring容器）配置文件
        ApplicationContext context = new ClassPathXmlApplicationContext("classpath:spring/applicationContext-redis.xml");

        //2.获取redisTemplate对象
        RedisTemplate bean = context.getBean(RedisTemplate.class);*/

        redisTemplate.boundValueOps("key11").set("value11");

        Object key1 = redisTemplate.boundValueOps("key1").get();//get("key1")
        System.out.println(key1);

    }





}
