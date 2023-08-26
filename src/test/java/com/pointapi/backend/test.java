package com.pointapi.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName test
 * @Description TODO
 * @Author point
 * @Date 2023/8/20 15:20
 * @Version 1.0
 */
@SpringBootTest
@Component
public class test {
    @Resource
    private RedisTemplate redisTemplate;

    @Test
    void tempp() {

        System.out.println(redisTemplate.opsForValue().setIfAbsent("userId:interfeceInfoId:1", 0, 5, TimeUnit.MINUTES));

    }
}
