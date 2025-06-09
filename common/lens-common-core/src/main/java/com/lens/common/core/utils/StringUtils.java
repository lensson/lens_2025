package com.lens.common.core.utils;

import com.lens.common.base.utils.BaseStringUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 对字符串转换的一些操作
 *
 * @author 陌溪
 * @date 2020年9月20日10:56:45
 */
@Slf4j
public class StringUtils extends BaseStringUtils {
    /**
     * 获取雪花UID
     * @return
     */
    public static Long getSnowflakeId() {
        SnowflakeIdWorker snowflakeIdWorker = new SnowflakeIdWorker(0, 0);
        return snowflakeIdWorker.nextId();
    }


    public static void main(String[] args) {
        System.out.println(underLine(new StringBuffer("dogId")));
    }
}
