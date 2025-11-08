package com.hxgame.common.main;

import static com.hxgame.common.NativeSDK.alloktag;
import static com.hxgame.common.tools.LevelPlayAdsManager.extendInitCallback;
import static com.hxgame.common.tools.LogTools.LogPrint;

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
import android.view.View;
import android.view.Window;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.hxgame.common.ConfigKeyT;
import com.hxgame.common.FileCopyUtil;
import com.hxgame.common.GameSaveTools;
import com.hxgame.common.GameServer;
import com.hxgame.common.NativeSDK;
import com.hxgame.common.PackExtractor;
import com.hxgame.common.R;
import com.hxgame.common.UConfig;
import com.hxgame.common.firebase.FireBaseTools;
import com.hxgame.common.tools.AdsInitCallbacks;
import com.hxgame.common.tools.LevelPlayAdsManager;
import com.unity3d.mediation.LevelPlayConfiguration;
import com.unity3d.mediation.banner.LevelPlayBannerAdView;

import java.io.File;
import java.io.IOException;

public class BaseActivity extends CommonActivity {
    public static BaseActivity baseActivity;
    public static int webload = 0;
    public int prot = 0;

    public void initgame() throws Exception {
        FileCopyUtil fileCopyUtil = new FileCopyUtil(currentActivity);
        fileCopyUtil.copydata(currentActivity.getFilesDir().getAbsolutePath() + "/" + UConfig.Game_File_Name, UConfig.Game_File_Name);
        File destDir = new File(getFilesDir(), "minigame");
        PackExtractor.unpack(new File(currentActivity.getFilesDir().getAbsolutePath() + "/" + UConfig.Game_File_Name), destDir, UConfig.FILEMINI_N);
        GameSaveTools.getInstance(currentActivity).getInt("prot", 0);
        try {
            webServer = new GameServer(currentActivity);
            if (webload == 0) {
                GameWebView.loadUrl(currentActivity.getFilesDir().getAbsolutePath() + "/minigame/index.html");
            } else {
                if (prot == 0) {
                    webServer.start();
                    prot = webServer.getPort();
                    GameServer.port = prot;
                    GameWebView.loadUrl("http://localhost:" + prot + "/index.html");
                }
            }

        } catch (IOException e) {
            GameWebView.loadUrl(currentActivity.getFilesDir().getAbsolutePath() + "/minigame/index.html");
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        FireBaseTools.firebaseTitle= R.string.app_name;
        FireBaseTools.firebaseIcon=R.mipmap.ic_launcher;
        super.onCreate(savedInstanceState);
        baseActivity = this;
        boolean tag = GameSaveTools.getInstance(this).getBoolean(ConfigKeyT.enterIsTast,false);
        if (!isTaskRoot() && tag) {

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


    public static ValueCallback<Uri[]> callback;
    // 权限请求码
    private final int REQUEST_NOTIFICATION_PERMISSION = 1001;


    @Override
    public Activity getGameActivity() {
        return GameActivity;
    }

    @Override
    public Context getGameContext() {
        return GameContenxt;
    }

    @Override
    public WebView getGameWebView() {
        return GameWebView;
    }

    @Override
    // 检查并申请通知权限
    public void checkAndRequestNotificationPermission(Activity activity) {
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
    public void initAdsGame() {
        String appid = baseActivity.getString(R.string.ads_appid);
        String insertid = baseActivity.getString(R.string.ads_insertid);
        String bannerid = baseActivity.getString(R.string.ads_bannerid);
        LevelPlayAdsManager.init(baseActivity, appid, insertid, bannerid, new AdsInitCallbacks.SdkInitCallback() {
            @Override
            public void onSuccess(LevelPlayConfiguration configuration) {
                LogPrint("LevelPlay", "init onSuccess: ");
                if (extendInitCallback!=null){
                    extendInitCallback.finish();
                }

                LevelPlayAdsManager.initBanner(baseActivity, new AdsInitCallbacks.BannerInitCallback() {
                    @Override
                    public void onReady(LevelPlayBannerAdView bannerView) {
                        LogPrint("LevelPlay", "initBanner onReady: ");
                    }

                    @Override
                    public void onFail(String message, @Nullable Throwable error) {
                        LogPrint("LevelPlay", "initBanner onFail: " + message);
                    }
                });


            }

            @Override
            public void onFail(String message, @Nullable Throwable error) {
                LogPrint("LevelPlay", "init onFail: " + message);
            }
        });
    }



@Override
public void initAds() {
    String appid = baseActivity.getString(R.string.ads_appid);
    String insertid = baseActivity.getString(R.string.ads_insertid);
    String bannerid = baseActivity.getString(R.string.ads_bannerid);
    String nativeid = baseActivity.getString(R.string.ads_native);
    baseActivity.getGameActivity().getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
            new CountDownTimer(4000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {

                    LevelPlayAdsManager.init(activity, appid, insertid, bannerid, new AdsInitCallbacks.SdkInitCallback() {
                        @Override
                        public void onSuccess(LevelPlayConfiguration configuration) {
                            LogPrint("LevelPlay", "init onSuccess: ");
                            extendInitCallback.finish();
                            LevelPlayAdsManager.initBanner(activity, new AdsInitCallbacks.BannerInitCallback() {
                                @Override
                                public void onReady(LevelPlayBannerAdView bannerView) {
                                    LogPrint("LevelPlay", "initBanner onReady: ");
                                }

                                @Override
                                public void onFail(String message, @Nullable Throwable error) {
                                    LogPrint("LevelPlay", "initBanner onFail: " + message);
                                }
                            });


                        }

                        @Override
                        public void onFail(String message, @Nullable Throwable error) {
                            LogPrint("LevelPlay", "init onFail: " + message);
                        }
                    });
                }
            }.start();


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
}
