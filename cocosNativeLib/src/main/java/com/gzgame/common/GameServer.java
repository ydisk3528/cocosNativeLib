package com.gzgame.common;



import static com.gzgame.common.main.BaseActivity.baseActivity;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;

import fi.iki.elonen.NanoHTTPD;

public class GameServer extends NanoHTTPD {
    private final AssetManager assetManager;
    public static int port = findAvailablePort();

    public GameServer(Context context) throws IOException {
        super(port);
        assetManager = context.getAssets();
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        System.out.println("Request URI: " + uri);

        try {
            if (uri.equals("/")) {
                uri = "/index.html";
            }

            // 拼接路径：files/minigame/...
            String filePath = new File(baseActivity .getFilesDir(), "minigame" + uri).getAbsolutePath();
            File requestedFile = new File(filePath);

            if (!requestedFile.exists()) {
                return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Not Found");
            }

            FileInputStream fileInputStream = new FileInputStream(requestedFile);
            String mimeType = getMimeType(requestedFile.getName());

            Response response = newChunkedResponse(Response.Status.OK, mimeType, fileInputStream);

            // Gzip 压缩支持
            if (filePath.endsWith(".gz")) {
                response.addHeader("Content-Encoding", "gzip");
                if (filePath.endsWith(".js.gz")) response.setMimeType("application/javascript");
                else if (filePath.endsWith(".wasm.gz")) response.setMimeType("application/wasm");
                else if (filePath.endsWith(".data.gz")) response.setMimeType("application/octet-stream");
            }
            // Brotli 压缩支持
            else if (filePath.endsWith(".br")) {
                response.addHeader("Content-Encoding", "br");
                if (filePath.endsWith(".js.br")) response.setMimeType("application/javascript");
                else if (filePath.endsWith(".wasm.br")) response.setMimeType("application/wasm");
                else if (filePath.endsWith(".data.br")) response.setMimeType("application/octet-stream");
            }

            return response;

        } catch (IOException e) {
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "Internal Server Error: " + e.getMessage());
        }
    }

    private String getMimeType(String fileName) {
        String extension = fileName.substring(fileName.lastIndexOf('.') + 1);
        return getMimeTypeByExtension(extension);
    }

    private String getMimeTypeByExtension(String extension) {
        switch (extension) {
            case "html":
                return MIME_HTML;
            case "css":
                return "text/css";
            case "js":
                return "application/javascript";
            case "wasm":
                return "application/wasm";
            case "data":
                return "application/octet-stream";
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "br":
                return "application/octet-stream";  // 默认 .br 用这个
            default:
                return "application/octet-stream";
        }
    }

    public static int findAvailablePort() {
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(0);
            return serverSocket.getLocalPort();
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        } finally {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void start() throws IOException {
        super.start();
    }

    public int getPort() {
        return port;
    }

    @Override
    public void stop() {
        super.stop();
    }
}
