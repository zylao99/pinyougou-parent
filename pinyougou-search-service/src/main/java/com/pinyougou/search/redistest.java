package com.pinyougou.search;

import redis.clients.jedis.Jedis;

public class redistest {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("192.168.25.135",6379);
        System.out.println("redis 存储的字符串为: "+ jedis.get("runoobkey"));
    }
}
