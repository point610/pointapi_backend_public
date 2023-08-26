package com.pointapi.backend.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName InterfaceDisplayVo
 * @Description 展示接口的简要下线
 * @Author point
 * @Date 2023/7/25 0:12
 * @Version 1.0
 */
@Data
public class InterfaceDisplayVo implements Serializable {
    /**
     * 主键
     */
    private Long id;
    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;


    /**
     * 接口图片
     */
    private String interfaceImg;


    private static final long serialVersionUID = 1L;

}
