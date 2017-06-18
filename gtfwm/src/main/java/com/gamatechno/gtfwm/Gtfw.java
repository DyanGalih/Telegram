package com.gamatechno.gtfwm;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;

import com.gamatechno.gtfwm.database.RealmDb;
import com.gamatechno.gtfwm.log.SysLog;
import com.gamatechno.gtfwm.network.NukeSSLCerts;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.realm.Realm;


/**
 * Created by dyangalih on 3/14/16.
 */
public class Gtfw extends Application {

    protected static Gtfw gtfw;
    private boolean https = false;
    private static final String versionNumber = "1.1.0";
    private static final String versionCode = "Zum zum - pulau";
    private boolean debug = false;
    private PackageInfo info;
    private int appVersionCode;
    private String appName;
    private String key=null;


    /*@Override
    public void onCreate() {
        super.onCreate();
        setKey();

    }*/

    public int getAppVersionCode(){
        return appVersionCode;
    }

    public String getAppName() {
        return appName;
    }

    protected void init() {
        //setKey();

        getKey();
        try {
            info = getPackageManager().getPackageInfo(
                    getApplicationContext().getPackageName(), 0);
            appVersionCode = info.versionCode;
            appName = getApplicationContext().getString(R.string.app_name).replace(" ","");

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        RealmDb realmDb = new RealmDb();
        realmDb.init(this);

        Config.getInstance(getApplicationContext()).setConfig("SESSION_COOKIE", "GTFWSessID");
        Config.getInstance(getApplicationContext()).setConfig("RESULT_KEY", "gtfwResult");
        Config.getInstance(getApplicationContext()).setConfig("NEW_HASH", true);
        Config.getInstance(getApplicationContext()).setConfig("HASHED", true);
    }

    public String getKey64(){
        String localkey = getKey().replace("\r\n","").replace("\r","").replace("\n","");
        String key64 = localkey+localkey+localkey;
        SysLog.getInstance().sendLog("KEY", key64);
        return key64.substring(0,64);
    }

    protected String getKey() {
        if(key!=null){
            return key;
        }
        try {
            info = getPackageManager().getPackageInfo(
                    getApplicationContext().getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = Base64.encodeToString(md.digest(), Base64.DEFAULT);
                return key;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    protected void setSSLOn() {
        https = true;
        NukeSSLCerts.nuke();
        SysLog.getInstance().sendLog("https status", String.valueOf(https));
    }

    public boolean isHttps() {
        SysLog.getInstance().sendLog("https status", String.valueOf(https));
        return https;
    }

    public String getAppKey(Context context) {
        //return SPHelper.getInstance(context).getDataString("APP_KEY");
        return getKey();
    }

    public static synchronized Gtfw getInstance() {
        return gtfw;
    }
}
