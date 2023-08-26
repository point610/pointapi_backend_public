package com.pointapi.backend.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.point.pointapicommon.model.entity.UserInterfaceInfo;

import java.util.List;

/**
 * @author Administrator
 * @description 针对表【user_interface_info(用户调用接口关系)】的数据库操作Mapper
 * @createDate 2023-07-28 19:30:22
 * @Entity generator.domain.UserInterfaceInfo
 */
public interface UserInterfaceInfoMapper extends BaseMapper<UserInterfaceInfo> {

    /**
     * 获取调用次数排名前几的接口
     *
     * @param limit
     * @return java.util.List<com.point.pointapicommon.model.entity.UserInterfaceInfo>
     * @Author point
     * @Date 17:55 2023/7/29
     **/
    List<UserInterfaceInfo> listTopInvokeInterfaceInfo(int limit);

}




