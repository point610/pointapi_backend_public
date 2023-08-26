package com.pointapi.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.point.pointapicommon.model.entity.UserInterfaceInfo;
import com.pointapi.backend.model.dto.userinterfaceinfo.UserInterfaceInfoAddRequest;
import com.pointapi.backend.model.dto.userinterfaceinfo.UserInterfaceInfoQueryRequest;

import javax.servlet.http.HttpServletRequest;

/**
* @author Administrator
* @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service
* @createDate 2023-07-28 19:30:22
*/
public interface UserInterfaceInfoService extends IService<UserInterfaceInfo> {

    boolean invokeCount(long interfaceInfoId, long userId);

    boolean addUserInterface(UserInterfaceInfoAddRequest userInterfaceInfoAddRequest, HttpServletRequest request);

    QueryWrapper<UserInterfaceInfo> getQueryWrapper(UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest);

    UserInterfaceInfo getUserInterfaceInfoByIds(UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest);

    boolean hasLeftNum(long interfaceInfoId, long userId);
}
