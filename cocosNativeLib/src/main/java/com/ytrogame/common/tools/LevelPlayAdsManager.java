package com.ytrogame.common.tools;//
//

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import static com.ytrogame.common.main.BaseActivity.baseActivity;
import static com.ytrogame.common.tools.LogTools.LogPrint;

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
import com.ytrogame.common.R;
import com.ytrogame.common.main.Jump503MainActivity;

import java.util.Arrays;
import java.util.List;

public class LevelPlayAdsManager {
    public static String insertId = "";
    public static String bannerid = "";

    public static void init(Activity activity, String adid, String insertIdMY, String banneridMY, CAS CAS) {

        insertId = insertIdMY;
        bannerid = banneridMY;
        activity.getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                IronSource.onResume(activity);
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
                IronSource.onPause(activity);
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
        List<LevelPlay.AdFormat> legacyAdFormats = Arrays.asList(LevelPlay.AdFormat.INTERSTITIAL);

        LevelPlayInitRequest initRequest = new LevelPlayInitRequest.Builder(adid)
                .withLegacyAdFormats(legacyAdFormats)
                .withUserId(IronSource.getAdvertiserId(activity))
                .build();
        LevelPlayInitListener initListener = new LevelPlayInitListener() {
            @Override
            public void onInitFailed(@NonNull LevelPlayInitError error) {
                //Recommended to initialize again
                LogPrint("LevelPlay:", "onInitFailed:" + error.getErrorMessage());
                CAS.finish();
            }

            @Override
            public void onInitSuccess(LevelPlayConfiguration configuration) {
                //Create ad objects and load ads
                LogPrint("LevelPlay:", "onInitSuccess");
//                loadBanner(baseActivity);
                initTypeByType(activity, CAS);
            }
        };

        try {
            ApplicationInfo appInfo = activity.getPackageManager().getApplicationInfo(activity.getPackageName(), PackageManager.GET_META_DATA);
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
            LogPrint("LevelPlay", "Exception:" + e.getLocalizedMessage());
            e.printStackTrace();
            CAS.finish();
        }


    }

    public static void initTypeByType(Activity activity, CAS CAS) {

        initInsert(activity, CAS);
        loadBanner(activity);

    }

    public static void loadBanner(Activity activity) {
// Create the banner view and set the ad unit id

        LevelPlayBannerAdView levelPlayBanner = new LevelPlayBannerAdView(activity, bannerid);
// Create the adaptive ad size to support both adaptive, banner and leaderboard (recommended)
        LevelPlayAdSize adSize = LevelPlayAdSize.createAdaptiveAdSize(activity);

        // Required when using createAdaptiveAdSize()
        if (adSize != null) {
            levelPlayBanner.setAdSize(adSize);
        } else {
            levelPlayBanner.setAdSize(LevelPlayAdSize.MEDIUM_RECTANGLE);
        }
        // 设置 FrameLayout 的宽高
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                200, // 宽度 px，可用 LayoutParams.WRAP_CONTENT
                100  // 高度
        );

// 设置对齐到顶部 + 水平居中
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);

// 应用布局参数
        levelPlayBanner.setLayoutParams(params);
        levelPlayBanner.setBannerListener(new LevelPlayBannerAdViewListener() {
            @Override
            public void onAdLoaded(@NonNull LevelPlayAdInfo adInfo) {
                // Ad was loaded successfully
                LogPrint("LevelPlay", " loadBanner onAdLoaded:" + adInfo.getAdUnitName());
            }

            @Override
            public void onAdLoadFailed(@NonNull LevelPlayAdError error) {
                // Ad load failed
                LogPrint("LevelPlay", " loadBanner onAdLoadFailed:" + error.getErrorMessage());
            }

            @Override
            public void onAdDisplayed(@NonNull LevelPlayAdInfo adInfo) {
                // Ad was displayed and visible on screen
                LogPrint("LevelPlay", " loadBanner onAdDisplayed:" + adInfo.getAdNetwork());

                if (levelPlayBanner!=null){

                }
            }

            @Override
            public void onAdDisplayFailed(@NonNull LevelPlayAdInfo adInfo, @NonNull LevelPlayAdError error) {
                // Optional. Ad failed to be displayed on screen
                LogPrint("LevelPlay", " loadBanner onAdDisplayFailed,error code" + error.getErrorMessage());
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
        levelPlayBanner.loadAd();
        FrameLayout frameLayout = activity.findViewById(R.id.banner_frame_layout);
        if (frameLayout==null){
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activity.addContentView(levelPlayBanner, new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));

                }
            });
            return;
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                frameLayout.addView(levelPlayBanner, new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));

            }
        });



    }

    public static LevelPlayInterstitialAd curLevelPlayInterstitialAd = null;

    public static void showVideo() {

        if (curLevelPlayInterstitialAd != null) {
           baseActivity.runOnUiThread(new Runnable() {
               @Override
               public void run() {
                   baseActivity.getGameWebView().onPause();
               }
           });
            new AlertDialog.Builder(baseActivity)
                    .setTitle(baseActivity.getString(R.string.app_name)) // App name as title
                    .setCancelable(false)
                    .setMessage("Do you want to watch an ad? Watching the ad may take some time.")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            baseActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    baseActivity.getGameWebView().onResume();
                                }
                            });
                            curLevelPlayInterstitialAd.loadAd();
                            curLevelPlayInterstitialAd.showAd(baseActivity);
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            baseActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    baseActivity.getGameWebView().onResume();
                                }
                            });

                        }
                    })
                    .setCancelable(false) // 不允许点击外部关闭
                    .show();


        } else {
            baseActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    baseActivity.getGameWebView().onResume();
                    Toast.makeText(baseActivity, "ads not init", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public static void initInsert(Activity activity, CAS CAS) {
        // Create the interstitial ad object
        try {
            LevelPlayInterstitialAd mInterstitialAd = new LevelPlayInterstitialAd(insertId);
            curLevelPlayInterstitialAd = mInterstitialAd;
            mInterstitialAd.setListener(new LevelPlayInterstitialAdListener() {
                @Override
                public void onAdLoaded(LevelPlayAdInfo levelPlayAdInfo) {
                    // Ad was loaded successfully
                    LogPrint("LevelPlay:", "onAdLoaded");
//                    mInterstitialAd.showAd(activity);
                }

                @Override
                public void onAdLoadFailed(LevelPlayAdError levelPlayAdError) {
                    // Ad load failed
                    LogPrint("LevelPlay:", "onAdLoadFailed:" + levelPlayAdError.getErrorMessage() + "..." + levelPlayAdError.getAdUnitId());
                    CAS.finish();

                }

                @Override
                public void onAdDisplayed(LevelPlayAdInfo levelPlayAdInfo) {
                    // Ad was displayed and visible on screen
                    LogPrint("LevelPlay:", "onAdDisplayed:" + levelPlayAdInfo.getAdNetwork());

                }

                @Override
                public void onAdDisplayFailed(LevelPlayAdError levelPlayAdError, LevelPlayAdInfo levelPlayAdInfo) {
                    // Ad fails to be displayed
                    LogPrint("LevelPlay:", "onAdDisplayFailed:" + levelPlayAdError.getErrorMessage());
                    CAS.finish();
                    // Optional
                }

                @Override
                public void onAdClicked(LevelPlayAdInfo levelPlayAdInfo) {
                    // Ad was clicked
                    mInterstitialAd.loadAd();
                    LogPrint("LevelPlay:", "onAdClicked:" + levelPlayAdInfo.getAdNetwork());
                    // Optional
                }

                @Override
                public void onAdClosed(LevelPlayAdInfo levelPlayAdInfo) {
                    // Ad was closed
                    // Optional
                    LogPrint("LevelPlay:", "onAdClosed:" + levelPlayAdInfo.getAdNetwork());
                    CAS.finish();
                }

                @Override
                public void onAdInfoChanged(LevelPlayAdInfo levelPlayAdInfo) {
                    // Called after the ad info is updated. Available when another interstitial ad has loaded, and includes a higher CPM/Rate
                    // Optional
                }
            });
            mInterstitialAd.loadAd();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
