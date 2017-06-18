package com.gamatechno.gtfwm.network;

import android.content.Context;
import android.content.Intent;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.gamatechno.gtfwm.Config;
import com.gamatechno.gtfwm.Gtfw;
import com.gamatechno.gtfwm.cache.SPHelper;
import com.gamatechno.gtfwm.log.SysLog;
import com.gamatechno.gtfwm.message.Loader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dyangalih on 3/10/16.
 */
public abstract class Rest {
    private StringRequest request;
    private VolleyMultipartRequest volleyMultipartRequest;
    private RequestQueue queue;
    private final Integer timeout = 25000;
    private final String TAG = "VOLLEY_REST";
    private Context context;

    private static final String SET_COOKIE_KEY = "Set-Cookie";
    private static final String COOKIE_KEY = "Cookie";
    //perlu di ambil dari config
    private static String SESSION_COOKIE;

    public Rest(Context context) {
        this.context = context;
        SESSION_COOKIE = Config.getInstance(context).getConfig("SESSION_COOKIE");
        queue = Volley.newRequestQueue(context);
    }

    private void processWithFile(String url, String tag, final Map<String, DataPart> files, boolean silent) {
        if(!silent) {
            Loader.getInstance().start(context);
        }
        if (Gtfw.getInstance().isHttps() && (url.startsWith("http://"))) {
            url = url.replace("http://", "https://");
        }

        SysLog.getInstance().sendLog("Service Url", url);
        volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Loader.getInstance().stop();
                        onRestResponse(response);
                        SysLog.getInstance().sendLog("REST", response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Loader.getInstance().stop();
                onRestError(String.valueOf(error));
                NetworkResponse networkResponse = error.networkResponse;

            }
        }) {

            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                checkSessionCookie(response.headers);
                return super.parseNetworkResponse(response);
            };

            @Override
            protected Map<String, String> getParams() {
                Map params = setParams();
                if (Gtfw.getInstance().isDebug() && params!=null) {
                    SysLog.getInstance().sendLog("Params", params.toString());
                }
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();
                if (headers == null
                        || headers.equals(Collections.emptyMap())) {
                    headers = new HashMap<>();
                }
                return setHeader(headers);
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = files;
                return params;
            }
        };

        volleyMultipartRequest.setTag(tag);
        volleyMultipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                timeout,
                5,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(volleyMultipartRequest);

    }


    private void process(String url, String tag, Integer method, boolean silent) {
        if(!silent) {
            Loader.getInstance().start(context);
        }
        if (Gtfw.getInstance().isHttps() && (url.startsWith("http://"))) {
            url = url.replace("http://", "https://");
        }

        SysLog.getInstance().sendLog("Service Url", url);

        request = new StringRequest(method, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Loader.getInstance().stop();
                        SysLog.getInstance().sendLog("Result Rest", response);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            if(jsonObject.getJSONObject(Config.getInstance(context).getConfig("RESULT_KEY")).getString("status").equals("401")) {
                                context.startActivity(new Intent(context, Config.getInstance(context).getLoginClass()));
                            }else{
                                onRestResponse(response);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Loader.getInstance().stop();
                        onRestError(error.toString());
                    }
                }
        ) {

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                // since we don't know which of the two underlying network vehicles
                // will Volley use, we have to handle and store session cookies manually
                checkSessionCookie(response.headers);

                return super.parseNetworkResponse(response);
            }

            @Override
            protected Map<String, String> getParams() {
                Map params = setParams();
                if (Gtfw.getInstance().isDebug() && params!=null) {
                    SysLog.getInstance().sendLog("Params", params.toString());
                }
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = super.getHeaders();
                if (headers == null
                        || headers.equals(Collections.emptyMap())) {
                    headers = new HashMap<>();
                }
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return setHeader(headers);
            }

        };

        request.setTag(tag);
        request.setRetryPolicy(new DefaultRetryPolicy(
                timeout,
                5,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }

    private Map<String, String> setHeader(Map<String, String> headers) {
        headers.put("X-GtfwFormat", "json");
        headers.put("User-Code", Gtfw.getInstance().getAppKey(context));
        SysLog.getInstance().sendLog("User-Code", Gtfw.getInstance().getAppKey(context));
        addSessionCookie(headers);
        return headers;
    }

    /*
        * Checks the response headers for session cookie and saves it
        * if it finds it.
        * @param headers Response Headers.
        */
    private final void checkSessionCookie(Map<String, String> headers) {
        SysLog.getInstance().sendLog("FRAMEWORK", "GET HEADER " + COOKIE_KEY + ": " + String.valueOf(headers.containsKey(SET_COOKIE_KEY)));

        if (headers.containsKey(SET_COOKIE_KEY)
                && headers.get(SET_COOKIE_KEY).startsWith(SESSION_COOKIE)) {
            String cookie = headers.get(SET_COOKIE_KEY);
            SysLog.getInstance().sendLog("FRAMWORK", "get cookie header " + cookie);

            if (cookie.length() > 0) {
                SysLog.getInstance().sendLog("Check Session Cookie", cookie);
                String[] splitCookie = cookie.split(";");
                String[] splitSessionId = splitCookie[0].split("=");
                cookie = splitSessionId[1];

                //ConfigRealm.getInstance().addConfig(context,SESSION_COOKIE, cookie);
                SPHelper.getInstance(context).setData(SESSION_COOKIE, cookie);
                SysLog.getInstance().sendLog("FRAMEWORK", "Check Session Cookie with cookie key : " + SESSION_COOKIE);
            }
        }
    }

    /**
     * Adds session cookie to headers if exists.
     *
     * @param headers
     */
    private final void addSessionCookie(Map<String, String> headers) {

        String sessionId = SPHelper.getInstance(context).getDataString(SESSION_COOKIE);
        //String sessionId = ConfigRealm.getInstance().getConfig(context, SESSION_COOKIE);
        SysLog.getInstance().sendLog("FRAMEWORK", "Add Session Cookie, Session Id :" + sessionId);
        SysLog.getInstance().sendLog(SESSION_COOKIE, sessionId);

        if (sessionId != null && !sessionId.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            builder.append(SESSION_COOKIE);
            builder.append("=");
            builder.append(sessionId);

            if (headers.containsKey(COOKIE_KEY)) {
                builder.append("; ");
                builder.append(headers.get(COOKIE_KEY));
            }
            headers.put(COOKIE_KEY, builder.toString());
            SysLog.getInstance().sendLog(COOKIE_KEY, headers.get(COOKIE_KEY));
        }
    }

    public void cancel(String tag) {
        queue.cancelAll(tag);
    }

    public void cancal() {
        queue.cancelAll(TAG);
    }

    public void post(String url) {
        process(url, TAG, Request.Method.POST, false);
    }

    public void postSilent(String url){
        process(url, TAG, Request.Method.POST, true);
    }

    public void get(String url) {
        process(url, TAG, Request.Method.GET, false);
    }

    public void getSilent(String url){
        process(url, TAG, Request.Method.GET, true);
    }

    public void post(String url, String tag) {
        process(url, tag, Request.Method.POST, false);
    }

    public void postSilent(String url, String tag){
        process(url, tag, Request.Method.POST, true);
    }

    public void post(String url, Map<String, DataPart>files){
        processWithFile(url, TAG, files, false);
    }

    public void postSilent(String url, Map<String, DataPart>files){
        processWithFile(url, TAG, files, true);
    }

    public void post(String url, String tag, Map<String, DataPart>files){
        processWithFile(url, tag, files, false);
    }

    public void postSilent(String url, String tag, Map<String, DataPart>files){
        processWithFile(url, tag, files, true);
    }

    public void get(String url, String tag) {
        process(url, tag, Request.Method.GET, false);
    }
    public void getSilent(String url, String tag) {
        process(url, tag, Request.Method.GET, true);
    }

    protected abstract void onRestResponse(String response);

    protected abstract void onRestError(String error);

    protected abstract Map<String, String> setParams();

}
