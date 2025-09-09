package com.lcsk42.starter.cache.redisson.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.stream.Stream;

public final class CacheUtil {

    private static final String SPLICING_OPERATOR = ":";

    /**
     * 通过使用下划线连接提供的键来构建缓存键。
     * 如果任何键为 null 或空，则抛出 RuntimeException。
     *
     * @param keys 要连接成缓存键的键
     * @return 构造的缓存键
     */
    public static String buildKey(String... keys) {
        Stream.of(keys)
                .forEach(key -> {
                    if (StringUtils.isBlank(key)) {
                        throw new RuntimeException("Cache key part must not be null or empty");
                    }
                });
        return StringUtils.join(keys, SPLICING_OPERATOR);
    }

    /**
     * 检查给定的缓存值是否为 null 或空字符串。
     *
     * @param cacheVal 要检查的值
     * @return 如果值为 null 或空白则为 true，否则为 false
     */
    public static boolean isNullOrBlank(Object cacheVal) {
        return Objects.isNull(cacheVal) || (cacheVal instanceof String && StringUtils.isBlank((String) cacheVal));
    }
}
