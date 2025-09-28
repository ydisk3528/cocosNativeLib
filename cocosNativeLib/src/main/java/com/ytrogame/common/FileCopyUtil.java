package com.ytrogame.common;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileCopyUtil {

    private Context context;

    public FileCopyUtil(Context context) {
        this.context = context;
    }

    public void copydata(String filepath, String file) {
        InputStream inputStream = null;
        FileOutputStream outputStream = null;

        try {
            // 获取输入流
            inputStream = context.getAssets().open(file);

            // 创建目标文件
            File outFile = new File(filepath);
            if (!outFile.getParentFile().exists()) {
                outFile.getParentFile().mkdirs();
            }

            outputStream = new FileOutputStream(outFile);

            // 开始复制
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.flush();
            System.out.println("File copied successfully to " + filepath);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error copying file: " + e.getMessage());
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
