package com.igame.common.tools;

import android.os.Debug;
import android.util.Log;

public class LogTools {
    public static void LogPrint(String a,String b){
        if (Debug.isDebuggerConnected()){
            Log.e(a,b);
        }

    }
}
