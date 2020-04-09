package com.walm.multi.cache.cluster;

import com.walm.multi.cache.Consts;
import com.walm.multi.cache.JsonUtils;
import com.walm.multi.cache.MultiCache;
import com.walm.multi.cache.MultiCacheException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * <p>ClusterManage</p>
 *
 * @author wangjn
 * @since 2020-04-08
 */
@Slf4j
public class ClusterMqInvoke<V> implements MessageListener {

    private StringRedisTemplate redisTemplate;
    private String redisTopic;
    private MultiCache<V> cache;
    private RedisMessageListenerContainer container;

    public ClusterMqInvoke(StringRedisTemplate redisTemplate, String redisTopic, MultiCache cache) {
        this.redisTemplate = redisTemplate;
        this.redisTopic = redisTopic;
        this.cache = cache;
        container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisTemplate.getConnectionFactory());
        container.addMessageListener(new MessageListenerAdapter(this), new ChannelTopic(redisTopic));
        container.afterPropertiesSet();
        container.start();
    }

    public void sendDel(List<String> keys) {
        String msg = JsonUtils.toJson(new com.walm.multi.cache.cluster.Message(Consts.CLUSTER_DEL, keys));
        redisTemplate.convertAndSend(redisTopic, msg);
    }

    public void sendUpdate(List<String> keys) {
        String msg = JsonUtils.toJson(new com.walm.multi.cache.cluster.Message(Consts.CLUSTER_UPDATE, keys));
        redisTemplate.convertAndSend(redisTopic, msg);
    }


    @Override
    public void onMessage(Message message, byte[] bytes) {
        log.info("multi cache cluster get msg = " + message + ", from " + redisTopic);
        com.walm.multi.cache.cluster.Message msg = JsonUtils.fromJson(new String(message.getBody(), StandardCharsets.UTF_8), com.walm.multi.cache.cluster.Message.class);
        List<String> keys = msg.getKeys();
        Integer operation = msg.getOperation();
        switch (operation) {
            case Consts.CLUSTER_DEL:
                // 批量移除key
                cache.getCaffeineCache().invalidateAll(keys);
                break;
            case Consts.CLUSTER_UPDATE:
                cache.getCaffeineCache().invalidateAll(keys);
                // get 触发一次从redis的更新
                keys.forEach(key -> cache.getCaffeineCache().get(key));
                break;
            default:
                throw new MultiCacheException("illegal operation.");
        }

    }
}
