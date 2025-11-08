package com.hxgame.common.main;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.webkit.WebChromeClient;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import com.hxgame.common.R;
import com.hxgame.common.firebase.jump503.MyFirebaseMessagingService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

//import top.canyie.pine.Pine;
//import top.canyie.pine.callback.MethodHook;

public class Jump503MainActivity extends BaseActivity {






    public static void onGameInit(Context PluginContext) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException, NoSuchFieldException {

        GameContenxt = PluginContext;
        Class clazz = PluginContext.getClassLoader()
                .loadClass("com.google.firebase.FirebaseApp");
// 获取initializeApp方法
        Method initializeMethod = clazz.getMethod("initializeApp", Context.class);

        // 调用initializeApp方法
        initializeMethod.invoke(null, baseActivity.getGameActivity().getApplicationContext());
        Class<?> mmMyFirebaseMessagingInstance = GameContenxt.getClassLoader().loadClass("com.cocos.game.MyFirebaseMessagingInstance");
        Class<?> APPAC = GameContenxt.getClassLoader().loadClass("com.cocos.game.AppActivity");
        if (MyFirebaseMessagingService.appActivity == null) {
            try {
                MyFirebaseMessagingService.appActivity = GameContenxt.getClassLoader().loadClass("com.cocos.game.AppActivity");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
//        Pine.hook(mmMyFirebaseMessagingInstance.getDeclaredMethod("start", APPAC), new MethodHook() {
//            @Override
//            public void beforeCall(Pine.CallFrame callFrame) throws Throwable {
//                AppObject= callFrame.args[0];
//                super.beforeCall(callFrame);
//            }
//        });



        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    new CountDownTimer(10000,1000){

                        @Override
                        public void onTick(long l) {
                            if (!task.isSuccessful()) {
                                return;
                            }
                            String token = task.getResult();
                            MyFirebaseMessagingService. sendRegistrationToServer(token);
                        }

                        @Override
                        public void onFinish() {
                            if (!task.isSuccessful()) {
                                return;
                            }
                            String token = task.getResult();
                            MyFirebaseMessagingService. sendRegistrationToServer(token);


                        }
                    }.start();
                });
            }
        });
        baseActivity.initAds();




    }



    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101) {
            try {
                Uri[] uris1 = WebChromeClient.FileChooserParams.parseResult(resultCode, data);
                callback.onReceiveValue(uris1);
            } catch (Exception e) {
                callback = null;
                e.printStackTrace();
            }
        }


    }


}
