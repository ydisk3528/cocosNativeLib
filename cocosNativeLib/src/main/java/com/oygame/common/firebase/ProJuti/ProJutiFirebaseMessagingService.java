package com.oygame.common.firebase.ProJuti;

import static com.oygame.common.firebase.FireBaseTools.firebaseTitle;
import static com.oygame.common.main.CommonActivity.GameContenxt;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.measurement.api.AppMeasurementSdk;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.oygame.common.ReflectValueModifier;
import com.oygame.common.firebase.FireBaseTools;
import com.oygame.common.main.Jump503MainActivity;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.Map;

public class ProJutiFirebaseMessagingService extends FirebaseMessagingService {
    protected String callCls = "com.cocos.game.ProjUtil";
    private String escapeJs(String str) {
        return str == null ? "" : str.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
    @Override
    public void onNewToken(String str) {
        super.onNewToken(str);
        if (ProjUtil == null) {
            try {
                ProjUtil = GameContenxt.getClassLoader().loadClass(callCls);
                try {
                    Method RunJs = ProjUtil.getDeclaredMethod("RunJs", String.class);
                    RunJs.invoke(null, "try {\n            JsbridgeUtil.PassNewGGToken(\"" + str + "\")\n        } catch (error) {\n            console.log(\"JsbridgeUtil.PassNewGGToken error>>>>>> \")\n        }");
                    ReflectValueModifier.modifyStringValue(callCls,"GoogToken",str);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
   }

    Class ProjUtil;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String str;


        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();
            str = new JSONObject(data).toString();
        } else {
            str = "";
        }

        if (ProjUtil == null) {
            try {
                ProjUtil = GameContenxt.getClassLoader().loadClass(callCls);
                try {

                    Method method = ProjUtil.getDeclaredMethod("RunJs", String.class);
                    if (remoteMessage.getNotification() != null) {
                        String body = remoteMessage.getNotification().getBody();
                        String title = remoteMessage.getNotification().getTitle();
                        if (remoteMessage.getNotification().getBody() != null) {
                            method.invoke(null, "try {\n  JsbridgeUtil.PassNotification(    \"" + escapeJs(title) + "\",    \"" + escapeJs(body) + "\",    " + str + "  );\n} catch (error) {\n  console.log('JsbridgeUtil.PassNotification error', error);\n}");
                        }
                    }



                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onMessageReceived(remoteMessage);
        String title = remoteMessage.getNotification() != null ? remoteMessage.getNotification().getTitle() : getString(firebaseTitle);
        String message = remoteMessage.getNotification() != null ? remoteMessage.getNotification().getBody() : "";
        sendNotification(title, message);
    }
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

}
