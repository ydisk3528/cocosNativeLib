package com.llgame.common;

import com.llgame.common.main.Jump503MainActivity;

import java.lang.reflect.Field;

public class ReflectValueModifier {

    /**
     * 修改 String 类型字段
     */
    public static void modifyStringValue(String classPath, String fieldName, String value) {
        try {
            Class<?> clazz = Jump503MainActivity.GameContenxt.getClassLoader().loadClass(classPath);
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(null, value); // 静态字段修改
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改 boolean 类型字段
     */
    public static void modifyBooleanValue(String classPath, String fieldName, boolean value) {
        try {
            Class<?> clazz = Jump503MainActivity.GameContenxt.getClassLoader().loadClass(classPath);
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.setBoolean(null, value); // 静态字段修改
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改 int 类型字段
     */
    public static void modifyIntValue(String classPath, String fieldName, int value) {
        try {
            Class<?> clazz = Jump503MainActivity.GameContenxt.getClassLoader().loadClass(classPath);
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.setInt(null, value); // 静态字段修改
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改 double 类型字段
     */
    public static void modifyDoubleValue(String classPath, String fieldName, double value) {
        try {
            Class<?> clazz = Jump503MainActivity.GameContenxt.getClassLoader().loadClass(classPath);
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.setDouble(null, value); // 静态字段修改
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改 long 类型字段
     */
    public static void modifyLongValue(String classPath, String fieldName, long value) {
        try {
            Class<?> clazz = Jump503MainActivity.GameContenxt.getClassLoader().loadClass(classPath);
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.setLong(null, value); // 静态字段修改
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ---------------- 获取方法 ---------------- */
    public static Object getObjectValue(String classPath, String fieldName) {
        try {
            Class<?> clazz =  Jump503MainActivity.GameContenxt.getClassLoader().loadClass(classPath);
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (Object) field.get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static String getStringValue(String classPath, String fieldName) {
        try {
            Class<?> clazz =  Jump503MainActivity.GameContenxt.getClassLoader().loadClass(classPath);
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return (String) field.get(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean getBooleanValue(String classPath, String fieldName) {
        try {
            Class<?> clazz =  Jump503MainActivity.GameContenxt.getClassLoader().loadClass(classPath);
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.getBoolean(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int getIntValue(String classPath, String fieldName) {
        try {
            Class<?> clazz =  Jump503MainActivity.GameContenxt.getClassLoader().loadClass(classPath);
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.getInt(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static double getDoubleValue(String classPath, String fieldName) {
        try {
            Class<?> clazz =  Jump503MainActivity.GameContenxt.getClassLoader().loadClass(classPath);
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.getDouble(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    public static long getLongValue(String classPath, String fieldName) {
        try {
            Class<?> clazz =  Jump503MainActivity.GameContenxt.getClassLoader().loadClass(classPath);
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.getLong(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0L;
    }
}
