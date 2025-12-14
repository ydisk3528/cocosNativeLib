package com.x8game.common.firebase.askfirebase;

import static com.x8game.common.main.BaseActivity.baseActivity;

import android.text.TextUtils;
import android.util.Log;



import java.lang.reflect.Method;

public class TAPushTrackHelper {

    public static void onFcmTokenRegister(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        try {
            // 1. 获取 SdkManager 类对象
            Class<?> sdkManagerClass =   baseActivity.getGameContext().getClassLoader().loadClass("cn.thinkingdata.analytics.aop.push.TAPushTrackHelper");

            // 2. 获取 callbackLuaFunc 方法（两个参数：String 和 JSONObject）
            Method callbackMethod = sdkManagerClass.getMethod("onFcmTokenRegister", String.class);

            // 4. 调用静态方法（传 null 表示静态方法）
            callbackMethod.invoke(null, str);
            Log.e("SdkManager..","TAPushTrackHelper...onFcmTokenRegister:");
        } catch (Exception e) {
            Log.e("SdkManager..","TAPushTrackHelper...error:");
            e.printStackTrace();
        }
    }

}
