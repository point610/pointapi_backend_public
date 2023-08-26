package com.pointapi.backend.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.point.pointapicommon.common.*;
import com.point.pointapicommon.constant.UserConstant;
import com.point.pointapicommon.exception.BusinessException;
import com.point.pointapicommon.exception.ThrowUtils;
import com.point.pointapicommon.model.entity.UserInterfaceInfo;
import com.pointapi.backend.annotation.AuthCheck;
import com.pointapi.backend.model.dto.userinterfaceinfo.UserInterfaceInfoAddRequest;
import com.pointapi.backend.model.dto.userinterfaceinfo.UserInterfaceInfoQueryRequest;
import com.pointapi.backend.model.dto.userinterfaceinfo.UserInterfaceInfoUpdateRequest;
import com.pointapi.backend.model.enums.InterfaceInfoStatusEnum;
import com.pointapi.backend.model.enums.UserInterfaceInfoStatusEnum;
import com.pointapi.backend.service.UserInterfaceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 接口信息接口
 */
@RestController
@RequestMapping("/userinterface")
@Slf4j
public class UserInterfaceInfoController {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    /**
     * 用户申请接口or调用次数
     * TODO 用户申请之后，发送信息给管理员，管理员确认之后才可以开启接口
     *
     * @param userInterfaceInfoAddRequest
     * @param request
     * @return
     */
    // @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/add")
    public BaseResponse<Boolean> addUserInterfaceInfo(@RequestBody UserInterfaceInfoAddRequest userInterfaceInfoAddRequest, HttpServletRequest request) {

        // 检查参数
        if (userInterfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 调用方法添加
        boolean result = userInterfaceInfoService.addUserInterface(userInterfaceInfoAddRequest, request);
        return ResultUtils.success(result);
    }

    /**
     * 删除用户和接口的关系
     *
     * @param deleteRequest
     * @param request
     * @return com.pointapi.backend.common.BaseResponse<java.lang.Boolean>
     * @Author point
     * @Date 23:27 2023/7/24
     **/
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUserInterface(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userInterfaceInfoService.removeById(deleteRequest.getId());
        return ResultUtils.success(result);
    }

    /**
     * 批量删除用户和接口的关系
     *
     * @param deleteRequest
     * @param request
     * @return com.pointapi.backend.common.BaseResponse<java.lang.Boolean>
     * @Author point
     * @Date 23:29 2023/7/24
     **/
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/list/delete")
    public BaseResponse<Boolean> deleteUserInterfaces(@RequestBody List<DeleteRequest> deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userInterfaceInfoService.removeByIds(deleteRequest);
        return ResultUtils.success(result);
    }

    /**
     * 修改用户和接口的关系
     *
     * @param userInterfaceInfoUpdateRequest
     * @param request
     * @return com.pointapi.backend.common.BaseResponse<java.lang.Boolean>
     * @Author point
     * @Date 23:35 2023/7/24
     **/
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUserInterface(@RequestBody UserInterfaceInfoUpdateRequest userInterfaceInfoUpdateRequest, HttpServletRequest request) {

        // 校验参数
        if (userInterfaceInfoUpdateRequest == null || userInterfaceInfoUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 修改数据库
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        BeanUtils.copyProperties(userInterfaceInfoUpdateRequest, userInterfaceInfo);
        boolean result = userInterfaceInfoService.updateById(userInterfaceInfo);

        // 返回结果
        ThrowUtils.throwIf(!result, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 管理员获取所有的用户和接口的关系
     *
     * @param userInterfaceInfoQueryRequest
     * @return com.point.pointapicommon.common.BaseResponse<com.baomidou.mybatisplus.extension.plugins.pagination.Page < com.point.pointapicommon.model.entity.UserInterfaceInfo>>
     * @Author point
     * @Date 21:48 2023/7/28
     **/
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserInterfaceInfo>> listUserInterfaceInfo(@RequestBody UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest) {

        // 校验参数
        if (userInterfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = userInterfaceInfoQueryRequest.getCurrent();
        long size = userInterfaceInfoQueryRequest.getPageSize();

        // 分页查询数据库
        Page<UserInterfaceInfo> userInterfaceInfoPage = userInterfaceInfoService.page(new Page<>(current, size), userInterfaceInfoService.getQueryWrapper(userInterfaceInfoQueryRequest));

        // 返回结果
        return ResultUtils.success(userInterfaceInfoPage);

    }

    /**
     * 允许用户调用接口
     *
     * @param idRequest
     * @param request
     * @return com.pointapi.backend.common.BaseResponse<java.lang.Boolean>
     * @Author point
     * @Date 0:04 2023/7/25
     **/
    @PostMapping("/on")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> onUserInterface(@RequestBody IdRequest idRequest, HttpServletRequest request) {

        // 校验参数
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 去数据库中查询是否存在
        UserInterfaceInfo tempUserInterfaceInfo = userInterfaceInfoService.getById(idRequest.getId());
        if (tempUserInterfaceInfo == null && tempUserInterfaceInfo.getStatus() == InterfaceInfoStatusEnum.OFFLINE.getValue()) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        // TODO 看接口是否能够调用

        // 修数据库中的接口的状态
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        userInterfaceInfo.setId(idRequest.getId());
        userInterfaceInfo.setStatus(UserInterfaceInfoStatusEnum.ON.getValue());
        boolean result = userInterfaceInfoService.updateById(userInterfaceInfo);

        // 返回结果
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 禁止用户调用接口
     *
     * @param idRequest
     * @param request
     * @return com.pointapi.backend.common.BaseResponse<java.lang.Boolean>
     * @Author point
     * @Date 0:04 2023/7/25
     **/
    @PostMapping("/off")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> offUserInterface(@RequestBody IdRequest idRequest, HttpServletRequest request) {

        // 校验参数
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 去数据库中查询是否存在
        UserInterfaceInfo tempUserInterfaceInfo = userInterfaceInfoService.getById(idRequest.getId());
        if (tempUserInterfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        // 修数据库中的接口的状态
        UserInterfaceInfo userInterfaceInfo = new UserInterfaceInfo();
        userInterfaceInfo.setId(idRequest.getId());
        userInterfaceInfo.setStatus(UserInterfaceInfoStatusEnum.OFF.getValue());
        boolean result = userInterfaceInfoService.updateById(userInterfaceInfo);

        // 返回结果
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 获取用户接口关系的详细信息
     *
     * @param userInterfaceInfoQueryRequest
     * @param request
     * @return com.pointapi.backend.common.BaseResponse<com.pointapi.backend.model.entity.InterfaceInfo>
     * @Author point
     * @Date 17:54 2023/7/25
     **/
    @PostMapping("/get")
    public BaseResponse<UserInterfaceInfo> getUserInterfaceInfoByIds(@RequestBody UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest, HttpServletRequest request) {

        // 检查参数
        if (userInterfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 去数据库查询接口信息
        UserInterfaceInfo userInterfaceInfo = userInterfaceInfoService.getUserInterfaceInfoByIds(userInterfaceInfoQueryRequest);

        // 返回接口
        if (userInterfaceInfo == null) {
            return ResultUtils.success(null);
        }
        return ResultUtils.success(userInterfaceInfo);
    }
}
