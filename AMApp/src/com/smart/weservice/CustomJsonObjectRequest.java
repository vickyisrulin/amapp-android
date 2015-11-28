package com.smart.weservice;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.smart.framework.Constants;
import com.smart.framework.SmartApplication;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tasol on 12/10/15.
 */
public class CustomJsonObjectRequest extends JsonObjectRequest {

    private Map<String, String> params;

    public CustomJsonObjectRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
    }

    public CustomJsonObjectRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(url, jsonRequest, listener, errorListener);
    }

    @Override
    protected Response parseNetworkResponse(NetworkResponse response) {
        Map headers = response.headers;
        String cookie = (String) headers.get("Set-Cookie");
        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(Constants.COOKIE, cookie);
        return super.parseNetworkResponse(response);
    }

    @Override
    public Map getHeaders() throws AuthFailureError {
        Map headers = new HashMap();
        String cookie=SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(Constants.COOKIE,"");
        if(!cookie.equals(""))
            headers.put(Constants.COOKIE, cookie);
        return headers;
    }


}
