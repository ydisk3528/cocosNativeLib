package com.rugame.common.tools;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class NetworkUtils {

    // 创建一个单线程池来处理异步任务
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();

    public interface Callback {
        void onSuccess(String result);
        void onError(Exception e);
    }

    public static void makeGetRequest(String urlString, Callback callback) {
        executor.execute(() -> {
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(urlString);
                urlConnection = (HttpURLConnection) url.openConnection();

                // 设置请求方法和超时
                urlConnection.setRequestMethod("GET");
                urlConnection.setConnectTimeout(5000); // 5秒
                urlConnection.setReadTimeout(5000);

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = in.readLine()) != null) {
                        response.append(line);
                    }
                    in.close();

                    // 成功回调
                    callback.onSuccess(response.toString());
                } else {
                    callback.onError(new Exception("HTTP Error: " + responseCode));
                }
            } catch (Exception e) {
                callback.onError(e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }
        });
    }
}