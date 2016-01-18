package org.smart.weservice;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.design.widget.Snackbar;
import android.util.LruCache;
import android.widget.ImageView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import org.smart.framework.Constants;
import org.smart.framework.SmartUtils;

import org.json.JSONObject;

import java.util.HashMap;

public class SmartWebManager implements Constants{

    private static final int TIMEOUT = 5000;

    public enum REQUEST_TYPE{JSON_OBJECT,JSON_ARRAY,IMAGE,GET,POST};
    public enum REQUEST_METHOD_PARAMS{CONTEXT,PARAMS,REQUEST_TYPES,REQUEST_METHOD,TAG,URL,TABLE_NAME,UN_NORMALIZED_FIELDS,RESPONSE_LISTENER};

    private static SmartWebManager mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mCtx;
    private String errorMessage=null;

    private SmartWebManager(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap>
                    cache = new LruCache<String, Bitmap>(20);

            @Override
            public Bitmap getBitmap(String url) {
                return cache.get(url);
            }

            @Override
            public void putBitmap(String url, Bitmap bitmap) {
                cache.put(url, bitmap);
            }
        });
    }

    public static synchronized SmartWebManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SmartWebManager(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }



    public <T> void addToRequestQueue(final HashMap<REQUEST_METHOD_PARAMS,Object> requestParams,String message, final boolean isShowProgress) {

        CustomJsonObjectRequest jsObjRequest=null;

        JSONObject jsonParam=new JSONObject();
        final OnResponseReceivedListener listener = ((OnResponseReceivedListener) requestParams.get(REQUEST_METHOD_PARAMS.RESPONSE_LISTENER));

//        try {
//            jsonParam.put(REQ_OBJECT,((JSONObject)requestParams.get(REQUEST_METHOD_PARAMS.PARAMS)).toString());
//            Log.d("request",jsonParam.toString());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        if(isShowProgress){
            SmartUtils.showProgressDialog((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), message, false);
        }
        if(requestParams.get(REQUEST_METHOD_PARAMS.REQUEST_TYPES)==REQUEST_TYPE.JSON_OBJECT){
            jsObjRequest = new CustomJsonObjectRequest(
                    requestParams.get(REQUEST_METHOD_PARAMS.REQUEST_METHOD)==REQUEST_TYPE.GET?
                            Request.Method.GET:
                            Request.Method.POST,
                    (String)requestParams.get(REQUEST_METHOD_PARAMS.URL),jsonParam, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {
                    errorMessage=SmartUtils.validateResponse((Context)requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), response, errorMessage);
                    if(isShowProgress) {
                        SmartUtils.hideProgressDialog();
                        SmartUtils.hideSoftKeyboard((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                        if(errorMessage != null) {
                            SmartUtils.showSnackBar((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), errorMessage, Snackbar.LENGTH_INDEFINITE);
                            listener.onFailure(errorMessage);
                            return;
                        }
                    }
                    listener.onSuccess(response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    String errorMessage = VolleyErrorHelper.getMessage(error, (Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                    if(isShowProgress) {
                        SmartUtils.hideProgressDialog();
                        SmartUtils.hideSoftKeyboard((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT));
                        SmartUtils.showSnackBar((Context) requestParams.get(REQUEST_METHOD_PARAMS.CONTEXT), errorMessage, Snackbar.LENGTH_INDEFINITE);
                    }
                    listener.onFailure(errorMessage);
                }
            });
        }

        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(
                TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        jsObjRequest.setTag(requestParams.get(REQUEST_METHOD_PARAMS.TAG));
        getRequestQueue().add(jsObjRequest);
    }

    public <T> void addToRequestQueueForImage(Context context,String URL,String tag, final ImageView networkImageView) {

        ImageRequest request = new ImageRequest(URL,new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        networkImageView.setImageBitmap(bitmap);
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    public void onErrorResponse(VolleyError error) {
//                        networkImageView.setImageResource(R.drawable.image_load_error);
                    }
                });

        request.setTag(tag);
        getRequestQueue().add(request);
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

    public interface OnResponseReceivedListener <TResult>{
        void onSuccess(TResult tableRows);
        void onFailure(String errorMessage);
    }

}