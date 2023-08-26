package com.pointapi.backend.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.point.pointapicommon.common.ErrorCode;
import com.point.pointapicommon.constant.CommonConstant;
import com.point.pointapicommon.constant.UserInterfaceConstant;
import com.point.pointapicommon.exception.BusinessException;
import com.point.pointapicommon.model.entity.UserInterfaceInfo;
import com.pointapi.backend.mapper.UserInterfaceInfoMapper;
import com.pointapi.backend.model.dto.userinterfaceinfo.UserInterfaceInfoAddRequest;
import com.pointapi.backend.model.dto.userinterfaceinfo.UserInterfaceInfoQueryRequest;
import com.pointapi.backend.service.UserInterfaceInfoService;
import com.pointapi.backend.utils.SqlUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Administrator
 * @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Service实现
 * @createDate 2023-07-28 19:30:22
 */
@Service
public class UserInterfaceInfoServiceImpl extends ServiceImpl<UserInterfaceInfoMapper, UserInterfaceInfo>
        implements UserInterfaceInfoService {

    /**
     * 用户接口调用的次数累加
     *
     * @param interfaceInfoId
     * @param userId
     * @return boolean
     * @Author point
     * @Date 23:31 2023/7/28
     **/
    @Override
    public boolean invokeCount(long interfaceInfoId, long userId) {

        // 检查参数
        if (interfaceInfoId <= 0 || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 去数据库查询接口用户信息
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("interfaceInfoId", interfaceInfoId);
        queryWrapper.eq("userId", userId);
        UserInterfaceInfo userInterfaceInfo = getOne(queryWrapper);


        // 判断剩余次数是否大于0
        Integer totalNum = userInterfaceInfo.getTotalNum();
        Integer leftNum = userInterfaceInfo.getLeftNum();
        if (leftNum <= 0) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "剩余调用次数不足");
        }

        // 修改调用次数
        userInterfaceInfo.setLeftNum(leftNum - 1);
        userInterfaceInfo.setTotalNum(totalNum + 1);

        // 更新数据库
        boolean result = updateById(userInterfaceInfo);

        // 返回结果
        return result;
    }

    @Override
    public boolean hasLeftNum(long interfaceInfoId, long userId) {
        // 检查参数
        if (interfaceInfoId <= 0 || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 去数据库查询接口用户信息
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("interfaceInfoId", interfaceInfoId);
        queryWrapper.eq("userId", userId);
        UserInterfaceInfo userInterfaceInfo = getOne(queryWrapper);

        // 判断剩余次数是否大于0
        Integer leftNum = userInterfaceInfo.getLeftNum();

        // 返回结果
        return leftNum > 0;
    }

    /**
     * 用户申请接口调用次数
     * TODO 需要修改为用户申请，管理员确认，再开通
     *
     * @param userInterfaceInfoAddRequest
     * @param request
     * @return boolean
     * @Author point
     * @Date 21:25 2023/7/28
     **/
    @Override
    public boolean addUserInterface(UserInterfaceInfoAddRequest userInterfaceInfoAddRequest, HttpServletRequest request) {

        // 检验参数
        Long interfaceInfoId = userInterfaceInfoAddRequest.getInterfaceInfoId();
        Long userId = userInterfaceInfoAddRequest.getUserId();
        Integer totalNum = userInterfaceInfoAddRequest.getTotalNum();
        Integer leftNum = userInterfaceInfoAddRequest.getLeftNum();
        if (interfaceInfoId == null || interfaceInfoId <= 0 || userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("interfaceInfoId", interfaceInfoId);
        UserInterfaceInfo userInterfaceInfo = getOne(queryWrapper);

        // 但数据库中存在对应的用户和接口的信息时，此时判定为申请增加调用的次数
        if (userInterfaceInfo != null) {
            // 更新调用次数
            userInterfaceInfo.setLeftNum(UserInterfaceConstant.INITIAL_TOTAL_TIMES);
            boolean result = updateById(userInterfaceInfo);

            // 返回结果
            return result;
        }

        userInterfaceInfo = new UserInterfaceInfo();
        userInterfaceInfo.setUserId(userId);
        userInterfaceInfo.setInterfaceInfoId(interfaceInfoId);
        userInterfaceInfo.setTotalNum(totalNum == null ? UserInterfaceConstant.INITIAL_TOTAL_TIMES : totalNum);
        userInterfaceInfo.setLeftNum(leftNum == null ? UserInterfaceConstant.INITIAL_TOTAL_TIMES : leftNum);
        userInterfaceInfo.setStatus(0);
        // 接口的isDelete??

        // 保存数据库
        boolean result = save(userInterfaceInfo);

        return result;
    }

    @Override
    public QueryWrapper<UserInterfaceInfo> getQueryWrapper(UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest) {

        if (userInterfaceInfoQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }

        // 获取请求参数
        Long id = userInterfaceInfoQueryRequest.getId();
        Long userId = userInterfaceInfoQueryRequest.getUserId();
        Long interfaceInfoId = userInterfaceInfoQueryRequest.getInterfaceInfoId();
        Integer totalNum = userInterfaceInfoQueryRequest.getTotalNum();
        Integer leftNum = userInterfaceInfoQueryRequest.getLeftNum();
        Integer status = userInterfaceInfoQueryRequest.getStatus();
        String sortField = userInterfaceInfoQueryRequest.getSortField();
        String sortOrder = userInterfaceInfoQueryRequest.getSortOrder();

        // 拼接查询控制器
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(status != null, "status", status);
        queryWrapper.eq(userId != null, "userId", userId);
        queryWrapper.eq(userId != null, "interfaceInfoId", interfaceInfoId);
        queryWrapper.eq(userId != null, "totalNum", totalNum);
        queryWrapper.eq(userId != null, "leftNum", leftNum);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);

        return queryWrapper;
    }

    @Override
    public UserInterfaceInfo getUserInterfaceInfoByIds(UserInterfaceInfoQueryRequest userInterfaceInfoQueryRequest) {

        // 检验参数
        Long userId = userInterfaceInfoQueryRequest.getUserId();
        Long interfaceInfoId = userInterfaceInfoQueryRequest.getInterfaceInfoId();
        if (interfaceInfoId == null || interfaceInfoId <= 0 || userId == null || userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 去数据库查询
        QueryWrapper<UserInterfaceInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("interfaceInfoId", interfaceInfoId);
        UserInterfaceInfo userInterfaceInfo = getOne(queryWrapper);

        // 返回结果
        return userInterfaceInfo;
    }
}




