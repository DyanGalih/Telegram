package com.gamatechno.gtfwm.security.response;

import android.app.ProgressDialog;
import android.content.Context;

import com.gamatechno.gtfwm.Config;
import com.gamatechno.gtfwm.log.SysLog;
import com.gamatechno.gtfwm.message.Loader;
import com.gamatechno.gtfwm.network.DataPart;
import com.gamatechno.gtfwm.network.Rest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dyangalih on 4/11/16.
 */
public abstract class Auth {
    private Rest rest;
    private Context context;

    public Auth(Context context) {
        this.context = context;
    }

    private void setLogoutRest(){
        rest = new Rest(context) {

            @Override
            protected void onRestResponse(String response) {
                SysLog.getInstance().sendLog("Logout", response);
                closeProgressDialog();
                try {
                    JSONObject jsonObject = new JSONObject(response).getJSONObject(Config.getInstance(context).getConfig("RESULT_KEY"));
                    if(jsonObject.getString("status").equals("200") || jsonObject.getString("status").equals("201")){
                        onSuccess(true);
                    } else {
                        onSuccess(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onRestError(String error) {
                SysLog.getInstance().sendLog("Logout", error);
                closeProgressDialog();
            }

            @Override
            protected Map<String, String> setParams() {
                Map<String, String> map = new HashMap<>();
                return map;
            }
        };
    }

    public void setLogoutSilent(String urlLogout){
        setLogoutRest();
        rest.getSilent(urlLogout);
    }

    public void setLogout(String urlLogout) {
        setLogoutRest();
        rest.get(urlLogout);
    }

    private void closeProgressDialog() {
        Loader.getInstance().stop();
    }

    private void getSalt(final String urlLogin, String urlSalt, final String username, final String password) {
        rest = new Rest(context) {

            @Override
            protected void onRestResponse(String response) {
                SysLog.getInstance().sendLog("Salt Response", response);

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject messageJson = jsonObject.getJSONObject(Config.getInstance(context).getConfig("RESULT_KEY"));
                    if(messageJson.getString("status").equals("200")){
                        String saltText =  messageJson.getJSONObject("data").getString("salt");
                        SysLog.getInstance().sendLog("Salt", saltText);
                        _setLogin(urlLogin, saltText, username, password);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onRestError(String error) {
                SysLog.getInstance().sendLog("Main Activity", error);
                closeProgressDialog();
            }

            @Override
            protected Map<String, String> setParams() {
                return null;
            }

        };

        rest.getSilent(urlSalt);
    }

    private void _setLogin(String url, final String salt, final String username, final String password) {
        rest = new Rest(context) {

            @Override
            protected void onRestResponse(String response) {
                SysLog.getInstance().sendLog("Auth", response);
                closeProgressDialog();

                try {
                    JSONObject jsonObject = new JSONObject(response).getJSONObject(Config.getInstance(context).getConfig("RESULT_KEY"));
                    if(jsonObject.getString("status").equals("201")){
                        onSuccess(true);
                    } else {
                        onSuccess(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected void onRestError(String error) {
                SysLog.getInstance().sendLog("Auth", error);
                closeProgressDialog();
                onFailed(error);
            }

            @Override
            protected Map<String, String> setParams() {
                Map<String, String> map = new HashMap<>();
                map.put("username", username);
                if(Config.getInstance(context).getConfigBoolean("HASHED")) {
                    if (Config.getInstance(context).getConfigBoolean("NEW_HASH")) {
                        map.put("password", Md5.getInstance().hash(salt + Md5.getInstance().hash(password)));
                    } else {
                        map.put("password", Md5.getInstance().hash(Md5.getInstance().hash(password) + salt));
                    }
                    map.put("hashed", "1");
                }else{
                    map.put("password", password);
                }
                return map;
            }
        };

        rest.postSilent(url);
    }

    public void setLogin(String urlSalt, String urlLogin, final String username, final String password) {
        Loader.getInstance().start(context);
        if(Config.getInstance(context).getConfigBoolean("HASHED")) {
            getSalt(urlLogin, urlSalt, username, password);
        }else{
            _setLogin(urlLogin, "", username, password);
        }
    }

    protected abstract void onSuccess(Boolean status);

    protected abstract void onFailed(String error);

}
