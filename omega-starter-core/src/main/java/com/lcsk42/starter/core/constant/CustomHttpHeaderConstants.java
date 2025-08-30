package com.lcsk42.starter.core.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 自定义 Http 请求头常量
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CustomHttpHeaderConstants {
    /**
     * 用户 ID
     */
    public static final String USER_ID = "User-Id";

    /**
     * 请求 ID
     */
    public static final String REQUEST_ID = "Request-Id";

    /**
     * 令牌
     */
    public static final String TOKEN = "Token";
}
