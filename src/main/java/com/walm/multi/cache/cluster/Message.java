package com.walm.multi.cache.cluster;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * <p>Message</p>
 *
 * @author wangjn
 * @since 2020-04-09
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    /**
     * 操作类型
     */
    private Integer operation;

    /**
     * 要操作的key
     */
    private List<String> keys;
}
