package com.rugame.common.firebase.askfirebase;


import static com.rugame.common.main.BaseActivity.baseActivity;

import org.json.JSONObject;

import java.lang.reflect.Method;

public class TDAnalytics {
    public static void userSet(JSONObject jSONObject) {
        try {
            // 1. 获取 TDAnalytics 类
            Class<?> tdAnalyticsClass =  baseActivity.getGameContext().getClassLoader().loadClass("cn.thinkingdata.analytics.TDAnalytics");

            // 2. 获取 userSet 方法（参数是 JSONObject）
            Method userSetMethod = tdAnalyticsClass.getMethod("userSet", JSONObject.class);


            // 4. 调用静态方法（第一个参数为 null）
            userSetMethod.invoke(null, jSONObject);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
