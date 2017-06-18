package com.gamatechno.gtfwm.message;

import java.util.HashMap;

/**
 * Created by dyangalih on 6/10/16.
 */
public class ResultMessage {
    private static ResultMessage instance;
    private HashMap<String, String> messageList;

    public static ResultMessage getInstance(){
        if(instance==null){
            instance = new ResultMessage();
        }
        return instance;
    }

    public ResultMessage(){
        messageList = new HashMap<>();
        messageList.put("200", "Request Data Success");
        messageList.put("201", "Change Data Success");
        messageList.put("202", "Access Accepted");
        messageList.put("204", "Data Not Complete");
        messageList.put("302", "Data Found");
        messageList.put("400", "Request Not Match");
        messageList.put("401", "Request Denied / Authentication");
        messageList.put("403", "Forbidden / Authorization");
        messageList.put("404", "Data Not Found");
        messageList.put("405", "Method Not Allowed");
        messageList.put("406", "Change Data Failed");
        messageList.put("407", "Authentication Needed");
        messageList.put("408", "Request Timeout / Database Problem");
        messageList.put("409", "Data Conflict");
        messageList.put("410", "Url Not Found");
        messageList.put("500", "Error System");
        messageList.put("501", "System Not Available");
        messageList.put("502", "System Configuration Problem");
    }

    public String get(String key){
        return messageList.get(key);
    }
}
