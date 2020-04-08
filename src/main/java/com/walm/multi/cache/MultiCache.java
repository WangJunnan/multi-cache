package com.walm.multi.cache;

import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;

/**
 * <p>MultiCache</p>
 *
 * @author wangjn
 * @since 2020-04-03
 */
public class MultiCache<V> implements Cache<V>{

    private Cache<V> caffeineCache;
    private Cache<V> redisCache;

    /**
     * dataLoader
     */
    private DataLoader<V> dataLoader;

    public MultiCache(MultiCacheBuilder<? super V> builder, DataLoader<V> dataLoader) {
        this.dataLoader = dataLoader;
        this.redisCache = new RedisCache<>(builder.getRedisTemplate(), builder.getRedisExpireTime(), builder.getValueSerializer(), this.dataLoader);
        this.caffeineCache = new CaffeineCache<>(builder.getCaffeineMaxSize(), builder.getCaffeineExpireTime(), builder.getCaffeineRefreshTime(), this::loadFromRedis);
    }

    public MultiCache(MultiCacheBuilder<? super V> builder) {
        this.redisCache = new RedisCache<>(builder.getRedisTemplate(), builder.getRedisExpireTime(), builder.getValueSerializer(), this.dataLoader);
        this.caffeineCache = new CaffeineCache<>(builder.getCaffeineMaxSize(), builder.getCaffeineExpireTime(), builder.getCaffeineRefreshTime(), this::loadFromRedis);
    }


    @Override
    public V get(String key) {
        Assert.hasText(key, "key can not be empty");
        return caffeineCache.get(key);
    }

    @Override
    public void put(String key, V value) {
        Assert.hasText(key, "key can not be empty");
        Assert.notNull(value, "value can not be null");
        redisCache.put(key, value);
        caffeineCache.invalidate(key);
    }

    @Override
    public void invalidate(String key) {
        Assert.hasText(key, "key can not be empty");
        redisCache.invalidate(key);
        caffeineCache.invalidate(key);
    }

    @Override
    public void invalidateAll(List<String> keys) {
        Assert.notEmpty(keys, "keys can not be empty");
        redisCache.invalidateAll(keys);
        caffeineCache.invalidateAll(keys);
    }

    public CacheValue<V> loadFromRedis(String key) {
        V value = redisCache.get(key);
        if (Objects.isNull(value)) {
            return new CacheValue<>();
        }
        return new CacheValue<>(value);
    }
}
