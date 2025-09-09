package com.lcsk42.starter.cache.redisson.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Data
@ConfigurationProperties(prefix = CacheExtensionProperties.PREFIX)
public class CacheExtensionProperties {

    public static final String PREFIX = "framework.cache.redisson";

    public static final String BLOOM_FILTER = PREFIX + ".default-bloom-filter";

    /**
     * 键前缀
     */
    private String prefix = "";

    /**
     * 键前缀使用的字符集
     */
    private String prefixCharset = StandardCharsets.UTF_8.name();

    /**
     * 值的默认超时时间 (单位: 毫秒)
     */
    private Long valueTimeout = 30 * 1_000L;

    /**
     * 值超时的时间单位
     */
    private TimeUnit valueTimeUnit = TimeUnit.MILLISECONDS;

    /**
     * Bloom 过滤器
     */
    private BloomFilter defaultBloomFilter;

    @Data
    public static class BloomFilter {

        /**
         * 是否开启 boolean 过滤器
         */
        private Boolean enable = false;

        /**
         * Bloom 过滤器实例的默认名称
         */
        private String name = "cache_penetration_bloom_filter";

        /**
         * 每个元素的预期插入次数
         */
        private Long expectedInsertions = 64L;

        /**
         * 预期误判概率
         */
        private Double falseProbability = 0.03D;
    }
}

