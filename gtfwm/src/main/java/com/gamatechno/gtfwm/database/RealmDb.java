package com.gamatechno.gtfwm.database;

import android.content.Context;

import com.gamatechno.gtfwm.Gtfw;
import com.gamatechno.gtfwm.log.SysLog;

import java.security.SecureRandom;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by dyangalih on 9/13/16.
 */
public class RealmDb {

    public void init(Context context){
        Realm.init(context);
        SysLog.getInstance().sendLog("REALM", Gtfw.getInstance().getAppName());

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .schemaVersion(Integer.valueOf(Gtfw.getInstance().getAppVersionCode()))
                .deleteRealmIfMigrationNeeded()
                .encryptionKey((Gtfw.getInstance().getKey64()).getBytes())
                .name(Gtfw.getInstance().getAppName().toLowerCase() + ".realm")
                .build();
        Realm.setDefaultConfiguration(realmConfiguration);
    }

}
