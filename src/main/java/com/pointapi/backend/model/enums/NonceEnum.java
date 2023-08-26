package com.pointapi.backend.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 接口信息状态枚举
 */
public enum NonceEnum {

    LAST_TIME("nonce持续时间", 5L);

    private final String text;

    private final Long value;

    NonceEnum(String text, Long value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<Long> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    public Long getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
    }
