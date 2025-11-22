package com.tkgame.common.delegate;

import static com.tkgame.common.main.BaseActivity.baseActivity;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import java.lang.reflect.Method;

/* loaded from: classes.dex */
public class LocalService extends Service {
    public static Class<?> clazz;
    Object localServiceInstance;

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
            declaredMethod.invoke(this.localServiceInstance,baseActivity);
        } catch (Exception e) {
            e.printStackTrace();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, i, i2);
    }


}