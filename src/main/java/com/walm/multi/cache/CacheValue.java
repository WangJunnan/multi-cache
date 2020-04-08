package com.walm.multi.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>CacheValue</p>
 *
 * @author wangjn
 * @since 2020-04-07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CacheValue<V> {
    private V value;
}
