package com.ttgame.common.tools;

import android.content.Context;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import static com.ttgame.common.main.BaseActivity.baseActivity;
public class ToosGL {

    private static final String TOKEN_FILENAME = "auth.token";

    // 时间格式 yyyyMMddHHmm，用于每分钟生成一次 key 和 iv
    private static String getTimeBase(long timestampMs) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm", Locale.US);
        return sdf.format(new Date(timestampMs));
    }

    private static SecretKeySpec generateKey(String base) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(base.getBytes(StandardCharsets.UTF_8));
        byte[] keyBytes = Arrays.copyOfRange(hash, 0, 16);
        return new SecretKeySpec(keyBytes, "AES");
    }

    private static IvParameterSpec generateIv(String base) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(("IV_" + base).getBytes(StandardCharsets.UTF_8));
        byte[] ivBytes = Arrays.copyOfRange(hash, 0, 16);
        return new IvParameterSpec(ivBytes);
    }

    // 主程序调用：写入 token 文件
    public static void abcdemfa() {
        try {
            Context context= baseActivity.getBaseContext();
            String timeBase = getTimeBase(System.currentTimeMillis());
            SecretKeySpec key = generateKey(timeBase);
            IvParameterSpec iv = generateIv(timeBase);

            JSONObject json = new JSONObject();
            json.put("pkg", context.getPackageName());
            json.put("ts", System.currentTimeMillis());

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            byte[] encrypted = cipher.doFinal(json.toString().getBytes(StandardCharsets.UTF_8));

            File tokenFile = new File(context.getFilesDir(), TOKEN_FILENAME);
            try (FileOutputStream fos = new FileOutputStream(tokenFile)) {
                fos.write(encrypted);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    // 插件调用：验证 token 文件内容是否符合
//    public static boolean validateToken(Context context) {
//        try {
//            File tokenFile = new File(context.getFilesDir(), TOKEN_FILENAME);
//            if (!tokenFile.exists()) return false;
//
//            byte[] encrypted = readBytes(tokenFile);
//
//            long now = System.currentTimeMillis();
//            for (int offset = -1; offset <= 1; offset++) {
//                String timeBase = getTimeBase(now + offset * 60 * 1000);
//                SecretKeySpec key = generateKey(timeBase);
//                IvParameterSpec iv = generateIv(timeBase);
//
//                try {
//                    Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
//                    cipher.init(Cipher.DECRYPT_MODE, key, iv);
//                    byte[] decrypted = cipher.doFinal(encrypted);
//
//                    String jsonStr = new String(decrypted, StandardCharsets.UTF_8);
//                    JSONObject json = new JSONObject(jsonStr);
//
//                    String pkg = json.getString("pkg");
//                    long ts = json.getLong("ts");
//                    if (!pkg.equals(context.getPackageName())) continue;
//                    if (Math.abs(now - ts) > 5 * 60 * 1000) continue; // 容差 5 分钟
//
//                    return true; // 校验通过
//                } catch (Exception ignored) {
//                }
//            }
//
//            return false;
//        } catch (Exception e) {
//            return false;
//        }
//    }

    private static byte[] readBytes(File file) throws Exception {
        FileInputStream fis = new FileInputStream(file);
        byte[] data = new byte[(int) file.length()];
        fis.read(data);
        fis.close();
        return data;
    }
}
