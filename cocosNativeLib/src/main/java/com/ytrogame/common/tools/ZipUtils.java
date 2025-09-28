package com.ytrogame.common.tools;

import android.content.Context;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;

public class ZipUtils {

    public static void unzipWithPassword(Context context, String zipFilePath, String password) {
        try {
            // 解压路径
            File destDir = new File(context.getFilesDir(), "minigame");
            if (!destDir.exists()) {
                destDir.mkdirs();
            }

            // 创建 ZipFile 实例（带密码）
            ZipFile zipFile = new ZipFile(zipFilePath);
            if (zipFile.isEncrypted()) {
                zipFile.setPassword(password.toCharArray());
            }


            // 解压到目标目录
            zipFile.extractAll(destDir.getAbsolutePath());


        } catch (ZipException e) {
            e.printStackTrace();
        }
    }
}
