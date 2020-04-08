package com.walm.multi.cache;

import lombok.Data;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.Assert;

/**
 * <p>MultiCacheBuilder</p>
 *
 * @author wangjn
 * @since 2020-04-06
 */
@Data
public class MultiCacheBuilder<V> {

    /**
     * redis
     */
    private StringRedisTemplate redisTemplate;
    private Long redisExpireTime;

    private ValueSerializer<? extends V> valueSerializer;


    /**
     * caffeine
     */
    private Long caffeineExpireTime;
    private Long caffeineRefreshTime;
    private Integer caffeineMaxSize;

    /**
     * cacheName
     */
    private String cacheName;

    private MultiCacheBuilder(String cacheName) {
        this.cacheName = cacheName;
    }

    public static MultiCacheBuilder<Object> newBuilder(String cacheName) {
        return new MultiCacheBuilder<>(cacheName);
    }

    public MultiCacheBuilder<V> redisCache(StringRedisTemplate redisTemplate, Long expireTime) {
        this.redisTemplate = redisTemplate;
        this.redisExpireTime = expireTime;
        return this;
    }

    public <V1 extends V> MultiCacheBuilder<V> redisValueSerializer(ValueSerializer<V1> valueSerializer) {
        this.valueSerializer = valueSerializer;
        return this;
    }

    public MultiCacheBuilder<V> caffeineCache(Integer maximumSize, Long expireTime, Long refreshTime) {
        this.caffeineMaxSize = maximumSize;
        this.caffeineExpireTime = expireTime;
        this.caffeineRefreshTime = refreshTime;
        return this;
    }

    public <V1 extends V> Cache<V1> build() {
        Assert.notNull(valueSerializer, "redis valueSerializer is null");
        Assert.notNull(redisTemplate, "redis redisTemplate is null");
        Assert.notNull(redisExpireTime, "redis redisExpireTime is null");
        Assert.notNull(caffeineExpireTime, "caffeineExpireTime is null");
        Assert.notNull(caffeineRefreshTime, "caffeineRefreshTime is null");
        Assert.notNull(caffeineMaxSize, "caffeineMaxSize is null");
        return new MultiCache<>(this);
    }

    public <V1 extends V> Cache<V1> build(DataLoader<V1> dataLoader) {
        Assert.notNull(valueSerializer, "redis valueSerializer is null");
        Assert.notNull(redisTemplate, "redis redisTemplate is null");
        Assert.notNull(redisExpireTime, "redis redisExpireTime is null");
        Assert.notNull(caffeineExpireTime, "caffeineExpireTime is null");
        Assert.notNull(caffeineRefreshTime, "caffeineRefreshTime is null");
        Assert.notNull(caffeineMaxSize, "caffeineMaxSize is null");
        Assert.notNull(dataLoader, "redis dataLoader is null");
        return new MultiCache<>(this, dataLoader);
    }
}
