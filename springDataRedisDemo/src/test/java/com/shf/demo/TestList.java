package com.shf.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring/applicationContext-redis.xml")
public class TestList {
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 右压栈  后添加的元素排在右边
     */
    @Test
    public void testSetValue1(){
        redisTemplate.boundListOps("namelist1").rightPush("刘备");
        redisTemplate.boundListOps("namelist1").rightPush("关羽");
        redisTemplate.boundListOps("namelist1").rightPush("张飞");
    }

    @Test
    public void testGetValue1(){
        List list = redisTemplate.boundListOps("namelist1").range(0, 100);
        System.out.println(list);
    }

    /**
     * 左压栈
     */
    @Test
    public void testSetValue2(){
        redisTemplate.boundListOps("namelist2").leftPush("刘备");
        redisTemplate.boundListOps("namelist2").leftPush("关羽");
        redisTemplate.boundListOps("namelist2").leftPush("张飞");
    }

    @Test
    public void testGetValue2(){
        List list = redisTemplate.boundListOps("namelist2").range(0, 100);
        System.out.println(list);
    }

    /**
     * 按索引位置查询
     */
    @Test
    public void searchByIndex(){
        String str = (String) redisTemplate.boundListOps("namelist2").index(1);
        System.out.println(str);
    }

    @Test
    public void removeValue(){
        redisTemplate.boundListOps("namelist1").remove(1,"张飞");
    }
}
