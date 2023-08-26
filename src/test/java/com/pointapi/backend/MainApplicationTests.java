package com.pointapi.backend;

import com.pointapi.backend.config.WxOpenConfig;
import com.pointapi.backend.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;

/**
 * 主类测试
 */
@SpringBootTest
class MainApplicationTests {
    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private PostService postService;

    @Resource
    private WxOpenConfig wxOpenConfig;

    @Test
    void contextLoads() {
        System.out.println(wxOpenConfig);
    }

    @Test
    void upLoadImage() {
        System.out.println(66666);
    }




}
