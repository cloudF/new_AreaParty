package com.dkzy.areaparty.phone.utils_comman.jsonFormat;

import com.google.gson.Gson;

import java.util.List;


/**
 * Created by boris on 2016/12/16.
 * Json工具类
 */

public class JsonUitl {
    private static Gson mGson = null;
    static {
        if(mGson == null) {
            mGson = new Gson();
        }
    }

    /**
     * 将json字符串转化成实体对象
     * @param json json字符串
     * @param cls 对象从属的类
     * @return 反序列化得到的对象
     */
    public static <T> T stringToBean(String json, Class<T> cls) {
        T t = null;
        if(mGson != null) {
            t = mGson.fromJson(json, cls);
        }
        return t;
    }


    /**
     * 将对象准换为json字符串 或者 把list 转化成json
     * @param object 对象
     * @param <T>对象类型
     * @return 序列化后的Json字符串
     */
    public static <T> String objectToString(T object) {
        return mGson.toJson(object);
    }

    public static <T> String objectListToString(List<T> object) {
        return mGson.toJson(object);
    }
}
