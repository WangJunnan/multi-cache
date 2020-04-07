package com.walm.multi.cache;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

/**
 * JsonUtils
 *
 * @author wangjn
 * @date 2019/4/19
 */
public class JsonUtils {

    private static final Gson gson = new GsonBuilder()
            // 不过滤空值
            .serializeNulls()
            // 设置字段命名转换规则 --- 驼峰 eg: user_name -> userName userName -> user_name
            //.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            // 日期格式序列化规则 eg: 2019-09-23 12:00:00
            .setDateFormat("YYYY-MM-dd HH:mm:ss")
            // 设置字段序列化/反序列化过滤规则
            .excludeFieldsWithModifiers(Modifier.TRANSIENT, Modifier.STATIC)
            .create();


    /**
     * 对象序列化Json字符串
     *
     * @param obj
     * @return
     */
    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    /**
     * Json字符串反序列化对象
     *
     * @param json
     * @param clazz
     * @return
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    /**
     * Json字符串反序列化成泛型对象
     *
     * eg: List<String> ->  List<String> ss = </>fromJson(json, new TypeToken<List<String>>() {}.getType())
     *     Demo<A> ->  Demo<A> de = fromJson(json, new TypeToken<Demo<A>>() {}.getType())
     *
     * @param json
     * @param type
     * @return
     */
    public static <T> T fromJson(String json, Type type) {
        return gson.fromJson(json, type);
    }
}
