package com.llgame.common;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.app.Activity;
import android.app.Application;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ironsource.mediationsdk.ads.nativead.LevelPlayMediaView;
import com.ironsource.mediationsdk.ads.nativead.LevelPlayNativeAd;
import com.ironsource.mediationsdk.ads.nativead.LevelPlayNativeAdListener;
import com.ironsource.mediationsdk.ads.nativead.NativeAdLayout;
import com.ironsource.mediationsdk.ads.nativead.interfaces.NativeAdDataInterface;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.unity3d.mediation.LevelPlay;
import com.unity3d.mediation.LevelPlayAdError;
import com.unity3d.mediation.LevelPlayAdInfo;
import com.unity3d.mediation.LevelPlayAdSize;
import com.unity3d.mediation.LevelPlayConfiguration;
import com.unity3d.mediation.LevelPlayInitError;
import com.unity3d.mediation.LevelPlayInitListener;
import com.unity3d.mediation.LevelPlayInitRequest;
import com.unity3d.mediation.banner.LevelPlayBannerAdView;
import com.unity3d.mediation.banner.LevelPlayBannerAdViewListener;
import com.llgame.common.tools.CAS;
import com.llgame.common.tools.NativeAdManager;

import java.util.Arrays;
import java.util.List;

public class MyAdTools {
    public static String adid = "";
    public static String insertId = "";
    public static String bannerid = "";
    public static String navid = "";
    public static void init(Activity activity, String adid, String insertIdMY, String banneridMY,String nativeid, CAS CAS) {

        insertId = insertIdMY;
        bannerid = banneridMY;
        navid=nativeid;
        activity.getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

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


        //开始初始化

        // Init the SDK when implementing the Multiple Ad Units Interstitial and Banner APIs, and Rewarded using legacy APIs
        List<LevelPlay.AdFormat> legacyAdFormats = Arrays.asList(LevelPlay.AdFormat.BANNER);

        LevelPlayInitRequest initRequest = new LevelPlayInitRequest.Builder(adid)
                .build();
        LevelPlayInitListener initListener = new LevelPlayInitListener() {
            @Override
            public void onInitFailed(@NonNull LevelPlayInitError error) {
                //Recommended to initialize again
                CAS.finish();
            }

            @Override
            public void onInitSuccess(LevelPlayConfiguration configuration) {
                //Create ad objects and load ads
                initbanner(activity);
                Log.e("LevelPlay","onInitSuccess:");
//                initnativeads(activity);
            }
        };


        try {
            LevelPlay.init(activity, initRequest, initListener);
        } catch (Exception e) {
            CAS.finish();
        }


    }
    public  static void bindAndShow(LevelPlayNativeAd ad,Activity activity) {
        // 1) inflate 模板
        LayoutInflater inflater = LayoutInflater.from(activity);
        // 1) 创建 NativeAdLayout 容器
        NativeAdLayout nativeAdLayout = new NativeAdLayout(activity);
        nativeAdLayout.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        // 2) 创建内部控件
        TextView titleView = new TextView(activity);
        titleView.setTextSize(16);
        titleView.setTypeface(Typeface.DEFAULT_BOLD);

        TextView bodyView = new TextView(activity);
        bodyView.setTextSize(14);

        ImageView iconView = new ImageView(activity);
        iconView.setLayoutParams(new LinearLayout.LayoutParams(100, 100));

        Button ctaView = new Button(activity);
        ctaView.setText("ok");

        LevelPlayMediaView mediaView = new LevelPlayMediaView(activity);
        mediaView.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                400   // 自定义高度，比如 400px
        ));

        ImageView privacyIcon = new ImageView(activity);
        FrameLayout.LayoutParams privacyParams =
                new FrameLayout.LayoutParams(50, 50, Gravity.END);
        privacyIcon.setLayoutParams(privacyParams);

        // 3) 把子控件加到容器里（随便排版，这里简单 LinearLayout）
        LinearLayout container = new LinearLayout(activity);
        container.setOrientation(LinearLayout.VERTICAL);
        container.addView(titleView);
        container.addView(bodyView);
        container.addView(iconView);
        container.addView(mediaView);
        container.addView(ctaView);
        container.addView(privacyIcon);

        nativeAdLayout.addView(container);

        // 4) 设置各视图对应关系
        nativeAdLayout.setTitleView(titleView);
        nativeAdLayout.setBodyView(bodyView);
        nativeAdLayout.setIconView(iconView);
        nativeAdLayout.setCallToActionView(ctaView);
        nativeAdLayout.setMediaView(mediaView);
        nativeAdLayout.setIconView(privacyIcon);
        // 5) 填充数据
        if (ad.getTitle() != null) titleView.setText(ad.getTitle());
        if (ad.getBody() != null) bodyView.setText(ad.getBody());
        if (ad.getCallToAction() != null) ctaView.setText(ad.getCallToAction());

        NativeAdDataInterface.Image icon = ad.getIcon();
        if (icon != null && icon.getDrawable() != null) {
            iconView.setImageDrawable(icon.getDrawable());
        }

        NativeAdDataInterface.Image privacy = ad.getIcon();
        if (privacy != null && privacy.getDrawable() != null) {
            privacyIcon.setImageDrawable(privacy.getDrawable());
        }

        // 6) 注册广告
        nativeAdLayout.registerNativeAdViews(ad);

        nativeAdLayout.setClickable(false);
//        nativeAdLayout.setAlpha(0.01f);
        View parent = (View) nativeAdLayout;
        parent.post(() -> {
            int pw = parent.getWidth();
            int ph = parent.getHeight();

            float x = pw * 0f;
            float y = ph * 2f;

            nativeAdLayout.setX(x);
            nativeAdLayout.setY(y);
        });
        // 5) 添加到你的容器中显示
        activity.addContentView(nativeAdLayout,new FrameLayout.LayoutParams(-1,-1));

    }


    private static NativeAdLayout nativeAdLayout;
    private static  ViewGroup adContainer;
    public static void initnativeads(Activity activity){
        // 预加载
        NativeAdManager.getInstance().preload(
                activity,
                "DefaultNative", // 你的 placement 名
                new LevelPlayNativeAdListener() {
                    @Override
                    public void onAdLoaded(LevelPlayNativeAd ad, AdInfo info) {
                        activity.runOnUiThread(() -> bindAndShow(ad,activity));
                    }
                    @Override public void onAdLoadFailed(LevelPlayNativeAd ad, IronSourceError error) {
                        Log.e("LevelPlay","onAdLoadFailed:"+error.getErrorMessage());
                    }
                    @Override public void onAdImpression(LevelPlayNativeAd ad, AdInfo info) { }
                    @Override public void onAdClicked(LevelPlayNativeAd ad, AdInfo info) {
                        Log.e("LevelPlay","onAdLoadFailed:"+info.getAdNetwork());
                        NativeAdManager.getInstance().destroy();
                    }
                });
    }
    public static void initbanner(Activity activity){
        LevelPlayAdSize adSize = LevelPlayAdSize.BANNER;
        LevelPlayBannerAdView.Config adConfig = new LevelPlayBannerAdView.Config.Builder()
                .setAdSize(adSize)
                .build();

        // Create the banner view and set the ad unit id
        LevelPlayBannerAdView levelPlayBanner = new LevelPlayBannerAdView(activity, bannerid, adConfig);

// 设置对齐到顶部 + 水平居中

// 应用布局参数
        levelPlayBanner.setBannerListener(new LevelPlayBannerAdViewListener() {
            @Override
            public void onAdLoaded(@NonNull LevelPlayAdInfo adInfo) {
                // Ad was loaded successfully

            }

            @Override
            public void onAdLoadFailed(@NonNull LevelPlayAdError error) {
                // Ad load failed

            }

            @Override
            public void onAdDisplayed(@NonNull LevelPlayAdInfo adInfo) {
                // Ad was displayed and visible on screen

                if (levelPlayBanner!=null){
            levelPlayBanner.setAlpha(0.01f);
                }


            }

            @Override
            public void onAdDisplayFailed(@NonNull LevelPlayAdInfo adInfo, @NonNull LevelPlayAdError error) {
                // Optional. Ad failed to be displayed on screen
            }

            @Override
            public void onAdClicked(@NonNull LevelPlayAdInfo adInfo) {
                // Ad was clicked
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        levelPlayBanner.destroy();
                    }
                });
            }

            @Override
            public void onAdExpanded(@NonNull LevelPlayAdInfo adInfo) {
                // Optional. Ad is opened on full screen
            }

            @Override
            public void onAdCollapsed(@NonNull LevelPlayAdInfo adInfo) {
                // Optional. Ad is restored to its original size
            }

            @Override
            public void onAdLeftApplication(@NonNull LevelPlayAdInfo adInfo) {

            }
            // Optional. User pressed on the ad and was navigated out of the app
        });
// Load the banner ad

        FrameLayout frameLayout = activity.findViewById(R.id.banner_frame_layout);
        if (frameLayout==null){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    activity.addContentView(levelPlayBanner, new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
                    View parent = (View) levelPlayBanner.getParent();
                    parent.post(() -> {
                        int pw = parent.getWidth();
                        int ph = parent.getHeight();

                        float x = pw * 0f;
                        float y = ph * 0.6f;

                        levelPlayBanner.setX(x);
                        levelPlayBanner.setY(y);
                    });
                }
            });
            levelPlayBanner.loadAd();
        }

    }

}
