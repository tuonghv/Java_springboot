package com.zetcode;

import redis.clients.jedis.Jedis;

public class Application  {
    
    public static void main(String[] args) {
        
        Jedis jedis = new Jedis("localhost");
        jedis.set("foo", "bar");
        String value = jedis.get("foo");
        System.out.print("redis fool value: "+value);
    }
}