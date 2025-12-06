package com.igame.common.firebase.askfirebase;

import static com.igame.common.main.BaseActivity.baseActivity;

import android.util.Log;


import org.json.JSONObject;

import java.lang.reflect.Method;

public class SdkManager {

    public static void callbackLuaFunc(String callbackFunNameFireBasePushInfo, JSONObject jsonObject) {
        try {
            // 1. 获取 SdkManager 类对象
            Class<?> sdkManagerClass = baseActivity.getGameContext().getClassLoader().loadClass("com.game.javascript.sdk.SdkManager");

            // 2. 获取 callbackLuaFunc 方法（两个参数：String 和 JSONObject）
            Method callbackMethod = sdkManagerClass.getMethod("callbackLuaFunc", String.class, JSONObject.class);

            // 4. 调用静态方法（传 null 表示静态方法）
            callbackMethod.invoke(null, callbackFunNameFireBasePushInfo, jsonObject);
            Log.e("SdkManager..","SdkManager...ok:"+callbackFunNameFireBasePushInfo  + "data:"+jsonObject.toString());
        } catch (Exception e) {
            Log.e("SdkManager..","SdkManager...error:"+callbackFunNameFireBasePushInfo);
            e.printStackTrace();
        }


    }
}
