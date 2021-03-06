/*
 * Copyright (c) 2018. syshlang
 * @File: EnumUtil.java
 * @Description:
 * @Author: sunys
 * @Date: 18-8-30 下午10:28
 * @since:
 */

package com.syshlang.common.util.enumutil;

import com.syshlang.common.base.BaseException;
import org.apache.commons.lang3.EnumUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 枚举类工具
 * @author sunys
 */
public class EnumUtil extends EnumUtils {
    /**
     *
     * @param enumInstance 枚举常量
     * @param value 枚举常量的value
     * @param <T> 枚举的类型参数
     */
    public static <T extends Enum<T>> void changeNameTo(T enumInstance, String value) {
        try {
            Field fieldName = enumInstance.getClass().getSuperclass().getDeclaredField("name");
            fieldName.setAccessible(true);
            fieldName.set(enumInstance, value);
            fieldName.setAccessible(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 枚举类型的比较，包括null
     * @param enum1
     * @param enum2
     * @return 如果相等返回true, 否则返回false
     */
    public static boolean enumEquals(Enum enum1, Enum enum2) {
        if (enum1 == null && enum2 == null) {
            return true;
        } else if (enum1 != null) {
            return enum1.equals(enum2);
        } else {
            return enum2.equals(enum1);
        }
    }

    public static <E extends Enum<E>> String  getEnumDesc(Class<E> enumClass, String key) {
        try {
            if (key == null) {
                return null;
            }
            String enumName  = enumClass.getSimpleName()+"_"+key.toUpperCase();
            E e = Enum.valueOf(enumClass, enumName);
            if (e == null){
                return null;
            }
            Method method = e.getClass().getDeclaredMethod("getDesc");
            Object result = method.invoke(e);
            if (result != null){
                return String.valueOf(result);
            }
        } catch (IllegalArgumentException | NoSuchMethodException
                | IllegalAccessException | InvocationTargetException var3) {
            return null;
        }
        return null;
    }

    public static  <E extends Enum<E>>  BaseException productException(E e,Object[] args,String module){
        BaseException baseException = new BaseException();
        try {
            if (e == null) {
                return null;
            }
            Method methodCode = e.getClass().getDeclaredMethod("getCode");
            Object resultCode = methodCode.invoke(e);
            if (resultCode != null){
                baseException.setCode(Integer.valueOf(String.valueOf(resultCode)));
            }
            Method methodCodeStr = e.getClass().getDeclaredMethod("getCodeStr");
            Object resultCodeStr = methodCodeStr.invoke(e);
            if (resultCodeStr != null){
                baseException.setCodeStr(String.valueOf(resultCodeStr));
            }
            Method methodDesc = e.getClass().getDeclaredMethod("getDesc");
            Object resultDesc = methodDesc.invoke(e);
            if (resultDesc != null){
                baseException.setDefaultMessage(String.valueOf(resultDesc));
            }
            if (args != null){
                baseException.setArgs(args);
            }
            baseException.setModule(module);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e1) {
        }
        return baseException;
    }

}
