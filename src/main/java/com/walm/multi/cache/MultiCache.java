package com.walm.multi.cache;

import com.walm.multi.cache.cluster.ClusterMqInvoke;
import lombok.Getter;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * <p>MultiCache</p>
 *
 * @author wangjn
 * @since 2020-04-03
 */
@Getter
public class MultiCache<V> implements Cache<V>{

    private Cache<V> caffeineCache;
    private Cache<V> redisCache;

    /**
     * dataLoader
     */
    private DataLoader<V> dataLoader;
    private ClusterMqInvoke<V> clusterMqInvoke;

    public MultiCache(MultiCacheBuilder<? super V> builder, DataLoader<V> dataLoader) {
        this.dataLoader = dataLoader;
        this.redisCache = new RedisCache<>(builder.getRedisTemplate(), builder.getRedisExpireTime(), builder.getValueSerializer(), this.dataLoader);
        this.caffeineCache = new CaffeineCache<>(builder.getCaffeineMaxSize(), builder.getCaffeineExpireTime(), builder.getCaffeineRefreshTime(), this::loadFromRedis);
        this.clusterMqInvoke = new ClusterMqInvoke<>(builder.getRedisTemplate(), builder.getClusterRedisTopic(), this);
    }

    public MultiCache(MultiCacheBuilder<? super V> builder) {
        this.redisCache = new RedisCache<>(builder.getRedisTemplate(), builder.getRedisExpireTime(), builder.getValueSerializer());
        this.caffeineCache = new CaffeineCache<>(builder.getCaffeineMaxSize(), builder.getCaffeineExpireTime(), builder.getCaffeineRefreshTime(), this::loadFromRedis);
        this.clusterMqInvoke = new ClusterMqInvoke<>(builder.getRedisTemplate(), builder.getClusterRedisTopic(), this);
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
        clusterMqInvoke.sendUpdate(Collections.singletonList(key));
    }

    @Override
    public void invalidate(String key) {
        Assert.hasText(key, "key can not be empty");
        redisCache.invalidate(key);
        caffeineCache.invalidate(key);
        clusterMqInvoke.sendDel(Collections.singletonList(key));
    }

    @Override
    public void invalidateAll(List<String> keys) {
        Assert.notEmpty(keys, "keys can not be empty");
        redisCache.invalidateAll(keys);
        caffeineCache.invalidateAll(keys);
        clusterMqInvoke.sendDel(keys);
    }

    public CacheValue<V> loadFromRedis(String key) {
        V value = redisCache.get(key);
        if (Objects.isNull(value)) {
            return new CacheValue<>();
        }
        return new CacheValue<>(value);
    }
}
