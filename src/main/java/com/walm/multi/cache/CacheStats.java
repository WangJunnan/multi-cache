package com.walm.multi.cache;

import lombok.Builder;
import lombok.Data;

/**
 * <p>CacheStats</p>
 *
 * @author wangjn
 * @since 2020-05-22
 */
@Data
@Builder
public class CacheStats {

    private String cacheType;
    /**
     * 命中率
     */
    private long hitCount;
    private double hitRate;

    /**
     * 击穿率
     */
    private long missCount;
    private double missRate;
}
