package com.lcsk42.starter.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 表示系统中错误来源的枚举类型。
 * 每个枚举值对应特定的错误来源，并使用标准化代码表示。
 */
@Getter
@AllArgsConstructor
public enum ErrorSourceEnum implements BaseEnum<String> {
    /**
     * 错误来源于客户端应用程序或输入。
     */
    CLIENT("C", "Client"),

    /**
     * 错误来源于远程系统或外部服务。
     */
    REMOTE("R", "Remote"),

    /**
     * 错误来源于我们自己的服务内部。
     */
    SERVICE("S", "Service");

    /**
     * 表示此错误来源的标准化代码。
     * 用于在日志和错误响应中进行简洁标识。
     */
    private final String value;

    private final String description;
}
