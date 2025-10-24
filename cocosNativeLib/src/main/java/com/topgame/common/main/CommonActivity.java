package com.topgame.common.main;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.topgame.common.GameServer;

public abstract class CommonActivity extends AppCompatActivity {
    protected  Activity currentActivity;
    protected  WebView GameWebView;
    protected GameServer webServer;
    protected CountDownTimer countDownTimer;
    protected WebSettings settings;
    int prot = 0;
    public  ImageView imageView;

    public static Context GameContenxt;
    public  Activity GameActivity;
    public Intent intentmy;
    public  boolean readfirst = false;
    public abstract  void initgame() throws Exception;

    public abstract Activity getGameActivity();
    public abstract  void initAds();
    public abstract Context getGameContext();
    public abstract WebView getGameWebView();
    public abstract  void checkAndRequestNotificationPermission(Activity activity);
}
