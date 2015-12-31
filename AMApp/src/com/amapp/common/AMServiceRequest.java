package com.amapp.common;

import android.content.ContentValues;
import android.util.Log;

import com.amapp.AMApplication;
import com.amapp.Environment;
import com.amapp.common.events.EventBus;
import com.amapp.common.events.HomeTilesUpdateFailedEvent;
import com.amapp.common.events.HomeTilesUpdateSuccessEvent;
import com.amapp.common.events.QuoteOfTheWeekUpdateFailedEvent;
import com.amapp.common.events.QuoteOfTheWeekUpdateSuccessEvent;
import com.amapp.common.events.SahebjiDarshanUpdateFailedEvent;
import com.amapp.common.events.SahebjiDarshanUpdateSuccessEvent;
import com.amapp.common.events.SplashScreenUpdateFailedEvent;
import com.amapp.common.events.SplashScreenUpdateSuccessEvent;
import com.amapp.common.events.ThakorjiTodayUpdateFailedEvent;
import com.amapp.common.events.ThakorjiTodayUpdateSuccessEvent;
import com.smart.caching.SmartCaching;
import com.smart.framework.SmartApplication;
import com.smart.weservice.SmartWebManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by dadesai on 12/23/15.
 */
public class AMServiceRequest {

    private static final String TAG = "AMServiceRequest";
    private SmartCaching smartCaching;
    private static AMServiceRequest amServiceRequestInstance;
    private Environment mEnvironment;

    private AMServiceRequest() {
        mEnvironment = AMApplication.getInstance().getEnv();
        smartCaching = new SmartCaching(AMApplication.getInstance().getApplicationContext());
    }

    public static AMServiceRequest getInstance() {
        if(amServiceRequestInstance == null) {
            amServiceRequestInstance = new AMServiceRequest();
        }
        return amServiceRequestInstance;
    }

    /**
     * invokes the request to get the updated Splash Screen data from the server
     */
    public void startFetchingNewSplashScreenFromServer() {
        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT,AMApplication.getInstance().getApplicationContext());
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, null);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, AMConstants.AMS_Request_Get_SplashScreen_Tag);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getSplashScreenLastUpdatedTimeStamp());
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_METHOD, SmartWebManager.REQUEST_TYPE.GET);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, String errorMessage) {

                if (errorMessage != null) {
                    Log.e(TAG, "Error obtaining Splash screen data: " + errorMessage);
                    EventBus.getInstance().post(new ThakorjiTodayUpdateFailedEvent());
                } else {
                    try {
                        smartCaching.cacheResponse(response.getJSONArray("images"), "splashMessages", true, new SmartCaching.OnResponseParsedListener() {
                            @Override
                            public void onParsed(HashMap<String, ArrayList<ContentValues>> mapTableNameAndData) {
                                if(mapTableNameAndData.get("splashMessages") != null) {
                                    Log.d(TAG, "obtained splash screen data successfully");
                                    EventBus.getInstance().post(new SplashScreenUpdateSuccessEvent());
                                }
                            }
                        }, /*runOnMainThread*/ false, "splashMessages");
                        SmartApplication.REF_SMART_APPLICATION
                                .writeSharedPreferences(AMConstants.KEY_SplashScreenLastUpdatedTimestamp, response
                                        .getString(AMConstants.AMS_RequestParam_SplashScreen_LastUpdatedTimestamp));
                    } catch (JSONException e) {
                        EventBus.getInstance().post(new SplashScreenUpdateFailedEvent());
                        e.printStackTrace();
                    }
                }
            }
        });

        SmartWebManager.getInstance(AMApplication.getInstance().getApplicationContext()).addToRequestQueue(requestParams, null, false);
    }

    /**
     * invokes the request to get the updated Thakorji Today data from the server
     */
    public void startThakorjiTodayUpdatesFromServer() {
        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT,AMApplication.getInstance().getApplicationContext());
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, null);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, AMConstants.AMS_Request_Get_Temples_Tag);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getThakorjiTodayUrlWithLatestCachedTimestamp());
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_METHOD, SmartWebManager.REQUEST_TYPE.GET);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, String errorMessage) {

                if (errorMessage != null) {
                    Log.e(TAG, "Error obtaining temple data: " + errorMessage);
                    EventBus.getInstance().post(new ThakorjiTodayUpdateFailedEvent());
                } else {
                    try {
                        smartCaching.cacheResponse(response.getJSONArray("temples"), "temples", true, new SmartCaching.OnResponseParsedListener() {
                            @Override
                            public void onParsed(HashMap<String, ArrayList<ContentValues>> mapTableNameAndData) {
                                if(mapTableNameAndData.get("temples") != null) {
                                    Log.d(TAG, "obtained temple data successfully");
                                    EventBus.getInstance().post(new ThakorjiTodayUpdateSuccessEvent());
                                }
                            }
                        }, /*runOnMainThread*/ false, "images");
                        SmartApplication.REF_SMART_APPLICATION
                                .writeSharedPreferences(AMConstants.KEY_ThakorjiTodayLastUpdatedTimestamp, response
                                        .getString(AMConstants.AMS_RequestParam_ThakorjiToday_LastUpdatedTimestamp));
                    } catch (JSONException e) {
                        EventBus.getInstance().post(new ThakorjiTodayUpdateFailedEvent());
                        e.printStackTrace();
                    }
                }
            }
        });

        SmartWebManager.getInstance(AMApplication.getInstance().getApplicationContext()).addToRequestQueue(requestParams, null, false);
    }

    /**
     * invokes the request to get the updated Home Tiles data from the server
     */
    public void startHomeScreenTilesUpdatesFromServer() {
        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT,AMApplication.getInstance().getApplicationContext());
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, null);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, AMConstants.AMS_Request_Get_HomeScreen_List_Tag);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getHomeTilesUrlWithLatestCachedTimestamp());
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_METHOD, SmartWebManager.REQUEST_TYPE.GET);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, String errorMessage) {

                if (errorMessage != null) {
                    Log.e(TAG, "Error obtaining HomeTiles data: " + errorMessage);
                    EventBus.getInstance().post(new HomeTilesUpdateFailedEvent());
                } else {
                    try {
                        smartCaching.cacheResponse(response.getJSONArray("homeTiles"), "homeTiles", true, new SmartCaching.OnResponseParsedListener() {
                            @Override
                            public void onParsed(HashMap<String, ArrayList<ContentValues>> mapTableNameAndData) {
                                if(mapTableNameAndData.get("homeTiles") != null) {
                                    Log.d(TAG, "obtained homeTiles data successfully");
                                    EventBus.getInstance().post(new HomeTilesUpdateSuccessEvent());
                                }
                            }
                        }, /*runOnMainThread*/ false, "homeTiles");
                        SmartApplication.REF_SMART_APPLICATION
                                .writeSharedPreferences(AMConstants.KEY_HomeScreenLastUpdatedTimestamp, response
                                        .getString(AMConstants.AMS_RequestParam_HomeScreen_LastUpdatedTimestamp));
                    } catch (JSONException e) {
                        EventBus.getInstance().post(new HomeTilesUpdateFailedEvent());
                        e.printStackTrace();
                    }
                }
            }
        });

        SmartWebManager.getInstance(AMApplication.getInstance().getApplicationContext()).addToRequestQueue(requestParams, null, false);
    }

    /**
     * invokes the request to get the updated Home Tiles data from the server
     */
    public void startSahebjiDarshanUpdatesFromServer() {
        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT,AMApplication.getInstance().getApplicationContext());
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, null);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, AMConstants.AMS_Request_Get_Sahebji_Tag);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getSahebjiDarshanUrlWithLatestCachedTimestamp());
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_METHOD, SmartWebManager.REQUEST_TYPE.GET);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, String errorMessage) {

                if (errorMessage != null) {
                    Log.e(TAG, "Error obtaining Sahebji Darshan data: " + errorMessage);
                    EventBus.getInstance().post(new HomeTilesUpdateFailedEvent());
                } else {
                    try {
                        smartCaching.cacheResponse(response.getJSONArray("images"), "sahebjiDarshanImages", true, new SmartCaching.OnResponseParsedListener() {
                            @Override
                            public void onParsed(HashMap<String, ArrayList<ContentValues>> mapTableNameAndData) {
                                if(mapTableNameAndData.get("sahebjiDarshanImages") != null) {
                                    Log.d(TAG, "obtained sahebjiDarshan Images data successfully");
                                    EventBus.getInstance().post(new SahebjiDarshanUpdateSuccessEvent());
                                }
                            }
                        }, /*runOnMainThread*/ false, "sahebjiDarshanImages");
                        SmartApplication.REF_SMART_APPLICATION
                                .writeSharedPreferences(AMConstants.KEY_SahebjiDarshanLastUpdatedTimestamp, response
                                        .getString(AMConstants.AMS_RequestParam_SahebjiDarshan_LastUpdatedTimestamp));
                    } catch (JSONException e) {
                        EventBus.getInstance().post(new SahebjiDarshanUpdateFailedEvent());
                        e.printStackTrace();
                    }
                }
            }
        });

        SmartWebManager.getInstance(AMApplication.getInstance().getApplicationContext()).addToRequestQueue(requestParams, null, false);
    }

    /**
     * invokes the request to get the updated QOW data from the server
     */
    public void startQuoteOfTheWeekUpdatesFromServer() {
        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT,AMApplication.getInstance().getApplicationContext());
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, null);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, AMConstants.AMS_Request_Get_Quotes_Tag);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getQuoteOfTheWeekLastUpdatedTimestamp());
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_METHOD, SmartWebManager.REQUEST_TYPE.GET);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, String errorMessage) {

                if (errorMessage != null) {
                    Log.e(TAG, "Error obtaining QOW data: " + errorMessage);
                    EventBus.getInstance().post(new HomeTilesUpdateFailedEvent());
                } else {
                    try {
                        smartCaching.cacheResponse(response.getJSONArray("images"), "qowImages", true, new SmartCaching.OnResponseParsedListener() {
                            @Override
                            public void onParsed(HashMap<String, ArrayList<ContentValues>> mapTableNameAndData) {
                                if(mapTableNameAndData.get("qowImages") != null) {
                                    Log.d(TAG, "obtained QOW Images data successfully");
                                    EventBus.getInstance().post(new QuoteOfTheWeekUpdateSuccessEvent());
                                }
                            }
                        }, /*runOnMainThread*/ false, "qowImages");
                        SmartApplication.REF_SMART_APPLICATION
                                .writeSharedPreferences(AMConstants.KEY_QuoteOfTheWeekLastUpdatedTimestamp, response
                                        .getString(AMConstants.AMS_RequestParam_QuoteOfTheWeek_LastUpdatedTimestamp));
                    } catch (JSONException e) {
                        EventBus.getInstance().post(new QuoteOfTheWeekUpdateFailedEvent());
                        e.printStackTrace();
                    }
                }
            }
        });

        SmartWebManager.getInstance(AMApplication.getInstance().getApplicationContext()).addToRequestQueue(requestParams, null, false);
    }

    // gets the latest timestamp cached on the client side
    // and addes it into the Sahebji Darshan endpoint as param
    private String getSahebjiDarshanUrlWithLatestCachedTimestamp() {
//        String endpoint = "http://www.mocky.io/v2/5682666b1000006e01153868";
//        return endpoint;
        String endpoint = AMApplication.getInstance().getEnv().getSahebjiDarshanEndpiont();
        String lastUpdatedTimeStamp = AMApplication.REF_SMART_APPLICATION
                .readSharedPreferences().getString(AMConstants.KEY_SahebjiDarshanLastUpdatedTimestamp, "");
        return String.format(endpoint,lastUpdatedTimeStamp,getNetworkSpeedParamValue());
    }

    // gets the latest timestamp cached on the client side
    // and addes it into the ThakorjiToday endpoint as param
    private String getThakorjiTodayUrlWithLatestCachedTimestamp() {
        String endpoint = AMApplication.getInstance().getEnv().getThakorjiTodayEndpoint();
        String lastUpdatedTimeStamp = AMApplication.REF_SMART_APPLICATION
                .readSharedPreferences().getString(AMConstants.KEY_ThakorjiTodayLastUpdatedTimestamp, "");
        return String.format(endpoint,lastUpdatedTimeStamp,getNetworkSpeedParamValue());
    }

    // gets the latest timestamp cached on the client side
    // and addes it into the Home Screen endpoint as param
    public String getHomeTilesUrlWithLatestCachedTimestamp() {
        String endpoint = AMApplication.getInstance().getEnv().getHomeTilesEndpoint();
        String lastUpdatedTimeStamp = AMApplication.REF_SMART_APPLICATION
                .readSharedPreferences().getString(AMConstants.KEY_HomeScreenLastUpdatedTimestamp, "");
        return String.format(endpoint,lastUpdatedTimeStamp,getNetworkSpeedParamValue());
    }

    // gets the latest timestamp cached on the client side
    // and addes it into the Splash Screen endpoint as param
    public String getSplashScreenLastUpdatedTimeStamp() {
        String endpoint = AMApplication.getInstance().getEnv().getSplashScreenEndpoint();
        String lastUpdatedTimeStamp = AMApplication.REF_SMART_APPLICATION
                .readSharedPreferences().getString(AMConstants.KEY_SplashScreenLastUpdatedTimestamp, "");
        return String.format(endpoint,lastUpdatedTimeStamp,getNetworkSpeedParamValue());
    }

    // gets the latest timestamp cached on the client side
    // and addes it into the Splash Screen endpoint as param
    public String getQuoteOfTheWeekLastUpdatedTimestamp() {
//        //FIXME: once LIVE is available, replace this
//        String endpoint = "http://www.mocky.io/v2/5682666b1000006e01153868";
//        return endpoint;
        String endpoint = AMApplication.getInstance().getEnv().getQuoteOfTheWeekEndpoint();
        String lastUpdatedTimeStamp = AMApplication.REF_SMART_APPLICATION
                .readSharedPreferences().getString(AMConstants.KEY_QuoteOfTheWeekLastUpdatedTimestamp, "");
        return String.format(endpoint,lastUpdatedTimeStamp,getNetworkSpeedParamValue());
    }

    private String getNetworkSpeedParamValue() {
        if(NetworkConnectionInfo.isMobileDataConnected(AMApplication.getInstance().getApplicationContext())) {
            return "slow";
        } else {
            return "fast";
        }
    }
}
