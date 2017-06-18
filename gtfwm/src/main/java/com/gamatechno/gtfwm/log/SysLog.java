package com.gamatechno.gtfwm.log;

import android.util.Log;

import com.gamatechno.gtfwm.Gtfw;

/**
 * Created by dyangalih on 3/11/16.
 */
public class SysLog {
    private static SysLog instance;

    public static SysLog getInstance() {
        if(instance==null){
            instance = new SysLog();
        }
        return instance;
    }

    public void sendLog(String tag, String log){
        if(Gtfw.getInstance().isDebug()){
            if(log==null){
                log = "";
            }
            int maxLogSize = 1000;
            if (log.length() >= maxLogSize) {
                for (int i = 0; i <= log.length() / maxLogSize; i++) {
                    int start = i * maxLogSize;
                    int end = (i + 1) * maxLogSize;
                    end = end > log.length() ? log.length() : end;
                    Log.i(tag, log.substring(start, end));
                }
            } else {
                Log.i(tag, log);
            }
        }
    }
}
