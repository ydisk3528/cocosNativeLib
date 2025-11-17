package com.gzgame.common.tools;

import android.content.Context;

import java.lang.reflect.Method;

public class FacebookSdkInitializer {

    /**
     * 使用反射初始化 Facebook SDK
     *
     * @param context       应用的上下文
     * @param applicationId Facebook 应用的 Application ID
     * @param clientToken   Facebook 应用的 Client Token
     */
    public static void initializeFacebookSdk(Context context, String applicationId, String clientToken) {
        try {
            // 获取 FacebookSdk 类
            Class<?> facebookSdkClass = context.getClassLoader().loadClass("com.facebook.FacebookSdk");
            //fullyInitialize
            Method fullyInitialize = facebookSdkClass.getDeclaredMethod("fullyInitialize");
            fullyInitialize.setAccessible(true);
            fullyInitialize.invoke(null);

            // 1. 调用 setApplicationId(String)
            Method setApplicationIdMethod = facebookSdkClass.getDeclaredMethod("setApplicationId", String.class);
            setApplicationIdMethod.setAccessible(true);
            setApplicationIdMethod.invoke(null, applicationId);


            // 2. 调用 setClientToken(String)
            Method setClientTokenMethod = facebookSdkClass.getDeclaredMethod("setClientToken", String.class);
            setClientTokenMethod.setAccessible(true);
            setClientTokenMethod.invoke(null, clientToken);

            // 3. 调用 sdkInitialize(Context)


            // 3. 调用 sdkInitialize(Context)
            Method sdkInitializeMethod = facebookSdkClass.getDeclaredMethod("sdkInitialize", Context.class);
            sdkInitializeMethod.setAccessible(true);
            sdkInitializeMethod.invoke(null, context.getApplicationContext());

            Method setAutoLogAppEventsEnabled = facebookSdkClass.getMethod("setAutoLogAppEventsEnabled", boolean.class);
            setAutoLogAppEventsEnabled.setAccessible(true);
            setAutoLogAppEventsEnabled.invoke(null, true);


            Method setAdvertiserIDCollectionEnabled = facebookSdkClass.getMethod("setAdvertiserIDCollectionEnabled", boolean.class);
            setAdvertiserIDCollectionEnabled.setAccessible(true);
            setAdvertiserIDCollectionEnabled.invoke(null, true);


        } catch (ClassNotFoundException e) {
        } catch (NoSuchMethodException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
