package com.oogame.common.tools;

import androidx.annotation.MainThread;
import androidx.annotation.Nullable;
import com.unity3d.mediation.LevelPlayConfiguration;
import com.unity3d.mediation.banner.LevelPlayBannerAdView;

public final class AdsInitCallbacks {

    /** SDK 初始化回调 */
    public interface SdkInitCallback {
        @MainThread
        void onSuccess(LevelPlayConfiguration configuration);

        @MainThread
        void onFail(String message, @Nullable Throwable error);
    }

    /** 插屏初始化回调（可在 onAdLoaded 首次触发时回调 ready） */
    public interface InterstitialInitCallback {
        @MainThread
        void onReady(); // 已可用（已 load 完成）

        @MainThread
        void onFail(String message, @Nullable Throwable error);

        @MainThread
        void onShowReady(); // 已可用（已 load 完成）

        @MainThread
        void onShowEnd(String message, @Nullable Throwable error);


        @MainThread
        void onShowFail(String message, @Nullable Throwable error);


    }


    /** Banner 初始化回调（回传已创建的 BannerView，便于外部控制添加/移除） */
    public interface BannerInitCallback {
        @MainThread
        void onReady(LevelPlayBannerAdView bannerView);

        @MainThread
        void onFail(String message, @Nullable Throwable error);
    }

    private AdsInitCallbacks() {}
}
