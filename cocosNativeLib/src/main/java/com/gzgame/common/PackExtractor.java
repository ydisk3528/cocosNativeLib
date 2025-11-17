package com.gzgame.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class PackExtractor {

    public static void unpack(File packFile, File outputDir, String password) throws Exception {
        if (!outputDir.exists()) outputDir.mkdirs();

        byte[] keyBytes = password.getBytes("UTF-8");

        try (FileInputStream fis = new FileInputStream(packFile)) {
            byte[] ivBytes = new byte[16];
            int readIv = fis.read(ivBytes);
            if (readIv != 16) {
                throw new IOException("file fail.iv error");
            }

            // 读取剩余加密数据（兼容 API 28）
            byte[] encryptedData = readAllBytesCompat(fis);

            // 解密
            byte[] decryptedData = decryptAES(encryptedData, keyBytes, ivBytes);

            try (DataInputStream dis = new DataInputStream(new ByteArrayInputStream(decryptedData))) {
                int count = dis.readInt();
                String[] names = new String[count];
                long[] offsets = new long[count];
                long[] lengths = new long[count];

                for (int i = 0; i < count; i++) {
                    names[i] = dis.readUTF();
                    offsets[i] = dis.readLong();
                    lengths[i] = dis.readLong();
                }

                for (int i = 0; i < count; i++) {
                    File outFile = new File(outputDir, names[i]);
                    File parent = outFile.getParentFile();
                    if (!parent.exists()) parent.mkdirs();

                    try (FileOutputStream fos = new FileOutputStream(outFile)) {
                        fos.write(decryptedData, (int) offsets[i], (int) lengths[i]);
                    }
                }
            }
        }
    }

    // 兼容 API 28 及以上的 readAllBytes()
    private static byte[] readAllBytesCompat(InputStream input) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int nRead;
        while ((nRead = input.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }

    private static byte[] decryptAES(byte[] data, byte[] key, byte[] iv) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivSpec);
        return cipher.doFinal(data);
    }
}
