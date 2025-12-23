package com.p8.common.tools;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.p8.common.main.BaseActivity.baseActivity;
import static com.p8.common.tools.LogTools.LogPrint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.p8.common.R;
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
import com.unity3d.mediation.interstitial.LevelPlayInterstitialAd;
import com.unity3d.mediation.interstitial.LevelPlayInterstitialAdListener;

public class LevelPlayAdsManager {
    public static CAS extendInitCallback;
    public static String insertId = "";
    public static String bannerid = "";
    public static  float bannerX=0f;
    public static  float vis=0.01f;
    public static  float bannerY=0.6f;
    public static @Nullable LevelPlayInterstitialAd curLevelPlayInterstitialAd = null;
    public static Activity nowAct;
    // ====== ① SDK 初始化 ======
    public static void init(
            Activity activity,
            String adid,
            String insertIdMY,
            String banneridMY,
            @Nullable AdsInitCallbacks.SdkInitCallback sdkCb
    ) {
        insertId = insertIdMY;
        nowAct=activity;
        bannerid = banneridMY;

        activity.getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override public void onActivityCreated(@NonNull Activity a, @Nullable Bundle b) {}
            @Override public void onActivityStarted(@NonNull Activity a) {}
            @Override public void onActivityResumed(@NonNull Activity a) { }
            @Override public void onActivityPaused(@NonNull Activity a) {  }
            @Override public void onActivityStopped(@NonNull Activity a) {}
            @Override public void onActivitySaveInstanceState(@NonNull Activity a, @NonNull Bundle b) {}
            @Override public void onActivityDestroyed(@NonNull Activity a) {}
        });

        // 多广告单元（保持与你原先一致）

        LevelPlayInitRequest initRequest = new LevelPlayInitRequest
                .Builder(adid)
                .build();

        LevelPlayInitListener initListener = new LevelPlayInitListener() {
            @Override
            public void onInitFailed(@NonNull LevelPlayInitError error) {
                LogPrint("LevelPlay", "onInitFailed: " + error.getErrorMessage());
                if (sdkCb != null) {
                    activity.runOnUiThread(() ->
                            sdkCb.onFail(error.getErrorMessage(), null)
                    );
                }
            }

            @Override
            public void onInitSuccess(LevelPlayConfiguration configuration) {
                LogPrint("LevelPlay", "onInitSuccess");
                if (sdkCb != null) {
                    activity.runOnUiThread(() ->
                            sdkCb.onSuccess(configuration)
                    );
                }
            }
        };

//        // test suite 元数据分支（保持兼容你原逻辑）
//        try {
//            ApplicationInfo appInfo = activity.getPackageManager()
//                    .getApplicationInfo(activity.getPackageName(), PackageManager.GET_META_DATA);
//            if (appInfo.metaData != null) {
//                String metaValue = appInfo.metaData.getString("is_test_suite");
//                Log.d("MetaData", "Value: " + metaValue);
//                if (metaValue != null) {
//                    IronSource.set("is_test_suite", "enable");
//                    IronSource.launchTestSuite(activity);
//                    IronSource.init(activity, adid, new InitializationListener() {
//                        @Override
//                        public void onInitializationComplete() {
//                            IntegrationHelper.validateIntegration(activity);
//                            IronSource.launchTestSuite(activity);
//                            if (sdkCb != null) {
//                                activity.runOnUiThread(() ->
//                                        sdkCb.onSuccess(null) // 这里无 LevelPlayConfiguration，可传 null
//                                );
//                            }
//                        }
//                    });
//                    return;
//                }
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }

        try {
            LevelPlay.init(activity, initRequest, initListener);
        } catch (Exception e) {
            LogPrint("LevelPlay", "Exception: " + e.getLocalizedMessage());
            e.printStackTrace();
            if (sdkCb != null) {
                activity.runOnUiThread(() ->
                        sdkCb.onFail("Exception during LevelPlay.init()", e)
                );
            }
        }
    }

    // ====== ② 插屏初始化（独立回调）======
    public static void initInterstitial(
            Activity activity,
            @Nullable AdsInitCallbacks.InterstitialInitCallback interCb
    ) {
        try {
            LevelPlayInterstitialAd mInterstitialAd = new LevelPlayInterstitialAd(insertId);
            curLevelPlayInterstitialAd = mInterstitialAd;

            mInterstitialAd.setListener(new LevelPlayInterstitialAdListener() {
                private boolean firstReadyEmitted = false;

                @Override
                public void onAdLoaded(LevelPlayAdInfo info) {
                    LogPrint("LevelPlay:Inter", "onAdLoaded");
                    // 首次 loaded 视为“初始化完成可用”
                    if (!firstReadyEmitted && interCb != null) {
                        firstReadyEmitted = true;
                        activity.runOnUiThread(interCb::onReady);
                    }
                }

                @Override
                public void onAdLoadFailed(LevelPlayAdError err) {
                    LogPrint("LevelPlay:Inter", "onAdLoadFailed: " + err.getErrorMessage());
                    if (interCb != null) {
                        activity.runOnUiThread(() ->
                                interCb.onFail(err.getErrorMessage(), null)
                        );
                    }
                }

                @Override
                public void onAdDisplayed(LevelPlayAdInfo info) {
                    if (interCb!=null){
                        interCb.onShowReady();
                    }
                    LogPrint("LevelPlay:Inter", "onAdDisplayed: " + info.getAdNetwork());
                }

                @Override
                public void onAdDisplayFailed(LevelPlayAdError err, LevelPlayAdInfo info) {
                    if (interCb!=null){
                        interCb.onShowFail(err.getErrorMessage(),null);
                    }
                    LogPrint("LevelPlay:Inter", "onAdDisplayFailed: " + err.getErrorMessage());
                }

                @Override
                public void onAdClicked(LevelPlayAdInfo info) {
                    LogPrint("LevelPlay:Inter", "onAdClicked: " + info.getAdNetwork());
                    mInterstitialAd.loadAd(); // 维持下一次可用
                }

                @Override
                public void onAdClosed(LevelPlayAdInfo info) {
                    if (interCb!=null){
                        interCb.onShowEnd(info.getAdUnitName(),null);
                    }
                    LogPrint("LevelPlay:Inter", "onAdClosed: " + info.getAdNetwork());
                    // 这里是否自动 load 看你策略
                    mInterstitialAd.loadAd();
                }

                @Override
                public void onAdInfoChanged(LevelPlayAdInfo info) { /* optional */ }
            });

            mInterstitialAd.loadAd();
        } catch (Exception e) {
            e.printStackTrace();
            if (interCb != null) {
                activity.runOnUiThread(() ->
                        interCb.onFail("Exception during interstitial init", e)
                );
            }
        }
    }

    // ====== ③ Banner 初始化（独立回调）======
    public static void initBanner(
            Activity activity,
            @Nullable AdsInitCallbacks.BannerInitCallback bannerCb
    ) {

        LevelPlayAdSize adSize = LevelPlayAdSize.createAdaptiveAdSize(activity);
        LevelPlayBannerAdView.Config adConfig = new LevelPlayBannerAdView.Config.Builder()
                .setAdSize(adSize)
                .build();
        LevelPlayBannerAdView bannerView = new LevelPlayBannerAdView(activity, bannerid,adConfig);
        bannerView.setBannerListener(new LevelPlayBannerAdViewListener() {
            @Override
            public void onAdLoaded(@NonNull LevelPlayAdInfo adInfo) {
                LogPrint("LevelPlay:Banner", "onAdLoaded: " + adInfo.getAdUnitName());
                if (bannerCb != null) {
                    activity.runOnUiThread(() -> bannerCb.onReady(bannerView));
                }
            }

            @Override
            public void onAdLoadFailed(@NonNull LevelPlayAdError error) {
                LogPrint("LevelPlay:Banner", "onAdLoadFailed: " + error.getErrorMessage());
                if (bannerCb != null) {
                    activity.runOnUiThread(() ->
                            bannerCb.onFail(error.getErrorMessage(), null)
                    );
                }
            }

            @Override
            public void onAdDisplayed(@NonNull LevelPlayAdInfo adInfo) {
                LogPrint("LevelPlay:Banner", "onAdDisplayed: " + adInfo.getAdNetwork());
            }

            @Override
            public void onAdDisplayFailed(@NonNull LevelPlayAdInfo adInfo, @NonNull LevelPlayAdError error) {
                LogPrint("LevelPlay:Banner", "onAdDisplayFailed: " + error.getErrorMessage());
            }

            @Override
            public void onAdClicked(@NonNull LevelPlayAdInfo adInfo) {
                // 你原先是点击后 destroy，这里保持逻辑一致
                activity.runOnUiThread(bannerView::destroy);
            }

            @Override public void onAdExpanded(@NonNull LevelPlayAdInfo adInfo) {}
            @Override public void onAdCollapsed(@NonNull LevelPlayAdInfo adInfo) {}
            @Override public void onAdLeftApplication(@NonNull LevelPlayAdInfo adInfo) {}
        });

        try {
            bannerView.loadAd();
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    FrameLayout frameLayout = activity.findViewById(R.id.banner_frame_layout);
                    if (frameLayout!=null){//A面才有
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                frameLayout.addView(bannerView, new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));

                            }
                        });
                    }else{
                        activity.addContentView(bannerView, new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
                        View parent = (View) bannerView.getParent();
                        parent.post(() -> {
                            bannerView.setAlpha(vis);
                            int pw = parent.getWidth();
                            int ph = parent.getHeight();

                            float x = pw * bannerX;
                            float y = ph * bannerY;

                            bannerView.setX(x);
                            bannerView.setY(y);
                        });
                    }


                }
            });

        } catch (Throwable t) {
            if (bannerCb != null) {
                activity.runOnUiThread(() ->
                        bannerCb.onFail("Exception during banner load", t)
                );
            }
        }
    }
    public static boolean showvideotips = true;
    // ====== ④ 你原有的 showVideo（保留）======
    public static void showVideo() {
        if (curLevelPlayInterstitialAd != null) {
            baseActivity.runOnUiThread(() -> baseActivity.getGameWebView().onPause());



            if (showvideotips){
                //A面
                new AlertDialog.Builder(baseActivity)
                        .setTitle(baseActivity.getString(R.string.app_name))
                        .setCancelable(false)
                        .setMessage("Do you want to watch an ad? Watching the ad may take some time.")
                        .setPositiveButton("OK", (DialogInterface dialog, int which) -> {
                            baseActivity.runOnUiThread(() -> baseActivity.getGameWebView().onResume());

                            curLevelPlayInterstitialAd.showAd(baseActivity);
                        })
                        .setNegativeButton("CANCEL", (d, w) ->
                                baseActivity.runOnUiThread(() -> baseActivity.getGameWebView().onResume())
                        )
                        .setCancelable(false)
                        .show();
            }else{
                //B面
                curLevelPlayInterstitialAd.loadAd();
                curLevelPlayInterstitialAd.showAd(nowAct);
            }

        } else {
            baseActivity.runOnUiThread(() -> {
                baseActivity.getGameWebView().onResume();
                Toast.makeText(baseActivity, "ads not init", Toast.LENGTH_LONG).show();
            });
        }
    }
}
