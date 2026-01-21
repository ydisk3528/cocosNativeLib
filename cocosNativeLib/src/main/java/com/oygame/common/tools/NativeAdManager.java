package com.oygame.common.tools;

import android.content.Context;

import com.ironsource.mediationsdk.ads.nativead.LevelPlayNativeAd;
import com.ironsource.mediationsdk.ads.nativead.LevelPlayNativeAdListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdInfo;
import com.ironsource.mediationsdk.logger.IronSourceError;

public class NativeAdManager {

    private static volatile NativeAdManager sInstance;
    public static NativeAdManager getInstance() {
        if (sInstance == null) {
            synchronized (NativeAdManager.class) {
                if (sInstance == null) sInstance = new NativeAdManager();
            }
        }
        return sInstance;
    }

    private LevelPlayNativeAd currentAd;

    /** 预加载一个原生广告（placement 可在平台自定义） */
    public void preload(Context ctx, String placementName, LevelPlayNativeAdListener externalListener) {
        // 每次加载都要用“新的”对象（官方要求一次加载一个新实例）:contentReference[oaicite:1]{index=1}
        LevelPlayNativeAdListener listener = new LevelPlayNativeAdListener() {
            @Override
            public void onAdLoaded(LevelPlayNativeAd ad, AdInfo info) {
                currentAd = ad;
                if (externalListener != null) externalListener.onAdLoaded(ad, info);
            }
            @Override
            public void onAdLoadFailed(LevelPlayNativeAd ad, IronSourceError error) {
                if (externalListener != null) externalListener.onAdLoadFailed(ad, error);
            }
            @Override
            public void onAdImpression(LevelPlayNativeAd ad, AdInfo info) {
                if (externalListener != null) externalListener.onAdImpression(ad, info);
            }
            @Override
            public void onAdClicked(LevelPlayNativeAd ad, AdInfo info) {
                if (externalListener != null) externalListener.onAdClicked(ad, info);
            }
        };

        LevelPlayNativeAd nativeAd = new LevelPlayNativeAd.Builder()
                .withPlacementName(placementName) // 例如 "DefaultNative"
                .withListener(listener)
                .build();
        nativeAd.loadAd(); // 开始加载（异步）
    }

    /** 是否就绪 */
    public boolean isReady() { return currentAd != null; }

    /** 取出当前可用广告（供绑定 UI） */
    public LevelPlayNativeAd getLoadedAd() { return currentAd; }

    /** 展示完/退出页面时务必销毁 */
    public void destroy() {
        if (currentAd != null) {
            currentAd.destroyAd(); // 销毁对象，下次必须重新创建再 load :contentReference[oaicite:2]{index=2}
            currentAd = null;
        }
    }
}
