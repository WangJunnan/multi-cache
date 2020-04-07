package com.walm.multi.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import java.util.concurrent.TimeUnit;

/**
 * <p>CaffeineCache</p>
 *
 * @author wangjn
 * @since 2020-04-03
 */
public class CaffeineCache<V> implements Cache<V> {

    final private LoadingCache<String, V> cache;

    final private Integer maximumSize;
    final private Long expireTime;
    final private Long refreshTime;

    public CaffeineCache(Integer maximumSize,
                         Long expireTime,
                         Long refreshTime,
                         DataLoader<V> dataLoader) {

        this.maximumSize = maximumSize;
        this.expireTime = expireTime;
        this.refreshTime = refreshTime;

        cache = Caffeine.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterWrite(expireTime, TimeUnit.MILLISECONDS)
                .refreshAfterWrite(refreshTime, TimeUnit.MILLISECONDS)
                .build(dataLoader::load);


    }

    @Override
    public V get(String key) {
        return cache.get(key);
    }

    @Override
    public void put(String key, V value) {
        cache.put(key, value);
    }

    @Override
    public void invalidate(String key) {
        cache.invalidate(key);
    }
}
