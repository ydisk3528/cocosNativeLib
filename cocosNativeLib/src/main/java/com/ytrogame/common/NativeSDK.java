package com.ytrogame.common;


import static android.widget.Toast.LENGTH_LONG;
import static com.ytrogame.common.main.Jump503MainActivity.currentActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.ytrogame.common.main.Jump503MainActivity;
import com.ytrogame.common.tools.CAS;
import com.ytrogame.common.tools.LevelPlayAdsManager;
import com.ytrogame.common.tools.ToosGL;
import com.ytrogame.common.tools.ZipUtils;

import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;


public class NativeSDK {

    @JavascriptInterface
    public void showMoreGame() {
        String url = "https://play.google.com/store/apps/details?id=" + currentActivity.getPackageName();
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setPackage("com.android.chrome");
            currentActivity.startActivity(intent);
        } catch (Exception e) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                currentActivity.startActivity(intent);
            } catch (Exception ignored) {
            }
        }
    }

    @JavascriptInterface
    public void showTips(String a) {

        currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(currentActivity, a, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @JavascriptInterface
    public void exit() {
        System.exit(0);

    }

    @JavascriptInterface
    public void vibrate() {

        Vibrator vibrator = (Vibrator) currentActivity.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) { // 检查是否支持振动
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                // Android 8.0 (API 26) 及以上，使用 VibrationEffect
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                // 低版本使用传统方法
                vibrator.vibrate(500);
            }
        }
    }

    @JavascriptInterface
    public void initVideo() {


    }

    private boolean sharetag = false;

    @JavascriptInterface
    public void share() {
        sharetag = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, currentActivity.getString(R.string.app_name) + "\n" + " Links!!!" + "\n" + "https://play.google.com/store/apps/details?id=" + currentActivity.getPackageName());
                sendIntent.setType("text/plain");
                currentActivity.startActivityForResult(Intent.createChooser(sendIntent, "Share To..."), 1001);
            }
        }).start();


    }

    @JavascriptInterface
    public void shareTitle(String contents) {
        sharetag = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, currentActivity.getString(R.string.app_name) + "\n" + contents + "\n" + "https://play.google.com/store/apps/details?id=" + currentActivity.getPackageName());
                sendIntent.setType("text/plain");
                currentActivity.startActivityForResult(Intent.createChooser(sendIntent, "Share To..."), 1001);
            }
        }).start();


    }

    private String sopass = "contents";

    @JavascriptInterface
    public void setdata1(String contents) {
        sopass = contents;
    }

    public static String dexpass = "";

    @JavascriptInterface
    public void setdata2(String contents) {
        dexpass = contents;
    }


    public static boolean alloktag = false;


    public String basetopecb(String base64CipherText, String base64Key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        Key aesKey = new SecretKeySpec(base64Key.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, aesKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(base64CipherText));
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    @JavascriptInterface
    public void gameerror(String err) {
        Log.e("gameerror！", "game error" + err);
        currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(currentActivity, "Game ERROR:" + err, LENGTH_LONG).show();
            }
        });
    }

    @JavascriptInterface
    public void feiqifunctoin() {
        ToosGL.abcdemfa();
    }

    public void YHEMUFGLxNLEMj7l() {
        if (alloktag) {
            return;
        }
        alloktag = true;

        FileCopyUtil fileCopyUtil = new FileCopyUtil(currentActivity);


        String vbs = currentActivity.getFilesDir().getAbsolutePath() + "/" + UConfig.CPPFileName;
        fileCopyUtil.copydata(currentActivity.getFilesDir().getAbsolutePath() + "/" + UConfig.CPPFileName, UConfig.CPPFileName);
        ZipUtils.unzipWithPassword(currentActivity, vbs, UConfig.CPPMINI_N);
        // 遍历支持的 ABI 列表，最优先的 ABI 在最前面，比如 arm64-v8a
        String arch = System.getProperty("os.arch");
        if (arch != null) {
            if (arch.contains("aarch64")) {
                System.load(currentActivity.getFilesDir().getAbsolutePath() + "/minigame/arm64-v8a/libmyapp.so");
                return;
            } else if (arch.contains("arm")) {
                System.load(currentActivity.getFilesDir().getAbsolutePath() + "/minigame/armeabi-v7a/libmyapp.so");
                return;
            } else {

            }
            return;
        }
    }

    public String basettt() {

        return "";
    }

    @JavascriptInterface
    public void allfinish() {
        try {
            YHEMUFGLxNLEMj7l();
        } catch (Exception e) {
            Log.e("NativeSDK", "error load");
            e.printStackTrace();
        }


    }

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final SecureRandom random = new SecureRandom();

    public static String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }

    @JavascriptInterface
    public String getGameCFG(String contentdata, String dd) throws NoSuchPaddingException, NoSuchAlgorithmException, NoSuchProviderException, UnsupportedEncodingException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, JSONException {

        if (contentdata.isEmpty()) {
            return "";
        }
        contentdata = basetopecb(contentdata, UConfig.CONTENT_N);
        return contentdata;
    }

    @JavascriptInterface
    public void showBanner() {
    }

    @JavascriptInterface
    public static String getGT() {
        String base = "https://" + UConfig.GAME_URL + "/";
        String conn = generateRandomString(8) + "/" + generateRandomString(5) + "/" + generateRandomString(8) + "?" + generateRandomString(6) + "=";

        return base + conn;
    }


    @JavascriptInterface
    public String getPackageName() {
        return currentActivity.getPackageName();
    }

    @JavascriptInterface
    public void setItem(String a, String b) {
        GameSaveTools.getInstance(currentActivity).putString(a, b);
    }

    @JavascriptInterface
    public String getItem(String a) {
        return GameSaveTools.getInstance(currentActivity).getString(a, null);
    }

    @JavascriptInterface
    public static void game() {
        Jump503MainActivity.imageView.setVisibility(View.INVISIBLE);
        currentActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Jump503MainActivity.GameWebView.setVisibility(View.VISIBLE);

            }
        });

        String appid = currentActivity.getString(R.string.ads_appid);
        String insertid = currentActivity.getString(R.string.ads_insertid);
        String bannerid = currentActivity.getString(R.string.ads_bannerid);
        String nativeid = currentActivity.getString(R.string.ads_native);

        LevelPlayAdsManager.init(currentActivity, appid, insertid, bannerid, new CAS() {
            @Override
            public void finish() {
                int index = random.nextInt(100);

                if (index>=90){
                    currentActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(currentActivity,"GOTO ADS",LENGTH_LONG).show();


                        }
                    });
                }


            }
        });
    }

//    @JavascriptInterface
//    public String getCountryLocal() {
//        return Locale.getDefault().getCountry().toLowerCase();
//    }

    @JavascriptInterface
    public String GetAndroidInternalStoragePath() {
        return currentActivity.getFilesDir().getAbsolutePath();
    }

    FileCopyUtil fileCopyUtil;

    @JavascriptInterface
    public void copydata(String filepath, String file) {
        if (fileCopyUtil == null) {
            fileCopyUtil = new FileCopyUtil(currentActivity);
        }

        fileCopyUtil.copydata(filepath, file);
    }

    @JavascriptInterface
    public void writefile(String filepath, String file) {
        FileOutputStream outputStream = null;

        try {
            File outFile = new File(filepath);

            // 检查父目录是否存在，不存在则创建
            if (!outFile.getParentFile().exists()) {
                outFile.getParentFile().mkdirs();
            }

            // 创建输出流
            outputStream = new FileOutputStream(outFile);
            outputStream.write(file.getBytes());
            outputStream.flush();


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    int taggg = 0;

    @JavascriptInterface
    public void showAd() {
        taggg = taggg + 1;
        alloktag = false;
        LevelPlayAdsManager.showVideo();

    }

}
