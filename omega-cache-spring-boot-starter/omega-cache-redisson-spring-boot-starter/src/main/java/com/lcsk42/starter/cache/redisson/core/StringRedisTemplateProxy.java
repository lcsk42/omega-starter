package com.lcsk42.starter.cache.redisson.core;

import com.lcsk42.starter.cache.redisson.config.CacheExtensionProperties;
import com.lcsk42.starter.cache.redisson.function.CacheGetFilter;
import com.lcsk42.starter.cache.redisson.function.CacheGetIfAbsent;
import com.lcsk42.starter.cache.redisson.function.CacheLoader;
import com.lcsk42.starter.cache.redisson.util.CacheUtil;
import com.lcsk42.starter.core.Singleton;
import com.lcsk42.starter.json.jackson.util.JacksonUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.BooleanUtils;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class StringRedisTemplateProxy implements DistributedCache {

    private final StringRedisTemplate stringRedisTemplate;
    private final CacheExtensionProperties cacheExtensionProperties;
    private final RedissonClient redissonClient;

    private static final String LUA_PUT_IF_ALL_ABSENT_SCRIPT_PATH = "lua/putIfAllAbsent.lua";
    private static final String SAFE_GET_DISTRIBUTED_LOCK_KEY_PREFIX = "safe_get_distributed_lock_get:";

    @Override
    public <T> T get(String key, Class<T> clazz) {
        String value = stringRedisTemplate.opsForValue().get(key);
        if (String.class.isAssignableFrom(clazz)) {
            return clazz.cast(value);
        }
        return JacksonUtil.fromJson(value, clazz);
    }

    @Override
    public void put(String key, Object value) {
        put(key, value, cacheExtensionProperties.getValueTimeout());
    }

    @Override
    public Boolean putIfAllAbsent(@NotNull Collection<String> keys) {
        DefaultRedisScript<Boolean> actual = Singleton.get(LUA_PUT_IF_ALL_ABSENT_SCRIPT_PATH, () -> {
            DefaultRedisScript<Boolean> redisScript = new DefaultRedisScript<>();
            redisScript.setScriptSource(
                    new ResourceScriptSource(new ClassPathResource(LUA_PUT_IF_ALL_ABSENT_SCRIPT_PATH))
            );
            redisScript.setResultType(Boolean.class);
            return redisScript;
        });
        if (Objects.isNull(actual)) {
            return Boolean.FALSE;
        }
        Boolean result = stringRedisTemplate.execute(actual,
                List.copyOf(keys),
                cacheExtensionProperties.getValueTimeout().toString());
        return BooleanUtils.isTrue(result);
    }

    @Override
    public Boolean delete(String key) {
        return stringRedisTemplate.delete(key);
    }

    @Override
    public Long delete(Collection<String> keys) {
        return stringRedisTemplate.delete(keys);
    }

    @Override
    public <T> T get(@NotBlank String key, Class<T> clazz, CacheLoader<T> cacheLoader, long timeout) {
        return get(key, clazz, cacheLoader, timeout, cacheExtensionProperties.getValueTimeUnit());
    }

    @Override
    public <T> T get(@NotBlank String key,
                     Class<T> clazz,
                     CacheLoader<T> cacheLoader,
                     long timeout,
                     TimeUnit timeUnit) {
        T result = get(key, clazz);
        if (!CacheUtil.isNullOrBlank(result)) {
            return result;
        }
        return loadAndSet(key, cacheLoader, timeout, timeUnit, false, null);
    }

    @Override
    public <T> T safeGet(@NotBlank String key, Class<T> clazz, CacheLoader<T> cacheLoader, long timeout) {
        return safeGet(key, clazz, cacheLoader, timeout, cacheExtensionProperties.getValueTimeUnit());
    }

    @Override
    public <T> T safeGet(@NotBlank String key,
                         Class<T> clazz,
                         CacheLoader<T> cacheLoader,
                         long timeout,
                         TimeUnit timeUnit) {
        return safeGet(key, clazz, cacheLoader, timeout, timeUnit, null);
    }

    @Override
    public <T> T safeGet(@NotBlank String key,
                         Class<T> clazz,
                         CacheLoader<T> cacheLoader,
                         long timeout,
                         RBloomFilter<String> bloomFilter) {
        return safeGet(key, clazz, cacheLoader, timeout, bloomFilter, null, null);
    }

    @Override
    public <T> T safeGet(@NotBlank String key,
                         Class<T> clazz,
                         CacheLoader<T> cacheLoader,
                         long timeout,
                         TimeUnit timeUnit,
                         RBloomFilter<String> bloomFilter) {
        return safeGet(key, clazz, cacheLoader, timeout, timeUnit, bloomFilter, null, null);
    }

    @Override
    public <T> T safeGet(String key,
                         Class<T> clazz,
                         CacheLoader<T> cacheLoader,
                         long timeout,
                         RBloomFilter<String> bloomFilter,
                         CacheGetFilter<String> cacheCheckFilter) {
        return safeGet(key,
                clazz,
                cacheLoader,
                timeout,
                cacheExtensionProperties.getValueTimeUnit(),
                bloomFilter,
                cacheCheckFilter,
                null);
    }

    @Override
    public <T> T safeGet(String key,
                         Class<T> clazz,
                         CacheLoader<T> cacheLoader,
                         long timeout, TimeUnit timeUnit,
                         RBloomFilter<String> bloomFilter,
                         CacheGetFilter<String> cacheCheckFilter) {
        return safeGet(key, clazz, cacheLoader, timeout, timeUnit, bloomFilter, cacheCheckFilter, null);
    }

    @Override
    public <T> T safeGet(String key,
                         Class<T> clazz,
                         CacheLoader<T> cacheLoader,
                         long timeout,
                         RBloomFilter<String> bloomFilter,
                         CacheGetFilter<String> cacheGetFilter,
                         CacheGetIfAbsent<String> cacheGetIfAbsent) {
        return safeGet(key,
                clazz,
                cacheLoader,
                timeout,
                cacheExtensionProperties.getValueTimeUnit(),
                bloomFilter,
                cacheGetFilter,
                cacheGetIfAbsent);
    }

    @Override
    public <T> T safeGet(String key,
                         Class<T> clazz,
                         CacheLoader<T> cacheLoader,
                         long timeout,
                         TimeUnit timeUnit,
                         RBloomFilter<String> bloomFilter,
                         CacheGetFilter<String> cacheGetFilter,
                         CacheGetIfAbsent<String> cacheGetIfAbsent) {
        T result = get(key, clazz);
        // 如果缓存结果不为 null 或不为空，则返回缓存结果
        // 使用函数来决定是否返回 null 以支持不可删除的 Bloom 过滤器场景
        // 如果两次检查都失败，当 Bloom 过滤器不包含该键时返回 null
        if (!CacheUtil.isNullOrBlank(result)
                || Optional.ofNullable(cacheGetFilter).map(each -> each.filter(key)).orElse(false)
                || Optional.ofNullable(bloomFilter).map(each -> !each.contains(key)).orElse(false)) {
            return result;
        }
        RLock lock = redissonClient.getLock(SAFE_GET_DISTRIBUTED_LOCK_KEY_PREFIX + key);
        lock.lock();
        try {
            if (CacheUtil.isNullOrBlank(result = get(key, clazz))) {
                if (CacheUtil.isNullOrBlank(
                        result = loadAndSet(key, cacheLoader, timeout, timeUnit, true, bloomFilter)
                )) {
                    Optional.ofNullable(cacheGetIfAbsent).ifPresent(each -> each.accept(key));
                }
            }
        } finally {
            lock.unlock();
        }
        return result;
    }

    @Override
    public void put(String key, Object value, long timeout) {
        put(key, value, timeout, cacheExtensionProperties.getValueTimeUnit());
    }

    @Override
    public void put(String key, Object value, long timeout, TimeUnit timeUnit) {
        String actual = value instanceof String ? (String) value : JacksonUtil.toJSON(value);
        if (Objects.nonNull(actual)) {
            stringRedisTemplate.opsForValue().set(key, actual, timeout, timeUnit);
        }
    }

    @Override
    public void safePut(String key, Object value, long timeout, RBloomFilter<String> bloomFilter) {
        safePut(key, value, timeout, cacheExtensionProperties.getValueTimeUnit(), bloomFilter);
    }

    @Override
    public void safePut(String key, Object value, long timeout, TimeUnit timeUnit, RBloomFilter<String> bloomFilter) {
        put(key, value, timeout, timeUnit);
        if (bloomFilter != null) {
            bloomFilter.add(key);
        }
    }

    @Override
    public Boolean hasKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    @Override
    public Object getInstance() {
        return stringRedisTemplate;
    }

    @Override
    public Long countExistingKeys(String... keys) {
        return stringRedisTemplate.countExistingKeys(List.of(keys));
    }

    private <T> T loadAndSet(String key,
                             CacheLoader<T> cacheLoader,
                             long timeout,
                             TimeUnit timeUnit,
                             boolean safeFlag,
                             RBloomFilter<String> bloomFilter) {
        T result = cacheLoader.get();
        if (CacheUtil.isNullOrBlank(result)) {
            return result;
        }
        if (safeFlag) {
            safePut(key, result, timeout, timeUnit, bloomFilter);
        } else {
            put(key, result, timeout, timeUnit);
        }
        return result;
    }
}