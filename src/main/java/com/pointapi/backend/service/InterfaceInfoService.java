package com.pointapi.backend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.point.pointapicommon.model.entity.InterfaceInfo;
import com.pointapi.backend.model.dto.interfaceInfo.InterfaceInfoAddRequest;
import com.pointapi.backend.model.dto.interfaceInfo.InterfaceInfoQueryRequest;

import javax.servlet.http.HttpServletRequest;

/**
* @author Administrator
* @description 针对表【interface_info(接口信息)】的数据库操作Service
* @createDate 2023-08-13 16:34:48
*/
public interface InterfaceInfoService extends IService<InterfaceInfo> {

    boolean addInterfaceInfo(InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request);

    QueryWrapper<InterfaceInfo> getQueryWrapper(InterfaceInfoQueryRequest infoQueryRequest);

}
