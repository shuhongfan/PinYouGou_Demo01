package com.shf.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-redis.xml")
public class TestValue {
    @Autowired
    private RedisTemplate redisTemplate;

//    存入
    @Test
    public void testSetValue(){
        redisTemplate.boundValueOps("name").set("shf");
    }

//    读取
    @Test
    public void getValue(){
        String str = (String) redisTemplate.boundValueOps("name").get();
        System.out.println(str);
    }

//    删除
    @Test
    public void deleteValue(){
        redisTemplate.delete("name");
    }
}
