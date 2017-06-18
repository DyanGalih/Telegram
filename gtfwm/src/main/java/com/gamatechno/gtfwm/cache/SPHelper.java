package com.gamatechno.gtfwm.cache;

import android.content.Context;
import android.content.SharedPreferences;

import com.gamatechno.gtfwm.Gtfw;
import com.gamatechno.gtfwm.log.SysLog;
import com.gamatechno.gtfwm.security.response.Security;

/**
 * Created by dyangalih on 3/8/16.
 */
public class SPHelper {
    private final String SP_NAME = "GTFW";
    private SharedPreferences sp;
    private Context context;
    private SharedPreferences.Editor editor;
    private static SPHelper instance;

    public SPHelper(Context context) {
        this.context = context;
    }

    public static SPHelper getInstance(Context context) {
        if(instance==null){
            instance = new SPHelper(context);
        }
        return instance;
    }

    private void openSP(){
        sp = context.getSharedPreferences(SP_NAME, 0);
        editor = sp.edit();
    }

    public void setData(String key, String value){
        openSP();
        SysLog.getInstance().sendLog("Security",Security.getInstance(context).enc(value));
        editor.putString(key, Security.getInstance(context).enc(value));
        //editor.putString(key, value);
        editor.commit();
    }

    public void setData(String key, Integer value){
        openSP();
        editor.putInt(key, value);
        editor.commit();
    }

    public void setData(String key, Boolean value){
        openSP();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public String getDataString(String key){
        sp = context.getSharedPreferences(SP_NAME, 0);
        SysLog.getInstance().sendLog("Security",sp.getString(key, null));
        //return sp.getString(key, null);
        return Security.getInstance(context).dec(sp.getString(key, null));
    }

    public Integer getDataInt(String key){
        sp = context.getSharedPreferences(SP_NAME, 0);
        return sp.getInt(key, 0);
    }

    public Boolean getDataBoolean(String key){
        sp = context.getSharedPreferences(SP_NAME, 0);
        return sp.getBoolean(key, false);
    }

}
