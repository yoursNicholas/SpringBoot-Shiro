package com.zyd.shiro.util.common;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.data.redis.core.RedisTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=RedisTemplateUtilTest.class)
public class RedisTemplateUtilTest {
    @Autowired
    private RedisTemplate redisTemplate;

    @Test
    public void set() {

        redisTemplate.opsForValue().set("aaa","bbb",1111111111);
    }
}