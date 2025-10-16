package com.mmpgame.common.main;



import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mmpgame.common.GameSaveTools;
import static com.mmpgame.common.firebase.pushid.MyFirebaseMessagingService.myids;

import org.json.JSONException;
import org.json.JSONObject;

import com.mmpgame.common.MyAdTools;
import com.mmpgame.common.R;
import  com.mmpgame.common.firebase.pushid.MyFirebaseMessagingService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import top.canyie.pine.Pine;
import top.canyie.pine.callback.MethodHook;


public class PushIdMainActivity extends BaseActivity {
    

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        String id = getIntent().getStringExtra("jump");
        myids = id;
        Bundle extras = getIntent().getExtras();
        try {
            if (extras != null) {
                myids = extras.get("FCMJump").toString();
                GameSaveTools.getInstance(this).putInt("FCMPushId", (Integer) extras.get("FCMPushId"));
                GameSaveTools.getInstance(this).putString("FCMTitle", extras.get("FCMTitle").toString());
                GameSaveTools.getInstance(this).putString("FCMBody", extras.get("FCMBody").toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (getIntent().getStringExtra("payload") != null) {
            try {
                JSONObject jSONObject = new JSONObject(getIntent().getStringExtra("payload"));
                int r8 = jSONObject.has("push_id") ? jSONObject.getInt("push_id") : 0;
                GameSaveTools.getInstance(this).putInt("FCMPushId", r8);
                GameSaveTools.getInstance(this).putInt("push_id", r8);
            } catch (JSONException e) {
                GameSaveTools.getInstance(this).putInt("FCMPushId", 0);
                GameSaveTools.getInstance(this).putInt("push_id", 0);
            }
        }
        super.onCreate(savedInstanceState);
      



    }
 

    public static Context GameContenxt;
    public static Activity GameActivity;

    public static void onGameIntent(Activity activity) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // 1. 通过反射获取 AppCcActivity 类

        Class<?> cls = GameContenxt.getClassLoader().loadClass("com.cocos.game.AppCcActivity");

        // 2. 反射调用静态方法 getInstance() 获取单例对象
        Method getInstanceMethod = cls.getDeclaredMethod("getInstance");
        Object activityInstance = getInstanceMethod.invoke(null); // 静态方法，所以传 null

        if (activityInstance != null) {
            // ====== 调用 dealExtras(boolean, boolean, Intent) ======
            Method dealExtrasMethod = cls.getDeclaredMethod(
                    "dealExtras",
                    boolean.class,
                    boolean.class,
                    Intent.class
            );
            dealExtrasMethod.setAccessible(true);

            Intent intent1 = new Intent();
            intent1 = activity.getIntent();

            dealExtrasMethod.invoke(activityInstance, true, true, intent1);
            myids="";

        }
    }

    public static void onGameRead(Activity activity) {
        Intent intent = activity.getIntent();

        intent.putExtra("FCMNotificationSource", "Notification");
        intent.putExtra("FCMPushId", GameSaveTools.getInstance(activity).getInt("FCMPushId", 1000));
        intent.putExtra("FCMJump", myids);
        intent.putExtra("FCMTitle", GameSaveTools.getInstance(activity).getString("FCMTitle", ""));
        intent.putExtra("FCMBody", GameSaveTools.getInstance(activity).getString("FCMBody", ""));
        try {

            onGameIntent(activity);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean readfirst = false;

    public static void onGameInit(Context PluginContext) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException, NoSuchFieldException {

        GameContenxt = PluginContext;
        MyFirebaseMessagingService.JsbBridge = PluginContext.getClassLoader().loadClass("com.cocos.game.JsbBridge");
        MyFirebaseMessagingService.LocalAppAccActivity = PluginContext.getClassLoader().loadClass("com.cocos.game.AppCcActivity");
        Class clazz = GameContenxt.getClassLoader()
                .loadClass("com.google.firebase.FirebaseApp");
// 获取initializeApp方法
        Method initializeMethod = clazz.getMethod("initializeApp", Context.class);

        // 调用initializeApp方法
        initializeMethod.invoke(null, baseActivity.getApplicationContext());



        Pine.hook(MyFirebaseMessagingService.JsbBridge.getDeclaredMethod("GetFCMTokenCallBack", String.class), new MethodHook() {
            @Override
            public void beforeCall(Pine.CallFrame callFrame) throws Throwable {
                readfirst = true;
                Class<?> cls = GameContenxt.getClassLoader().loadClass("com.cocos.game.AppCcActivity");
                FirebaseMessaging.getInstance().getToken()
                        .addOnCompleteListener(new OnCompleteListener<String>() {
                            @Override
                            public void onComplete(@NonNull Task<String> task) {

                            }
                        });

                // 2. 反射调用静态方法 getInstance() 获取单例对象
                Method getInstanceMethod = cls.getDeclaredMethod("getInstance");
                Object activityInstance = getInstanceMethod.invoke(null); // 静态方法，所以传 null
                onGameRead((Activity) activityInstance);
                super.beforeCall(callFrame);
            }
        });
        String appid = MyAdTools.adid;
        String insertid = MyAdTools.insertId;
        String bannerid =MyAdTools.bannerid;
        String nativeid = MyAdTools.navid;
        baseActivity.getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

                if (activity.getComponentName().toString().contains("AppCcActivity")) {
                    GameActivity = activity;
                }

                onGameRead(activity);

                        baseActivity.initAds();




            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {


            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {


            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {


            }
        });




    }


    public static Intent intentmy;

    public static ValueCallback<Uri[]> callback;

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Intent myintent =intent;
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
