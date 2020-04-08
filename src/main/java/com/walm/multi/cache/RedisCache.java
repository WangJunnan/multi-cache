package com.walm.multi.cache;

import com.google.gson.reflect.TypeToken;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * <p>RedisCache</p>
 *
 * @author wangjn
 * @since 2020-04-03
 */
public class RedisCache<V> implements Cache<V> {

    private DataLoader<V> dataLoader;
    private Long expireTime;
    private StringRedisTemplate redisTemplate;
    private ValueSerializer<V> valueSerializer;

    public RedisCache(StringRedisTemplate redisTemplate,
                      Long expireTime,
                      ValueSerializer serializer,
                      DataLoader<V> dataLoader) {
        this(redisTemplate, expireTime, serializer);
        this.dataLoader = dataLoader;
    }

    public RedisCache(StringRedisTemplate redisTemplate,
                      Long expireTime,
                      ValueSerializer<V> serializer) {
        this.expireTime = expireTime;
        this.redisTemplate = redisTemplate;
        this.valueSerializer = serializer;
    }

    @Override
    public V get(String key) {
        String value = redisTemplate.opsForValue().get(key);
        if (Objects.isNull(value) && Objects.nonNull(dataLoader)) {
            V newValue = dataLoader.load(key);
            if (Objects.nonNull(newValue)) {
                value = valueSerializer.serialize(newValue);
                redisTemplate.opsForValue().set(key, value);
                return newValue;
            } else {
                redisTemplate.opsForValue().set(key, Consts.EMPTY_VALUE);
                return null;
            }
        }
        if (Objects.isNull(value) || Consts.EMPTY_VALUE.equals(value)) {
            return null;
        }
        return valueSerializer.deserialize(value);
    }

    @Override
    public void put(String key, V value) {
        redisTemplate.opsForValue().set(key, valueSerializer.serialize(value), expireTime, TimeUnit.MILLISECONDS);
    }

    @Override
    public void invalidate(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public void invalidateAll(List<String> keys) {
        redisTemplate.delete(keys);
    }
}
