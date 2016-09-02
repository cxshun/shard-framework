package com.shard.jdbc.util;

import java.lang.reflect.Field;

/**
 * some useful method for operation class
 * shun
 * 2016/9/2
 */
public class ClassUtil {

    /**
     * check if the specify class has the field name
     * @param clazz
     * @param fieldName
     * @return
     */
    public static boolean hasField(Class<?> clazz, String fieldName) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field:fields) {
            if (field.getName().equals(fieldName)) {
                return true;
            }
        }
        return false;
    }

}
