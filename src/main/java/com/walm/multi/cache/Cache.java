package com.walm.multi.cache;

import java.util.List;

/**
 * <p>Cache</p>
 *
 * @author wangjn
 * @since 2020-04-03
 */
public interface Cache<V> {

    /**
     * get value with key
     *
     * @param key
     * @return
     */
    V get(String key);

    /**
     *
     * @param key
     * @param value
     */
    void put(String key, V value);

    /**
     *
     * @param key
     */
    void invalidate(String key);

    void invalidateAll(List<String> keys);

    CacheStats getCacheStats();
}

