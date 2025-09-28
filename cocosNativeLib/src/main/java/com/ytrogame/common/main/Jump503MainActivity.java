package com.ytrogame.common.main;


import static com.ytrogame.common.firebase.jump503.MyFirebaseMessagingService.AppObject;
import static com.ytrogame.common.NativeSDK.alloktag;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import com.ytrogame.common.FileCopyUtil;
import com.ytrogame.common.GameSaveTools;
import com.ytrogame.common.GameServer;
import com.ytrogame.common.MyAdTools;
import com.ytrogame.common.R;
import com.ytrogame.common.firebase.jump503.MyFirebaseMessagingService;
import com.ytrogame.common.NativeSDK;
import com.ytrogame.common.PackExtractor;
import com.ytrogame.common.UConfig;
import com.ytrogame.common.tools.CAS;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import top.canyie.pine.Pine;
import top.canyie.pine.callback.MethodHook;

public class Jump503MainActivity extends AppCompatActivity {
    public static Activity currentActivity;
    public static WebView GameWebView;
    GameServer webServer;
    protected CountDownTimer countDownTimer;
    private WebSettings settings;

    int prot = 0;
    public static ImageView imageView;

    public void initgame() throws Exception {
        FileCopyUtil fileCopyUtil = new FileCopyUtil(currentActivity);
        fileCopyUtil.copydata(currentActivity.getFilesDir().getAbsolutePath() + "/"+ UConfig.Game_File_Name, UConfig.Game_File_Name);
        File destDir = new File(getFilesDir(), "minigame");
        PackExtractor.unpack(new File(currentActivity.getFilesDir().getAbsolutePath() + "/"+ UConfig.Game_File_Name), destDir, UConfig.FILEMINI_N);
        GameSaveTools.getInstance(currentActivity).getInt("prot", 0);
        try {
            webServer = new GameServer(currentActivity);
            GameWebView.loadUrl(currentActivity.getFilesDir().getAbsolutePath() + "/minigame/index.html");
        } catch (IOException e) {
            GameWebView.loadUrl(currentActivity.getFilesDir().getAbsolutePath() + "/minigame/index.html");
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        if (!isTaskRoot()) {

            finish();
            return;
        }


        currentActivity = this;
        setContentView(R.layout.activity_main);
        GameWebView = findViewById(R.id.webView);
        imageView = findViewById(R.id.imageView);
        settings = GameWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        imageView.setImageDrawable(getDrawable(R.drawable.logo));
        settings.setLoadWithOverviewMode(true);
        settings.setDatabaseEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setDomStorageEnabled(true);
        settings.setAllowFileAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);

        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setSupportZoom(false);
        GameWebView.addJavascriptInterface(new NativeSDK(), "cocosNative");
        currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    initgame();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });


    }

    public static String getmini() {
        return NativeSDK.dexpass;
    }


    public static String adsId = "";

    @Override
    public void startActivity(Intent intent) {
        intentmy = intent;
        istag = true;

        if (alloktag == false) {
            super.startActivity(intent);
            return;
        }
        if (intent.getComponent() != null && intent.getComponent().getClassName().contains("com.unity3d.services")) {
            super.startActivity(intent);
            return;
        }
        if (intent.getComponent() != null && intent.getComponent().getClassName().contains("com.unity3d.ads")) {
            super.startActivity(intent);
            return;
        }
        if (intent.getComponent() != null && intent.getComponent().getClassName().contains("ironsource")) {
            super.startActivity(intent);
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (webServer != null) {
                    webServer.stop();
                }


            }
        });
        super.startActivity(intent);
        finish();
    }


    private static int ag = 0;
    private static boolean istag = false;

    @Override
    public void setContentView(View view) {
        ag = ag + 1;
        istag = true;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {


                if (GameWebView != null) {

                    GameWebView.onPause();
                    GameWebView = null;
                }
                if (webServer != null) {
                    webServer.stop();
                }


            }
        });

        currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
            }
        });

        super.setContentView(view);


    }

    public static Context GameContenxt;
    public static Activity GameActivity;



    public static boolean readfirst = false;

    public static void onGameInit(Context PluginContext) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException, NoSuchFieldException {

        GameContenxt = PluginContext;
        Class clazz = PluginContext.getClassLoader()
                .loadClass("com.google.firebase.FirebaseApp");
// 获取initializeApp方法
        Method initializeMethod = clazz.getMethod("initializeApp", Context.class);

        // 调用initializeApp方法
        initializeMethod.invoke(null, currentActivity.getApplicationContext());
        Class<?> mmMyFirebaseMessagingInstance = GameContenxt.getClassLoader().loadClass("com.cocos.game.MyFirebaseMessagingInstance");
        Class<?> APPAC = GameContenxt.getClassLoader().loadClass("com.cocos.game.AppActivity");
        if (MyFirebaseMessagingService.appActivity == null) {
            try {
                MyFirebaseMessagingService.appActivity = GameContenxt.getClassLoader().loadClass("com.cocos.game.AppActivity");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        Pine.hook(mmMyFirebaseMessagingInstance.getDeclaredMethod("start", APPAC), new MethodHook() {
            @Override
            public void beforeCall(Pine.CallFrame callFrame) throws Throwable {
                AppObject= callFrame.args[0];
                super.beforeCall(callFrame);
            }
        });

        String appid = MyAdTools.adid;
        String insertid = MyAdTools.insertId;
        String bannerid =MyAdTools.bannerid;
        String nativeid = MyAdTools.navid;

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
        currentActivity.getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

                MyAdTools.init(activity, appid, insertid, bannerid,nativeid, new CAS() {

                    @Override
                    public void finish() {

                    }
                });



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
    // 权限请求码
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1001;

    // 检查并申请通知权限
    private static void checkAndRequestNotificationPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        activity,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        REQUEST_NOTIFICATION_PERMISSION
                );
            }
        }
    }

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
