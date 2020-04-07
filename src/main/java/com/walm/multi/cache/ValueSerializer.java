package com.walm.multi.cache;

import org.springframework.lang.Nullable;

/**
 * <p>ValueSerializer</p>
 *
 * @author wangjn
 * @since 2020-04-04
 */
public interface ValueSerializer<V> {

    String serialize(@Nullable V var1);

    V deserialize(@Nullable String value);
}
