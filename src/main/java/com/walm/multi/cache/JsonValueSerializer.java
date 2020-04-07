package com.walm.multi.cache;

import com.google.gson.reflect.TypeToken;

/**
 * <p>JsonValueSerializer</p>
 *
 * @author wangjn
 * @since 2020-04-04
 */
public class JsonValueSerializer<V> implements ValueSerializer<V>{

    private TypeToken<? super V> typeToken;

    public JsonValueSerializer(TypeToken<? super V> typeToken) {
        this.typeToken = typeToken;
    }

    @Override
    public String serialize(V obj) {
        return JsonUtils.toJson(obj);
    }

    @Override
    public V deserialize(String value) {
        return JsonUtils.fromJson(value, typeToken.getType());
    }
}
