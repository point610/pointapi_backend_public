package com.pointapi.backend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.point.pointapicommon.common.ErrorCode;
import com.point.pointapicommon.constant.CommonConstant;
import com.point.pointapicommon.exception.BusinessException;
import com.point.pointapicommon.model.entity.InterfaceInfo;
import com.pointapi.backend.mapper.InterfaceInfoMapper;
import com.pointapi.backend.model.dto.interfaceInfo.InterfaceInfoAddRequest;
import com.pointapi.backend.model.dto.interfaceInfo.InterfaceInfoQueryRequest;
import com.pointapi.backend.service.InterfaceInfoService;
import com.pointapi.backend.service.UserService;
import com.pointapi.backend.utils.SqlUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Administrator
 * @description 针对表【interface_info(接口信息)】的数据库操作Service实现
 * @createDate 2023-08-13 16:34:48
 */
@Service
public class InterfaceInfoServiceImpl extends ServiceImpl<InterfaceInfoMapper, InterfaceInfo>
        implements InterfaceInfoService {
    @Resource
    private UserService userService;

    @Override
    public boolean addInterfaceInfo(InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request) {

        // 检查参数
        String name = interfaceInfoAddRequest.getName();
        String description = interfaceInfoAddRequest.getDescription();
        String url = interfaceInfoAddRequest.getUrl();
        String requestHeader = interfaceInfoAddRequest.getRequestHeader();
        String responseHeader = interfaceInfoAddRequest.getResponseHeader();
        String method = interfaceInfoAddRequest.getMethod();
        if (StringUtils.isAnyBlank(name, description, url, requestHeader, responseHeader, method)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 加入数据库
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtil.copyProperties(interfaceInfoAddRequest, interfaceInfo);
        interfaceInfo.setUserId(userService.getLoginUser(request).getId());

        boolean result = save(interfaceInfo);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        // 返回结果
        return result;
    }

    /**
     * 拼接分页的查询语句
     *
     * @param infoQueryRequest
     * @return com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.pointapi.backend.model.entity.InterfaceInfo>
     * @Author point
     * @Date 14:43 2023/7/25
     **/
    @Override
    public QueryWrapper<InterfaceInfo> getQueryWrapper(InterfaceInfoQueryRequest infoQueryRequest) {

        if (infoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        // 获取请求参数
        Long id = infoQueryRequest.getId();
        String name = infoQueryRequest.getName();
        String description = infoQueryRequest.getDescription();
        String url = infoQueryRequest.getUrl();
        String requestHeader = infoQueryRequest.getRequestHeader();
        String responseHeader = infoQueryRequest.getResponseHeader();
        Integer status = infoQueryRequest.getStatus();
        String method = infoQueryRequest.getMethod();
        Long userId = infoQueryRequest.getUserId();
        String interfaceImg = infoQueryRequest.getInterfaceImg();
        String sortField = infoQueryRequest.getSortField();
        String sortOrder = infoQueryRequest.getSortOrder();

        // 拼接查询控制器
        QueryWrapper<InterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(status != null, "status", status);
        queryWrapper.eq(userId != null, "userId", userId);
        queryWrapper.eq(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.eq(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.eq(StringUtils.isNotBlank(url), "url", url);
        queryWrapper.like(StringUtils.isNotBlank(requestHeader), "requestHeader", requestHeader);
        queryWrapper.like(StringUtils.isNotBlank(responseHeader), "responseHeader", responseHeader);
        queryWrapper.like(StringUtils.isNotBlank(method), "method", method);
        queryWrapper.like(StringUtils.isNotBlank(method), "interfaceImg", interfaceImg);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);

        return queryWrapper;
    }

}




