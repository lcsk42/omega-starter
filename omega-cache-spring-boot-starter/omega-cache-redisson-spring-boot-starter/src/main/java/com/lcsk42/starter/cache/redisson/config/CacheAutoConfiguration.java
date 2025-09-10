package com.lcsk42.starter.cache.redisson.config;

import com.lcsk42.starter.cache.redisson.core.StringRedisTemplateProxy;
import com.lcsk42.starter.cache.redisson.serializer.RedisKeySerializer;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

@Slf4j
@AllArgsConstructor
@AutoConfiguration
@EnableConfigurationProperties(CacheExtensionProperties.class)
public class CacheAutoConfiguration {

    private final CacheExtensionProperties cacheExtensionProperties;

    @Bean
    public RedisKeySerializer redisKeySerializer() {
        String prefix = cacheExtensionProperties.getPrefix();
        String prefixCharset = cacheExtensionProperties.getPrefixCharset();
        return new RedisKeySerializer(prefix, prefixCharset);
    }

    @Bean
    @ConditionalOnProperty(prefix = CacheExtensionProperties.BLOOM_FILTER, name = "enabled", havingValue = "true")
    public RBloomFilter<String> cachePenetrationBloomFilter(RedissonClient redissonClient) {
        CacheExtensionProperties.BloomFilter defaultBloomFilter = cacheExtensionProperties.getDefaultBloomFilter();
        RBloomFilter<String> cachePenetrationBloomFilter =
                redissonClient.getBloomFilter(defaultBloomFilter.getName());
        cachePenetrationBloomFilter.tryInit(
                defaultBloomFilter.getExpectedInsertions(),
                defaultBloomFilter.getFalseProbability()
        );
        return cachePenetrationBloomFilter;
    }

    @Bean
    public StringRedisTemplateProxy stringRedisTemplateProxy(RedisKeySerializer redisKeySerializer,
                                                             StringRedisTemplate stringRedisTemplate,
                                                             RedissonClient redissonClient) {
        stringRedisTemplate.setKeySerializer(redisKeySerializer);
        return new StringRedisTemplateProxy(stringRedisTemplate, cacheExtensionProperties, redissonClient);
    }


    @PostConstruct
    public void postConstruct() {
        log.debug("[Omega Starter] - Auto Configuration 'Cache Redisson' completed initialization.");
    }
}
