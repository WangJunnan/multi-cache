package com.walm.multi.cache;

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
    private boolean autuLoad;

    public MultiCache(MultiCacheBuilder<? super V> builder, DataLoader<V> dataLoader) {
        // builder
        this.redisCache = new RedisCache<>(builder.getRedisTemplate(), builder.getRedisExpireTime(), builder.getValueSerializer(), dataLoader);
        this.caffeineCache = new CaffeineCache<>(builder.getCaffeineMaxSize(), builder.getCaffeineExpireTime(), builder.getCaffeineRefreshTime(), (key)->redisCache.get(key));
    }

    public MultiCache(MultiCacheBuilder<? super V> builder) {
        // builder
        this.redisCache = new RedisCache<>(builder.getRedisTemplate(), builder.getRedisExpireTime(), builder.getValueSerializer(), dataLoader);
        this.caffeineCache = new CaffeineCache<>(builder.getCaffeineMaxSize(), builder.getCaffeineExpireTime(), builder.getCaffeineRefreshTime(), (key)->redisCache.get(key));
    }


    @Override
    public V get(String key) {
        return caffeineCache.get(key);
    }

    @Override
    public void put(String key, V value) {
//        secondCache.put(key, value);
//        firstCache.invalidate(key);
        redisCache.put(key, value);
        caffeineCache.invalidate(key);
    }

    @Override
    public void invalidate(String key) {
//        secondCache.invalidate(key);
//        firstCache.invalidate(key);
        redisCache.invalidate(key);
        caffeineCache.invalidate(key);
    }
}
