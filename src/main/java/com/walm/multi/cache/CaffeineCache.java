package com.walm.multi.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * <p>CaffeineCache</p>
 *
 * @author wangjn
 * @since 2020-04-03
 */
public class CaffeineCache<V> implements Cache<V> {

    final private LoadingCache<String, CacheValue<V>> cache;

    final private Integer maximumSize;
    final private Long expireTime;
    final private Long refreshTime;
    final private Boolean recordStats;

    public CaffeineCache(Integer maximumSize,
                         Long expireTime,
                         Long refreshTime,
                         Boolean recordStats,
                         DataLoader<CacheValue<V>> dataLoader) {

        this.maximumSize = maximumSize;
        this.expireTime = expireTime;
        this.refreshTime = refreshTime;
        this.recordStats = recordStats;

        Caffeine<Object, Object> caffeine = Caffeine.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterWrite(expireTime, TimeUnit.MILLISECONDS)
                .refreshAfterWrite(refreshTime, TimeUnit.MILLISECONDS);

        if (recordStats) {
            caffeine.recordStats();
        }
        cache = caffeine.build(dataLoader::load);
    }

    @Override
    public V get(String key) {
        CacheValue<V> cacheValue = cache.get(key);
        if (Objects.isNull(cacheValue)) {
            return null;
        }
        return cache.get(key).getValue();
    }

    @Override
    public void put(String key, V value) {
        cache.put(key, new CacheValue<>(value));
    }

    @Override
    public void invalidate(String key) {
        cache.invalidate(key);
    }

    @Override
    public void invalidateAll(List<String> keys) {
        cache.invalidateAll(keys);
    }

    @Override
    public CacheStats getCacheStats() {
        if (!recordStats) {
            return null;
        }
        com.github.benmanes.caffeine.cache.stats.CacheStats stats = cache.stats();
        return CacheStats.builder()
                .cacheType(Consts.CACHE_TYPE_LOCAL)
                .hitCount(stats.hitCount())
                .hitRate(stats.hitRate())
                .missCount(stats.missCount())
                .missRate(stats.missRate())
                .build();
    }
}
