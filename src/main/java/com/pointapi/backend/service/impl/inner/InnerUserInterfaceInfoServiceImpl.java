package com.pointapi.backend.service.impl.inner;

import com.point.pointapicommon.service.InnerUserInterfaceInfoService;
import com.pointapi.backend.model.enums.NonceEnum;
import com.pointapi.backend.service.UserInterfaceInfoService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 内部用户接口信息服务实现类
 */
@DubboService
public class InnerUserInterfaceInfoServiceImpl implements InnerUserInterfaceInfoService {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 判断是否还用剩余的调用次数
     *
     * @param interfaceInfoId
     * @param userId
     * @return boolean
     * @Author point
     * @Date 16:51 2023/7/29
     **/
    @Override
    public boolean hasLeftNum(long interfaceInfoId, long userId) {
        return userInterfaceInfoService.hasLeftNum(interfaceInfoId, userId);
    }

    /**
     * 用户调用接口的次数递增
     *
     * @param interfaceInfoId
     * @param userId
     * @return boolean
     * @Author point
     * @Date 13:35 2023/7/29
     **/
    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {
        return userInterfaceInfoService.invokeCount(interfaceInfoId, userId);
    }

    /**
     * 往redis中插入nonce，格式为userId-interfaceInfoId-nonce
     *
     * @param nonce
     * @param interfaceInfoId
     * @param userId
     * @return boolean
     * @Author point
     * @Date 21:19 2023/8/22
     **/
    @Override
    public boolean insertNonce(long nonce, long interfaceInfoId, long userId) {
        String key = userId + ":" + interfaceInfoId + ":" + nonce;
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(key, 0, NonceEnum.LAST_TIME.getValue(), TimeUnit.MINUTES));
    }
}
