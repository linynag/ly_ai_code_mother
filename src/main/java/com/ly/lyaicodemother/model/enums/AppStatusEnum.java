package com.ly.lyaicodemother.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 应用状态枚举
 *
 * @author linyang
 */
public enum AppStatusEnum {

    /**
     * 待审核
     */
    PENDING_REVIEW("待审核", 0),

    /**
     * 正常上线
     */
    ONLINE("正常上线", 1),

    /**
     * 已下线
     */
    OFFLINE("已下线", 2),

    /**
     * 审核拒绝
     */
    REJECTED("审核拒绝", 3),

    /**
     * 已禁用
     */
    DISABLED("已禁用", 4);

    private final String text;

    private final int value;

    AppStatusEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static AppStatusEnum getEnumByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (AppStatusEnum anEnum : AppStatusEnum.values()) {
            if (anEnum.value == value) {
                return anEnum;
            }
        }
        return null;
    }

    public int getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}