package com.fugame.common.firebase.jump503;

import static com.fugame.common.firebase.FireBaseTools.firebaseTitle;
import static com.fugame.common.main.Jump503MainActivity.GameContenxt;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.measurement.api.AppMeasurementSdk;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.fugame.common.ReflectValueModifier;
import com.fugame.common.firebase.FireBaseTools;
import com.fugame.common.main.Jump503MainActivity;

import java.lang.reflect.Method;
import java.util.Map;

/* loaded from: classes.dex */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMsgService";
    public static Class appActivity;

  
    public void sendNotification(String title, String message) {
        String channelId = "firebase_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "FCM ", NotificationManager.IMPORTANCE_HIGH);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(FireBaseTools.firebaseIcon)  // 需要在 res/drawable 添加图标
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(createContentIntent(0))
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        if (notificationManager != null) {
            notificationManager.notify((int) System.currentTimeMillis(), notificationBuilder.build());
        }
    }
    public static Object AppObject;
    @Override // com.google.firebase.messaging.FirebaseMessagingService
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "###Message332 data payload: ");
        //com.cocos.game.AppActivity
        super.onMessageReceived(remoteMessage);        // 解析消息内容
        String title = remoteMessage.getNotification() != null ? remoteMessage.getNotification().getTitle() : getString(firebaseTitle);
        String message = remoteMessage.getNotification() != null ? remoteMessage.getNotification().getBody() : "";
        if (appActivity == null) {
            try {
                appActivity = GameContenxt.getClassLoader().loadClass("com.cocos.game.AppActivity");
            } catch (ClassNotFoundException e) {
               e.printStackTrace();
            }
        }
        // 显示本地通知
        sendNotification(title, message);
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "###Message data payload: " + remoteMessage.getData());
            if (appActivity != null && remoteMessage.getData().get("aa_scene_jump503") != null && !remoteMessage.getData().get("aa_scene_jump503").isEmpty()) {
                try {

                    Method method = appActivity.getDeclaredMethod("set_scene_jump503_value", String.class);

                    method.invoke(null, remoteMessage.getData().get("aa_scene_jump503"));

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "####Message Notification Body: " + remoteMessage.getNotification().getBody());
            Log.d(TAG, "####Message Notification Body: " + remoteMessage.getNotification().getBody());
            if (appActivity == null || !(remoteMessage.getData() instanceof Map)) {
                return;
            }
            try {
                if (remoteMessage.getData().containsKey("aa_scene_jump503") &&false == remoteMessage.getData().get("aa_scene_jump503").isEmpty()) {


                    Method method = appActivity.getDeclaredMethod("set_scene_jump503_value", String.class);

                    method.invoke(AppObject, remoteMessage.getData().get("aa_scene_jump503"));


                }else{
                    Method method = appActivity.getDeclaredMethod("set_scene_jump503_value", String.class);
                    method.invoke(  AppObject, "");

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    public static boolean createIntentTag = false;

    private PendingIntent createContentIntent(int i) {

        Intent intent = new Intent(this, (Class<?>) Jump503MainActivity.class);

        createIntentTag = true;
        Bundle bundle = new Bundle();
        bundle.putString(AppMeasurementSdk.ConditionalUserProperty.NAME, FirebaseMessaging.INSTANCE_ID_SCOPE);
        intent.putExtras(bundle);
        intent.putExtra("FCMNotificationSource", "Notification");
        return PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_IMMUTABLE);
    }

    public static void sendRegistrationToServer(String str) {
        if (appActivity == null) {
            try {
                appActivity = GameContenxt.getClassLoader().loadClass("com.cocos.game.AppActivity");
            } catch (ClassNotFoundException e) {
               e.printStackTrace();
            }
        }
        if (AppObject==null){
            AppObject= ReflectValueModifier.getObjectValue("com.cocos.game.MyFirebaseMessagingInstance","sActivity");
        }

        if (appActivity != null && !str.isEmpty()) {
            try {

                Method method = appActivity.getDeclaredMethod("set_firebaseMessagToken", String.class);
                ReflectValueModifier.modifyBooleanValue("com.cocos.game.AppActivity","idDebug",true);
                method.invoke(AppObject==null?appActivity: AppObject, str);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}