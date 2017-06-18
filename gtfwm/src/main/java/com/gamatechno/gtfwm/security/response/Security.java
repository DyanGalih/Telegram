package com.gamatechno.gtfwm.security.response;

import android.content.Context;

import com.gamatechno.gtfwm.Gtfw;


import se.simbio.encryption.Encryption;

/**
 * Created by dyangalih on 3/28/16.
 */
public class Security {
    private static Security instance;
    private Context context;
    private static Encryption encryption;


    public Security(Context context) {
        this.context = context;
    }

    public static Security getInstance(Context context) {
        if (instance == null) {
            instance = new Security(context);
            encryption = Encryption.getLowIteration(Gtfw.getInstance().getAppKey(context), "20072007", new byte[16]);
        }
        return instance;
    }

    public String enc(String data) {
        return encryption.encryptOrNull(data);
    }


    public String dec(String data) {
        return encryption.decryptOrNull(data);
    }

}
