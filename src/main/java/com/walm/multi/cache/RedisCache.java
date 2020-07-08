package com.walm.multi.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * <p>RedisCache</p>
 *
 * @author wangjn
 * @since 2020-04-03
 */
@Slf4j
public class RedisCache<V> implements Cache<V> {

    private DataLoader<V> dataLoader;
    private Long expireTime;
    private StringRedisTemplate redisTemplate;
    private ValueSerializer<V> valueSerializer;
    private Boolean recordStats;
    private CacheStatsCounter cacheStatsCounter;

    public RedisCache(StringRedisTemplate redisTemplate,
                      Long expireTime,
                      ValueSerializer serializer,
                      Boolean recordStats,
                      DataLoader<V> dataLoader) {
        this(redisTemplate, expireTime, serializer, recordStats);
        this.dataLoader = dataLoader;
    }

    public RedisCache(StringRedisTemplate redisTemplate,
                      Long expireTime,
                      ValueSerializer serializer,
                      Boolean recordStats) {
        this.expireTime = expireTime;
        this.redisTemplate = redisTemplate;
        this.valueSerializer = serializer;
        this.recordStats = recordStats;
        this.cacheStatsCounter = new CacheStatsCounter();
    }

    @Override
    public V get(String key) {
        log.info("multi cache load from redis key = {}", key);
        String value = redisTemplate.opsForValue().get(key);
        // TODO resolve thead safe ?
        // TODO double check lock ?
        if (recordStats) {
            if (value == null) {
                cacheStatsCounter.incrMissCount();
            } else {
                cacheStatsCounter.incrHitCount();
            }
        }

        if (Objects.isNull(value) && Objects.nonNull(dataLoader)) {
            V newValue = dataLoader.load(key);
            if (Objects.nonNull(newValue)) {
                value = valueSerializer.serialize(newValue);
                redisTemplate.opsForValue().set(key, value, expireTime, TimeUnit.MILLISECONDS);
                return newValue;
            } else {
                redisTemplate.opsForValue().set(key, Consts.EMPTY_VALUE, expireTime, TimeUnit.MILLISECONDS);
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

    @Override
    public CacheStats getCacheStats() {
        if (!recordStats) {
            return null;
        }
        long hitCount = cacheStatsCounter.hitCount();
        long missCount = cacheStatsCounter.missCount();
        long totalRequest = hitCount + missCount;
        return CacheStats.builder()
                .cacheType(Consts.CACHE_TYPE_REMOTE)
                .hitCount(hitCount)
                .hitRate(totalRequest == 0L ? 0.0D : (double) hitCount / (double) totalRequest)
                .missCount(missCount)
                .missRate(totalRequest == 0 ? 0.0D : (double) missCount / (double) totalRequest)
                .build();
    }
}
