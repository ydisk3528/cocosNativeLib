package com.rsgame.common.firebase.pushid;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

import static com.rsgame.common.main.BaseActivity.FirebaseActivityCls;
import static com.rsgame.common.main.BaseActivity.baseActivity;
import static com.rsgame.common.main.CommonActivity.GameContenxt;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.android.games.paddleboat.GameControllerManager;
import com.google.android.gms.measurement.api.AppMeasurementSdk;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.rsgame.common.GameSaveTools;
import com.rsgame.common.firebase.FireBaseTools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/* loaded from: classes.dex */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "myids";
    public static Class LocalAppAccActivity;
    public static Class JsbBridge;

    private void scheduleJob() {
    }
    private void javabrig(String xs) {
        try {
            if (baseActivity.getGameContext() == null) {
                Log.e("MyFirebase:", "GameC is null");
                return;
            }
            // 获取 JsbBridge 类的 Class 对象
            MyFirebaseMessagingService.JsbBridge = baseActivity.getGameContext() .getClassLoader().loadClass("com.cocos.game.JsbBridge");
            Class<?> jsbBridgeClass = JsbBridge;
            if (jsbBridgeClass == null) {
                return;
            }
            //{"push_id":656}
            // 获取 MessageReceivedCallBack 方法，参数为 String 类型
            Method method = jsbBridgeClass.getDeclaredMethod("MessageReceivedCallBack", String.class);

            // 调用静态方法
            method.setAccessible(true); // 如果方法是私有的，需要设置为可访问
            method.invoke(null, xs); // 静态方法传递 null 作为对象实例

        } catch (NoSuchMethodException | IllegalAccessException |
                 InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    private void sendRegistrationToServer(String str) {

    }

    public static String FCMTOKEN = "";

    private void onnewmy() {

        try {


            // 获取 AppCcActivity 类的 Class 对象
            MyFirebaseMessagingService.JsbBridge = baseActivity.getGameContext() .getClassLoader().loadClass("com.cocos.game.JsbBridge");
            MyFirebaseMessagingService.LocalAppAccActivity = baseActivity.getGameContext() .getClassLoader().loadClass("com.cocos.game.AppCcActivity");
            Class<?> appCcActivityClass = LocalAppAccActivity;
            Log.e("LocalAppAccActivity:", String.valueOf(LocalAppAccActivity == null));
            if (LocalAppAccActivity == null) {
                Log.e("LocalAppAccActivity:", "is null");
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                MyFirebaseMessagingService.JsbBridge = GameContenxt .getClassLoader().loadClass("com.cocos.game.JsbBridge");
                MyFirebaseMessagingService.LocalAppAccActivity =GameContenxt .getClassLoader().loadClass("com.cocos.game.AppCcActivity");
                Class<?> appCcActivityClass = LocalAppAccActivity;
                Log.e("LocalAppAccActivity:", String.valueOf(LocalAppAccActivity == null));
                if (LocalAppAccActivity == null) {
                    Log.e("LocalAppAccActivity:", "is null");
                    return;
                }
            }catch (Exception nnnnn){
                nnnnn.printStackTrace();
            }
        }
//        sendRegistrationToServer(str);
    }

    @Override
    public void onCreate() {
        super.onCreate();


        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            new CountDownTimer(10000, 1000) {

                @Override
                public void onTick(long l) {
                    onnewmy();
                }

                @Override
                public void onFinish() {
                    onnewmy();


                }
            }.start();
        });


    }

    public void gettoken() {
        if (LocalAppAccActivity != null) {
            return;
        }
        try {
            // 获取 AppCcActivity 类
            Log.e("token", "111");
            Class<?> appCcActivityClass = LocalAppAccActivity;

            // 获取 getInstance 方法
            Method getInstanceMethod = appCcActivityClass.getDeclaredMethod("getInstance");

            // 调用 getInstance 获取单例实例
            Object appCcActivityInstance = getInstanceMethod.invoke(null);  // 因为getInstance是静态方法，所以传null

            // 获取 GetFCMToken 方法
            Method getFCMTokenMethod = appCcActivityClass.getDeclaredMethod("GetFCMToken");

            // 调用 GetFCMToken 方法
            getFCMTokenMethod.invoke(appCcActivityInstance);  // 调用实例的方法
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String str = "";
        String str2 = "";
        String str3 = "";
        super.onMessageReceived(remoteMessage);

        Map<String, String> data = remoteMessage.getData();

        int r8 = (int) System.currentTimeMillis();
        if (data.get("payload") != null) {
            try {
                JSONObject jSONObject = new JSONObject(data.get("payload"));
                r8 = jSONObject.has("push_id") ? jSONObject.getInt("push_id") : 0;
                javabrig(data.get("payload"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "getData: " + data);
        RemoteMessage.Notification notification = remoteMessage.getNotification();

        String str4 = data.get("jump") != null ? data.get("jump") : "";
        if (str4 != null && str4.isEmpty() == false) {
            scheduleJob();
            myids = str4;
        }

        if (notification != null) {
            String body = notification.getBody();
            String title = notification.getTitle();
            str3 = String.valueOf(notification.getImageUrl());
            str2 = body;
            str = title;
        } else {
            String str5 = data.get("notice");
            if (str5 != null) {
                try {
                    JSONObject jSONObject2 = new JSONObject(str5);
                    String string = jSONObject2.has("title") ? jSONObject2.getString("title") : "";
                    String string2 = jSONObject2.has("body") ? jSONObject2.getString("body") : "";
                    String string3 = jSONObject2.has("image") ? jSONObject2.getString("image") : "";
                    str = string;
                    str2 = string2;
                    str3 = string3;
                } catch (JSONException e2) {
                    e2.printStackTrace();
                }
            } else {
                str = "";
                str2 = str;
                str3 = str2;
            }
        }

        sendNotification(str, str2, str3, r8, str4);
    }

    public static String myids = "";


    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    private void sendNotification(String str, String str2, String str3, int i, String str4) {
        String string = getString(getResources().getIdentifier("default_message_title", "string", getPackageName()));
        if (str == null || str.isEmpty()) {
            str = string;
        }
        String string2 = getString(getResources().getIdentifier("default_notification_channel_id", "string", getPackageName()));
        NotificationCompat.Builder contentIntent = new NotificationCompat.Builder(this, string2)
                .setSmallIcon(FireBaseTools.firebaseIcon) // 需要在 res/drawable 添加图标
                .setContentTitle(str).setContentText(str2)
                .setLargeIcon(getBitmapFromURL(str3))
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(createContentIntent(i, str4, str, str2));
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            CharSequence name = str;
            String description = str2;
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(string2, name, importance);
            channel.setDescription(description);
            channel.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION), null); // 设置声音
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify((int) System.currentTimeMillis(), contentIntent.build());
    }


    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    private PendingIntent createContentIntent(int i, String str, String str2, String str3) {

        Intent intent = new Intent(this, FirebaseActivityCls);
        Bundle bundle = new Bundle();
        bundle.putString(AppMeasurementSdk.ConditionalUserProperty.NAME, FirebaseMessaging.INSTANCE_ID_SCOPE);
        intent.putExtras(bundle);
        intent.putExtra("FCMNotificationSource", "Notification");
        intent.putExtra("FCMPushId", i);
        intent.putExtra("FCMJump", str);
        intent.putExtra("push_id", i);
        intent.putExtra("FCMTitle", str2);
        intent.putExtra("FCMBody", str3);
        GameSaveTools.getInstance(this).putInt("FCMPushId", i);
        GameSaveTools.getInstance(this).putString("FCMJump", str);
        myids = str;
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(FLAG_ACTIVITY_CLEAR_TOP | FLAG_ACTIVITY_SINGLE_TOP);

        GameSaveTools.getInstance(this).putArrString("FCMJump", str);
        GameSaveTools.getInstance(this).putInt("push_id", i);
        GameSaveTools.getInstance(this).putString("FCMTitle", str2);
        GameSaveTools.getInstance(this).putString("FCMBody", str3);
        gamesaveIntent = intent;
        int piFlags = GameControllerManager.DEVICEFLAG_BATTERY;
        return PendingIntent.getActivity(this, i, intent, piFlags);
    }

    public static Intent gamesaveIntent;

    public static Bitmap getBitmapFromURL(String str) {
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(str).openConnection();
            httpURLConnection.setDoInput(true);
            httpURLConnection.connect();
            return BitmapFactory.decodeStream(httpURLConnection.getInputStream());
        } catch (IOException unused) {
            return null;
        }
    }
}