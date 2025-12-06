package com.igame.common.delegate;

import static com.igame.common.main.BaseActivity.baseActivity;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.lang.reflect.Method;

/* loaded from: classes.dex */
public class MyGameBDService2 extends Service {
    public static Class<?> clazz;
    public static Object localServiceInstance;
    public static DBCallBack dbCallBack;

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        if (clazz == null) {
            return;
        }
        try {
            this.localServiceInstance = clazz.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            Method declaredMethod = clazz.getDeclaredMethod("onCreate", new Class[0]);
            declaredMethod.setAccessible(true);
            declaredMethod.invoke(this.localServiceInstance, baseActivity);
        } catch (Exception e) {
            e.printStackTrace();
            if (dbCallBack != null) {
                dbCallBack.onCreate();
            }
        }
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int i, int i2) {
        if (clazz == null) {
            return super.onStartCommand(intent, i, i2);
        }
        try {

            Method declaredMethod = clazz.getDeclaredMethod("onStartCommand", Intent.class, Integer.TYPE, Integer.TYPE);
            declaredMethod.setAccessible(true);
            declaredMethod.invoke(this.localServiceInstance, intent, Integer.valueOf(i), Integer.valueOf(i2));
            return super.onStartCommand(intent, i, i2);
        } catch (Exception e) {
            e.printStackTrace();
            if (dbCallBack != null) {
                return dbCallBack.onStartCommond();
            }
        }
        return super.onStartCommand(intent, i, i2);

    }


}