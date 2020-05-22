package com.walm.multi.cache;

import lombok.Data;

import java.util.concurrent.atomic.LongAdder;

/**
 * <p>CacheStatsCounter</p>
 *
 * @author wangjn
 * @since 2020-05-22
 */
@Data
public class CacheStatsCounter {
    private LongAdder missCount;
    private LongAdder hitCount;

    public CacheStatsCounter() {
        this.missCount = new LongAdder();
        this.hitCount = new LongAdder();
    }

    public void incrMissCount() {
        missCount.increment();
    }

    public void incrHitCount() {
        hitCount.increment();
    }

    public long missCount() {
        return missCount.sum();
    }

    public long hitCount() {
        return hitCount.sum();
    }
}
