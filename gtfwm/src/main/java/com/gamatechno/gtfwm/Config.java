package com.gamatechno.gtfwm;

import android.content.Context;

import com.gamatechno.gtfwm.cache.SPHelper;

/**
 * Created by dyangalih on 1/30/16.
 */
public class Config {
    public static Config instance;
    private Context context;
    private Class LoginClass;

    public Config(Context context){
        this.context = context;
    }

    public static Config getInstance(Context context) {
        if(instance==null){
            instance = new Config(context);
        }
        return instance;
    }

    public Class getLoginClass() {
        return LoginClass;
    }

    public void setLoginClass(Class loginClass) {
        LoginClass = loginClass;
    }

    public String getConfig(String key){
        return SPHelper.getInstance(context).getDataString("app_conf_" + key);
    }

    public Boolean getConfigBoolean(String key){
        return SPHelper.getInstance(context).getDataBoolean("app_conf_" + key);
    }

    public void setConfig(String key, String value){
        SPHelper.getInstance(context).setData("app_conf_" + key, value);
    }

    public void setConfig(String key, Boolean value){
        SPHelper.getInstance(context).setData("app_conf_" + key, value);
    }
}
