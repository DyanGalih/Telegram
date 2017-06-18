package com.gamatechno.gtfwm.security.response;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by dyangalih on 3/22/16.
 */
public class Md5 {
    private static Md5 instance;

    public static Md5 getInstance() {
        if(instance==null){
            instance = new Md5();
        }
        return instance;
    }

    public String hash(String password){
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md.update(password.getBytes());

        byte byteData[] = md.digest();

        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }
}
