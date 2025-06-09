package com.lens.common.base.controller;

import com.lens.common.base.constant.Constants;

import net.sf.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller基类
 *
 * @Author zhenac
 * @Created 5/30/25 7:48 AM
 */
public class SuperController {

    /**
     * 获取一个map
     *
     * @return
     */
    public static Map<String, Object> getMap() {
        Map<String, Object> map = new HashMap<>(Constants.NUM_ONE);
        return map;
    }

    /**
     * 将map转换成json字符串
     *
     * @param map
     * @return
     */
    public String toJson(Map<String, Object> map) {
        return JSONObject.fromObject(map).toString();
    }

    public <T> String toJson(List<T> list) {
        return JSONObject.fromObject(list).toString();
    }
}
