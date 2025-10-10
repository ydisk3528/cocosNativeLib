package com.fugame.common.tools;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static com.fugame.common.main.BaseActivity.baseActivity;
import static com.fugame.common.tools.LogTools.LogPrint;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fugame.common.R;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.integration.IntegrationHelper;
import com.ironsource.mediationsdk.sdk.InitializationListener;
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

import java.util.Arrays;
import java.util.List;

public class LevelPlayAdsManager {
    public static CAS extendInitCallback;
    public static String insertId = "";
    public static String bannerid = "";

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
            @Override public void onActivityResumed(@NonNull Activity a) { IronSource.onResume(a); }
            @Override public void onActivityPaused(@NonNull Activity a) { IronSource.onPause(a); }
            @Override public void onActivityStopped(@NonNull Activity a) {}
            @Override public void onActivitySaveInstanceState(@NonNull Activity a, @NonNull Bundle b) {}
            @Override public void onActivityDestroyed(@NonNull Activity a) {}
        });

        // 多广告单元（保持与你原先一致）
        List<LevelPlay.AdFormat> legacyAdFormats = Arrays.asList(LevelPlay.AdFormat.INTERSTITIAL);

        LevelPlayInitRequest initRequest = new LevelPlayInitRequest
                .Builder(adid)
                .withLegacyAdFormats(legacyAdFormats)
                .withUserId(IronSource.getAdvertiserId(activity))
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

        // test suite 元数据分支（保持兼容你原逻辑）
        try {
            ApplicationInfo appInfo = activity.getPackageManager()
                    .getApplicationInfo(activity.getPackageName(), PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                String metaValue = appInfo.metaData.getString("is_test_suite");
                Log.d("MetaData", "Value: " + metaValue);
                if (metaValue != null) {
                    IronSource.setMetaData("is_test_suite", "enable");
                    IronSource.launchTestSuite(activity);
                    IronSource.init(activity, adid, new InitializationListener() {
                        @Override
                        public void onInitializationComplete() {
                            IntegrationHelper.validateIntegration(activity);
                            IronSource.launchTestSuite(activity);
                            if (sdkCb != null) {
                                activity.runOnUiThread(() ->
                                        sdkCb.onSuccess(null) // 这里无 LevelPlayConfiguration，可传 null
                                );
                            }
                        }
                    });
                    return;
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

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
        LevelPlayBannerAdView bannerView = new LevelPlayBannerAdView(activity, bannerid);

        LevelPlayAdSize adSize = LevelPlayAdSize.createAdaptiveAdSize(activity);
        if (adSize != null) {
            bannerView.setAdSize(adSize);
        } else {
            bannerView.setAdSize(LevelPlayAdSize.MEDIUM_RECTANGLE);
        }

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(200, 100);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        bannerView.setLayoutParams(params);

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
                            curLevelPlayInterstitialAd.loadAd();
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
