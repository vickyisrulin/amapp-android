package org.anoopam.ext.smart.weservice;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import org.anoopam.ext.smart.framework.Constants;
import org.anoopam.ext.smart.framework.SmartApplication;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tasol on 12/10/15.
 */
public class CustomJsonObjectRequest extends JsonObjectRequest {

    private Map<String, String> params;

    public CustomJsonObjectRequest(int method, String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(method, url, jsonRequest, listener, errorListener);
        Log.d("amlog", "Request URL: " + url);
        Log.d("amlog", "Request JSON: " + jsonRequest.toString());
    }

    public CustomJsonObjectRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(url, jsonRequest, listener, errorListener);
    }

    @Override
    protected Response parseNetworkResponse(NetworkResponse response) {
        Map headers = response.headers;
        String cookie = (String) headers.get("Set-Cookie");
        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(Constants.COOKIE, cookie);
        try {
            String utf8String = new String(response.data, "UTF-8");
            return Response.success(new JSONObject(utf8String), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            // log error
        } catch (Exception e) {
            // log error
        }
        return super.parseNetworkResponse(response);
    }

    @Override
    public Map getHeaders() throws AuthFailureError {
        Map headers = new HashMap();
        headers.put("User-agent", "android");
        String cookie=SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(Constants.COOKIE,"");
        if(!cookie.equals(""))
            headers.put(Constants.COOKIE, cookie);
        return headers;
    }

    @Override
    public int getMethod() {
        return super.getMethod();
    }
}
