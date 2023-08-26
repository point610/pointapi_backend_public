package com.pointapi.backend.controller;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.point.pointapicommon.common.*;
import com.point.pointapicommon.constant.UserConstant;
import com.point.pointapicommon.exception.BusinessException;
import com.point.pointapicommon.exception.ThrowUtils;
import com.point.pointapicommon.model.entity.InterfaceInfo;
import com.point.pointapicommon.model.entity.User;
import com.point.pointapicommon.utils.HeaderUtils;
import com.pointapi.backend.annotation.AuthCheck;
import com.pointapi.backend.model.dto.interfaceInfo.InterfaceInfoAddRequest;
import com.pointapi.backend.model.dto.interfaceInfo.InterfaceInfoInvokeRequest;
import com.pointapi.backend.model.dto.interfaceInfo.InterfaceInfoQueryRequest;
import com.pointapi.backend.model.dto.interfaceInfo.InterfaceInfoUpdateRequest;
import com.pointapi.backend.model.enums.InterfaceInfoStatusEnum;
import com.pointapi.backend.model.vo.InterfaceDisplayVo;
import com.pointapi.backend.service.InterfaceInfoService;
import com.pointapi.backend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 接口信息接口
 */
@RestController
@RequestMapping("/interface")
@Slf4j
public class InterfaceController {

    @Resource
    private InterfaceInfoService interfaceInfoService;

    @Resource
    private UserService userService;

    /**
     * 创建接口
     *
     * @param interfaceInfoAddRequest
     * @param request
     * @return
     */
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/add")
    public BaseResponse<Boolean> addInterfaceInfo(@RequestBody InterfaceInfoAddRequest interfaceInfoAddRequest, HttpServletRequest request) {
        if (interfaceInfoAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = interfaceInfoService.addInterfaceInfo(interfaceInfoAddRequest, request);
        return ResultUtils.success(result);
    }

    /**
     * 删除接口
     *
     * @param deleteRequest
     * @param request
     * @return com.pointapi.backend.common.BaseResponse<java.lang.Boolean>
     * @Author point
     * @Date 23:27 2023/7/24
     **/
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteInterface(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = interfaceInfoService.removeById(deleteRequest.getId());
        return ResultUtils.success(result);
    }

    /**
     * 批量删除接口
     *
     * @param deleteRequest
     * @param request
     * @return com.pointapi.backend.common.BaseResponse<java.lang.Boolean>
     * @Author point
     * @Date 23:29 2023/7/24
     **/
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    @PostMapping("/list/delete")
    public BaseResponse<Boolean> deleteInterfaces(@RequestBody List<DeleteRequest> deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = interfaceInfoService.removeByIds(deleteRequest);
        return ResultUtils.success(result);
    }

    /**
     * 修改接口
     *
     * @param interfaceInfoUpdateRequest
     * @param request
     * @return com.pointapi.backend.common.BaseResponse<java.lang.Boolean>
     * @Author point
     * @Date 23:35 2023/7/24
     **/
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateInterface(@RequestBody InterfaceInfoUpdateRequest interfaceInfoUpdateRequest, HttpServletRequest request) {

        // 校验参数
        if (interfaceInfoUpdateRequest == null || interfaceInfoUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 修改数据库
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        BeanUtils.copyProperties(interfaceInfoUpdateRequest, interfaceInfo);
        boolean result = interfaceInfoService.updateById(interfaceInfo);

        // 返回结果
        ThrowUtils.throwIf(!result, ErrorCode.PARAMS_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 管理员获取所有的接口信息
     *
     * @param infoQueryRequest
     * @return com.pointapi.backend.common.BaseResponse<java.util.List < com.pointapi.backend.model.entity.InterfaceInfo>>
     * @Author point
     * @Date 23:47 2023/7/24
     **/
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<InterfaceInfo>> listInterfaceInfo(@RequestBody InterfaceInfoQueryRequest infoQueryRequest) {

        // 校验参数
        if (infoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = infoQueryRequest.getCurrent();
        long size = infoQueryRequest.getPageSize();

        // 分页查询数据库
        Page<InterfaceInfo> interfaceInfoPage = interfaceInfoService.page(new Page<>(current, size), interfaceInfoService.getQueryWrapper(infoQueryRequest));

        // 返回结果
        return ResultUtils.success(interfaceInfoPage);

    }

    /**
     * 上线接口
     *
     * @param idRequest
     * @param request
     * @return com.pointapi.backend.common.BaseResponse<java.lang.Boolean>
     * @Author point
     * @Date 0:04 2023/7/25
     **/
    @PostMapping("/online")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> onlineInterface(@RequestBody IdRequest idRequest, HttpServletRequest request) {

        // 校验参数
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 去数据库中查询是否存在
        InterfaceInfo tempInterface = interfaceInfoService.getById(idRequest.getId());
        if (tempInterface == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        // TODO 检查接口是否可用正常调用

        // 修数据库中的接口的状态
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(idRequest.getId());
        interfaceInfo.setStatus(InterfaceInfoStatusEnum.ONLINE.getValue());
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        // 返回结果
        return ResultUtils.success(true);

    }


    /**
     * 下线接口
     *
     * @param idRequest
     * @param request
     * @return com.pointapi.backend.common.BaseResponse<java.lang.Boolean>
     * @Author point
     * @Date 0:04 2023/7/25
     **/
    @PostMapping("/offline")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> offlineInterface(@RequestBody IdRequest idRequest, HttpServletRequest request) {

        // 校验参数
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 去数据库中查询是否存在
        InterfaceInfo tempInterface = interfaceInfoService.getById(idRequest.getId());
        if (tempInterface == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        // 修数据库中的接口的状态
        InterfaceInfo interfaceInfo = new InterfaceInfo();
        interfaceInfo.setId(idRequest.getId());
        interfaceInfo.setStatus(InterfaceInfoStatusEnum.OFFLINE.getValue());
        boolean result = interfaceInfoService.updateById(interfaceInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        // 返回结果
        return ResultUtils.success(true);

    }

    /**
     * 用户获取接口的简要信息
     *
     * @return com.pointapi.backend.common.BaseResponse<java.util.List < com.pointapi.backend.model.vo.InterfaceDisplayVo>>
     * @Author point
     * @Date 0:28 2023/7/25
     **/
    @PostMapping("/list/online")
    public BaseResponse<List<InterfaceDisplayVo>> listOnlineInterfaceVO() {

        // 构造查询条件
        InterfaceInfo tempInterfaceInfo = new InterfaceInfo();
        tempInterfaceInfo.setStatus(InterfaceInfoStatusEnum.ONLINE.getValue());
        QueryWrapper<InterfaceInfo> interfaceInfoQueryWrapper = new QueryWrapper<>(tempInterfaceInfo);

        // 去数据库查询
        List<InterfaceInfo> interfaceInfoList = interfaceInfoService.list(interfaceInfoQueryWrapper);

        // 封装结果
        List<InterfaceDisplayVo> interfaceDisplayVos = interfaceInfoList.stream().map(interfaceInfo -> {
            InterfaceDisplayVo interfaceDisplayVo = new InterfaceDisplayVo();
            BeanUtils.copyProperties(interfaceInfo, interfaceDisplayVo);
            return interfaceDisplayVo;
        }).collect(Collectors.toList());

        // 返回结果
        return ResultUtils.success(interfaceDisplayVos);

    }

    /**
     * 获取接口的详细信息
     *
     * @param idRequest
     * @param request
     * @return com.pointapi.backend.common.BaseResponse<com.pointapi.backend.model.entity.InterfaceInfo>
     * @Author point
     * @Date 17:54 2023/7/25
     **/
    @PostMapping("/get")
    public BaseResponse<InterfaceInfo> getInterfaceInfoById(@RequestBody IdRequest idRequest, HttpServletRequest request) {

        // 检查参数
        if (idRequest == null || idRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 去数据库查询接口信息
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(idRequest.getId());

        // 返回接口
        if (interfaceInfo == null || interfaceInfo.getStatus() == InterfaceInfoStatusEnum.OFFLINE.getValue()) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(interfaceInfo);
    }

    /**
     * 测试调用接口
     */
    @PostMapping("/invoke")
    public BaseResponse<Object> invokeInterfaceInfo(@RequestBody InterfaceInfoInvokeRequest interfaceInfoInvokeRequest, HttpServletRequest request) throws Exception {
        // 校验参数
        if (interfaceInfoInvokeRequest == null || interfaceInfoInvokeRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = interfaceInfoInvokeRequest.getId();
        String userRequestParams = interfaceInfoInvokeRequest.getUserRequestParams();
        System.out.println("interfaceInfoInvokeRequest");
        System.out.println(interfaceInfoInvokeRequest);
        // 判断是否存在
        InterfaceInfo interfaceInfo = interfaceInfoService.getById(id);
        if (interfaceInfo == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 判断是否下线
        if (interfaceInfo.getStatus() == InterfaceInfoStatusEnum.OFFLINE.getValue()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接口已下线");
        }

        // 获取当前用户的accessKey和secretKey
        User loginUser = userService.getLoginUser(request);
        String accessKey = loginUser.getAccessKey();
        String secretKey = loginUser.getSecretKey();

        // 使用http请求来实现--过网关
        System.out.println("interfaceInfo.getUrl()");
        System.out.println(interfaceInfo.getUrl());
        HttpResponse response = HttpRequest.post(interfaceInfo.getUrl())
                .addHeaders(HeaderUtils.getHeaderMap(userRequestParams, accessKey, secretKey))
                .body(userRequestParams)
                .execute();

        System.out.println(response.body());

        return ResultUtils.success(response.body());

    }
}
