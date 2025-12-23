package com.p8.common.firebase.askfirebase;

import static com.p8.common.firebase.FireBaseTools.firebaseTitle;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.p8.common.R;
import com.p8.common.firebase.FireBaseTools;
import com.p8.common.main.AskFireBaseMainActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.EnumMap;

/* loaded from: classes.dex */
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static String callbackFunName = "";
    private static String firebaseAppId = "";
    public static MyFirebaseMessagingService instance;
    public static Object mContext;
    private  int tagnumber = 0;
    public static boolean createIntentTag =false;
    private PendingIntent createContentIntent(int i) {
        try {
            Class fm =Class.forName("com.p8.common.MainActivity");
            Intent intent = new Intent(this, (Class<?>) fm);
//        if(MainActivity.intentmy!=null){
//            intent= MainActivity.intentmy;
//
//        }
            createIntentTag=true;


            intent.putExtra("FCMNotificationSource", "push_channel");
            return PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_IMMUTABLE);
        }catch ( Exception e){
            e.printStackTrace();
            Intent intent = new Intent(this, (Class<?>) AskFireBaseMainActivity.class);
            intent.putExtra("FCMNotificationSource", "push_channel");
            return PendingIntent.getActivity(this, i, intent, PendingIntent.FLAG_IMMUTABLE);
        }

    }
    public  void sendNotification(String title, String message){
        String channelId = "push_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent pendingIntent= createContentIntent(tagnumber);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(FireBaseTools.firebaseIcon)  // 需要在 res/drawable 添加图标
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        if (Build.VERSION.SDK_INT >= 31) {
            notificationBuilder.setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE);
        }

        if (notificationManager != null) {
            int notificationId = (int) System.currentTimeMillis();
            notificationManager.notify(notificationId, notificationBuilder.build());
        }



        tagnumber=tagnumber+1;
    }
    @Override // com.google.firebase.messaging.FirebaseMessagingService
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            Log.d("NewToken", "onMessageReceived: " + remoteMessage.getNotification().getTitle() + remoteMessage.getNotification().getBody());
        }
        if (remoteMessage.getData().size() > 0) {
            Log.d("NewToken", "notice: " + remoteMessage.getData());
            SdkManager.callbackLuaFunc("callbackFunName_FireBasePushInfo", new JSONObject(remoteMessage.getData()));
        }
        String title = remoteMessage.getNotification() != null ? remoteMessage.getNotification().getTitle() : getString(firebaseTitle);
        String message = remoteMessage.getNotification() != null ? remoteMessage.getNotification().getBody() : "";
        sendNotification(title, message);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createPushNotificationChannel();
        init();


    }
    public static void  onnewmy(String token){
        TAPushTrackHelper.onFcmTokenRegister(token);
        Log.d("NewToken", "New token: " + token);
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("fcm_token", token);
            TDAnalytics.userSet(jSONObject);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }


    public static MyFirebaseMessagingService getInstance() {
        if (instance == null) {
            instance = new MyFirebaseMessagingService();
        }
        return instance;
    }

    public  void init() {
        initFirebaseAppId();
        askSetConsent();
    }

    public  void askSetConsent() {
        EnumMap enumMap = new EnumMap(FirebaseAnalytics.ConsentType.class);
        enumMap.put(FirebaseAnalytics.ConsentType.ANALYTICS_STORAGE,FirebaseAnalytics.ConsentStatus.GRANTED);
        enumMap.put( FirebaseAnalytics.ConsentType.AD_STORAGE,  FirebaseAnalytics.ConsentStatus.GRANTED);
        enumMap.put( FirebaseAnalytics.ConsentType.AD_USER_DATA,  FirebaseAnalytics.ConsentStatus.GRANTED);
        enumMap.put( FirebaseAnalytics.ConsentType.AD_PERSONALIZATION, FirebaseAnalytics.ConsentStatus.GRANTED);
        FirebaseAnalytics.getInstance(this).setConsent(enumMap);
    }

    public  void initFirebaseAppId() {
        FirebaseAnalytics.getInstance(this).getAppInstanceId().addOnCompleteListener(new OnCompleteListener() { // from class: com.game.javascript.sdk.-$$Lambda$MyFirebaseMessagingService$JTsYfco3HBR8AGojpG8OqM5a800
            @Override // com.google.android.gms.tasks.OnCompleteListener
            public final void onComplete(Task task) {
                MyFirebaseMessagingService.lambdaggz(task);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambdaggz(Task task) {
        if (task.isSuccessful()) {
            String str = (String) task.getResult();
            firebaseAppId = str;
            endFirebaseAppId();
            Log.d("FirebaseAppInstanceId", "App Instance ID: " + str);
            return;
        }
        Log.e("FirebaseAppInstanceId", "Failed to get App Instance ID", task.getException());
    }

    public static void getFirebaseClientId(String callName) {
        callbackFunName = callName;
        endFirebaseAppId();
    }
    private  void createPushNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            // 设置通知声音
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            NotificationChannel channel = new NotificationChannel(
                    "push_channel",
                    "推送消息",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("应用推送消息通知");
            channel.enableLights(true);
            channel.setSound(soundUri,audioAttributes);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            channel.setVibrationPattern(new long[]{0, 300, 200, 300});
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationManager manager = this.getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
    public static void endFirebaseAppId() {
        String str = firebaseAppId;
        if (str != null) {
            if (str.equals("")) {
                return;
            }
            try {
                JSONObject jSONObject = new JSONObject();
                jSONObject.put("firebaseAppId", firebaseAppId);
                SdkManager.callbackLuaFunc(callbackFunName, jSONObject);
            } catch (Exception unused) {
            }
        }
        callbackFunName = null;
    }
}