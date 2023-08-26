package com.pointapi.backend.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.point.pointapicommon.common.ErrorCode;
import com.point.pointapicommon.exception.BusinessException;
import com.point.pointapicommon.model.entity.User;
import com.point.pointapicommon.service.InnerUserService;
import com.pointapi.backend.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * 内部用户服务实现类
 */
@DubboService
public class InnerUserServiceImpl implements InnerUserService {

    @Resource
    private UserService userService;

    @Override
    public User getInvokeUser(String accessKey) {
        if (StringUtils.isAnyBlank(accessKey)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("accessKey", accessKey);
        User user = userService.getOne(queryWrapper);
        System.out.println("user");
        System.out.println(user);
        return user;
    }
}
