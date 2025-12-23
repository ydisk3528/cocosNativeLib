package com.p8game.common.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebChromeClient;

import androidx.annotation.Nullable;

public class MyWebActivity extends  BaseActivity{
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

    @Override
    public void startActivity(Intent intent) {
        isWeb=true;
        super.startActivity(intent);
    }
}
