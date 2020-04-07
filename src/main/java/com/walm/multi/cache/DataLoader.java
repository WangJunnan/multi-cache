package com.walm.multi.cache;

/**
 * <p>DataLoader</p>
 *
 * @author wangjn
 * @since 2020-04-03
 */
@FunctionalInterface
public interface DataLoader<V> {

    V load(String key);
}
