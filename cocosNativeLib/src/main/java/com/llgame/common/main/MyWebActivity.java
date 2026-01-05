package com.llgame.common.main;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.AdjustEvent;
import com.appsflyer.AppsFlyerConversionListener;
import com.appsflyer.AppsFlyerLib;
import com.appsflyer.attribution.AppsFlyerRequestListener;

import java.util.Map;

public class MyWebActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    protected void initAf(Activity activity, String string) {

        AppsFlyerLib.getInstance().init(string, new AppsFlyerConversionListener() {
            @Override
            public void onConversionDataSuccess(Map<String, Object> map) {
                Log.e("AppsFlyerLib", "onConversionDataSuccess===" + map);
            }

            @Override
            public void onConversionDataFail(String s) {
                Log.e("AppsFlyerLib", "onConversionDataFail===" + s);
            }

            @Override
            public void onAppOpenAttribution(Map<String, String> map) {
                Log.e("AppsFlyerLib", "onAppOpenAttribution===" + map);
            }

            @Override
            public void onAttributionFailure(String s) {
                Log.e("AppsFlyerLib", "onAttributionFailure===" + s);
            }
        }, activity);

        AppsFlyerLib.getInstance().start(activity, string, new AppsFlyerRequestListener() {
            @Override
            public void onSuccess() {
                Log.e("AppsFlyerLib", "onSuccess");
            }

            @Override
            public void onError(int i, @NonNull String s) {
                Log.e("AppsFlyerLib", "onError: code===" + i + "; msg===" + s);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(activity, "AppsFlyerLib_onError:" + s, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }
    protected boolean tagas=false;
    @Override
    public void setContentView(View view) {

        if (tagas){
            super.setContentView(view);
            return;
        }
        tagas=true;


        FrameLayout rootLayout = new FrameLayout(currentActivity);

        rootLayout.addView(view, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        currentActivity.setContentView(rootLayout);


        ViewCompat.setOnApplyWindowInsetsListener(rootLayout, (v, insets) -> {
            Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sys.left, sys.top, sys.right, 0);
            return insets;
        });
        ViewCompat.requestApplyInsets(rootLayout);
    }

    protected void afevent(String event) {
        AppsFlyerLib.getInstance().logEvent(currentActivity, event, null);
    }

    protected void initAdjust(String key) {
        AdjustConfig config = new AdjustConfig(currentActivity, key, AdjustConfig.ENVIRONMENT_PRODUCTION);
        config.setOnAttributionChangedListener(adjustAttribution -> Log.e("Adjust:", adjustAttribution.toString()));
        config.setOnEventTrackingSucceededListener(adjustEventSuccess -> Log.e("Adjust:", "adjustEventSuccess:" + adjustEventSuccess.eventToken));

        config.setOnEventTrackingFailedListener(adjustEventFailure -> Log.e("Adjust:", adjustEventFailure.toString()));

        Adjust.initSdk(config);
        getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                Adjust.onResume();
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                Adjust.onPause();
            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {

            }
        });
    }

    protected void qz(String ev) {
        AdjustEvent adjustEvent = new AdjustEvent(ev);
        Adjust.trackEvent(adjustEvent);
    }

    @Override
    public void startActivity(Intent intent) {
        isWeb = true;
        super.startActivity(intent);
    }
}
