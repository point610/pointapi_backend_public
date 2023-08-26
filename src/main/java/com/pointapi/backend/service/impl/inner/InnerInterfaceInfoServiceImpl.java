package com.pointapi.backend.service.impl.inner;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.point.pointapicommon.common.ErrorCode;
import com.point.pointapicommon.exception.BusinessException;
import com.point.pointapicommon.model.entity.InterfaceInfo;
import com.point.pointapicommon.service.InnerInterfaceInfoService;
import com.pointapi.backend.mapper.InterfaceInfoMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import javax.annotation.Resource;

/**
 * 内部接口服务实现类
 */
@DubboService
public class InnerInterfaceInfoServiceImpl implements InnerInterfaceInfoService {

    @Resource
    private InterfaceInfoMapper interfaceInfoMapper;

    /**
     * 根据接口的请求路径和请求方法去数据库查询接口信息
     *
     * @param url
     * @param method
     * @return com.point.pointapicommon.model.entity.InterfaceInfo
     * @Author point
     * @Date 13:35 2023/7/29
     **/
    @Override
    public InterfaceInfo getInterfaceInfo(String url, String method) {
        if (StringUtils.isAnyBlank(url, method)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("url", url);
        queryWrapper.eq("method", method);
        return interfaceInfoMapper.selectOne(queryWrapper);
    }

}
