/*
 * Copyright (c) 2016. Anoopam Mission. This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License at "http://www.gnu.org/licenses/licenses.html" for more details.
 *
 */

package org.anoopam.main.common;

import android.content.ContentValues;
import android.util.Log;

import org.anoopam.main.AMApplication;
import org.anoopam.main.Environment;
import org.anoopam.main.common.events.EventBus;
import org.anoopam.main.common.events.HomeTilesUpdateFailedEvent;
import org.anoopam.main.common.events.HomeTilesUpdateSuccessEvent;
import org.anoopam.main.common.events.QuoteOfTheWeekUpdateFailedEvent;
import org.anoopam.main.common.events.QuoteOfTheWeekUpdateSuccessEvent;
import org.anoopam.main.common.events.SahebjiDarshanUpdateFailedEvent;
import org.anoopam.main.common.events.SahebjiDarshanUpdateSuccessEvent;
import org.anoopam.main.common.events.SplashScreenUpdateFailedEvent;
import org.anoopam.main.common.events.SplashScreenUpdateSuccessEvent;
import org.anoopam.main.common.events.ThakorjiTodayUpdateFailedEvent;
import org.anoopam.main.common.events.ThakorjiTodayUpdateSuccessEvent;
import org.anoopam.ext.smart.caching.SmartCaching;
import org.anoopam.ext.smart.weservice.SmartWebManager;

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
     * makes the server calls to get the updated metadata for the feature flows
     */
    public void fetchUpdatedServerData() {
        //TODO: Optimize these calls to get the data in one server request
        AMServiceRequest.getInstance().startThakorjiTodayUpdatesFromServer();
        AMServiceRequest.getInstance().startSahebjiDarshanUpdatesFromServer();
        AMServiceRequest.getInstance().startQuoteOfTheWeekUpdatesFromServer();
        AMServiceRequest.getInstance().startNewsUpdatesFromServer();
        AMServiceRequest.getInstance().startFetchingNewSplashScreenFromServer();
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
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new AMServiceResponseListener() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            smartCaching.cacheResponse(response.getJSONArray("images"), "splashMessages", true, new SmartCaching.OnResponseParsedListener() {
                                @Override
                                public void onParsed(HashMap<String, ArrayList<ContentValues>> mapTableNameAndData) {
                                    if (mapTableNameAndData.get("splashMessages") != null) {
                                        Log.d(TAG, "obtained splash screen data successfully");
                                        EventBus.getInstance().post(new SplashScreenUpdateSuccessEvent());
                                    }
                                }
                            }, /*runOnMainThread*/ false, "splashMessages");
                            AMApplication.getInstance()
                                    .writeSharedPreferences(AMConstants.KEY_SplashScreenLastUpdatedTimestamp, response
                                            .getString(AMConstants.AMS_RequestParam_SplashScreen_LastUpdatedTimestamp));
                        } catch (JSONException e) {
                            EventBus.getInstance().post(new SplashScreenUpdateFailedEvent());
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(String failureMessage) {
                        Log.e(TAG, "Error obtaining Splash screen data: " + failureMessage);
                        EventBus.getInstance().post(new ThakorjiTodayUpdateFailedEvent());
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
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new AMServiceResponseListener() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            smartCaching.cacheResponse(response.getJSONArray("temples"), "temples", true, new SmartCaching.OnResponseParsedListener() {
                                @Override
                                public void onParsed(HashMap<String, ArrayList<ContentValues>> mapTableNameAndData) {
                                    if (mapTableNameAndData.get("temples") != null) {
                                        Log.d(TAG, "obtained temple data successfully");
                                        EventBus.getInstance().post(new ThakorjiTodayUpdateSuccessEvent());
                                    }
                                }
                            }, /*runOnMainThread*/ false, "images");
                            AMApplication.getInstance()
                                    .writeSharedPreferences(AMConstants.KEY_ThakorjiTodayLastUpdatedTimestamp, response
                                            .getString(AMConstants.AMS_RequestParam_ThakorjiToday_LastUpdatedTimestamp));
                        } catch (JSONException e) {
                            EventBus.getInstance().post(new ThakorjiTodayUpdateFailedEvent());
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(String failureMessage) {
                        Log.e(TAG, "Error obtaining temple data: " + failureMessage);
                        EventBus.getInstance().post(new ThakorjiTodayUpdateFailedEvent());
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
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new AMServiceResponseListener() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            smartCaching.cacheResponse(response.getJSONArray("homeTiles"), "homeTiles", true, new SmartCaching.OnResponseParsedListener() {
                                @Override
                                public void onParsed(HashMap<String, ArrayList<ContentValues>> mapTableNameAndData) {
                                    if (mapTableNameAndData.get("homeTiles") != null) {
                                        Log.d(TAG, "obtained homeTiles data successfully");
                                        EventBus.getInstance().post(new HomeTilesUpdateSuccessEvent());
                                    }
                                }
                            }, /*runOnMainThread*/ false, "homeTiles");
                            AMApplication.getInstance()
                                    .writeSharedPreferences(AMConstants.KEY_HomeScreenLastUpdatedTimestamp, response
                                            .getString(AMConstants.AMS_RequestParam_HomeScreen_LastUpdatedTimestamp));
                        } catch (JSONException e) {
                            EventBus.getInstance().post(new HomeTilesUpdateFailedEvent());
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(String failureMessage) {
                        Log.e(TAG, "Error obtaining HomeTiles data: " + failureMessage);
                        EventBus.getInstance().post(new HomeTilesUpdateFailedEvent());
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
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new AMServiceResponseListener() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            smartCaching.cacheResponse(response.getJSONArray("images"), "sahebjiDarshanImages", true, new SmartCaching.OnResponseParsedListener() {
                                @Override
                                public void onParsed(HashMap<String, ArrayList<ContentValues>> mapTableNameAndData) {
                                    if (mapTableNameAndData.get("sahebjiDarshanImages") != null) {
                                        Log.d(TAG, "obtained sahebjiDarshan Images data successfully");
                                        EventBus.getInstance().post(new SahebjiDarshanUpdateSuccessEvent());
                                    }
                                }
                            }, /*runOnMainThread*/ false, "sahebjiDarshanImages");
                            AMApplication.getInstance()
                                    .writeSharedPreferences(AMConstants.KEY_SahebjiDarshanLastUpdatedTimestamp, response
                                            .getString(AMConstants.AMS_RequestParam_SahebjiDarshan_LastUpdatedTimestamp));
                        } catch (JSONException e) {
                            EventBus.getInstance().post(new SahebjiDarshanUpdateFailedEvent());
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(String failureMessage) {
                        Log.e(TAG, "Error obtaining Sahebji Darshan data: " + failureMessage);
                        EventBus.getInstance().post(new HomeTilesUpdateFailedEvent());
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
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new AMServiceResponseListener() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            smartCaching.cacheResponse(response.getJSONArray("images"), "qowImages", true, new SmartCaching.OnResponseParsedListener() {
                                @Override
                                public void onParsed(HashMap<String, ArrayList<ContentValues>> mapTableNameAndData) {
                                    if (mapTableNameAndData.get("qowImages") != null) {
                                        Log.d(TAG, "obtained QOW Images data successfully");
                                        EventBus.getInstance().post(new QuoteOfTheWeekUpdateSuccessEvent());
                                    }
                                }
                            }, /*runOnMainThread*/ false, "qowImages");
                            AMApplication.getInstance()
                                    .writeSharedPreferences(AMConstants.KEY_QuoteOfTheWeekLastUpdatedTimestamp, response
                                            .getString(AMConstants.AMS_RequestParam_QuoteOfTheWeek_LastUpdatedTimestamp));
                        } catch (JSONException e) {
                            EventBus.getInstance().post(new QuoteOfTheWeekUpdateFailedEvent());
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(String failureMessage) {
                        Log.e(TAG, "Error obtaining QOW data: " + failureMessage);
                        EventBus.getInstance().post(new HomeTilesUpdateFailedEvent());
                    }
                });
        SmartWebManager.getInstance(AMApplication.getInstance().getApplicationContext()).addToRequestQueue(requestParams, null, false);
    }

    /**
     * invokes the request to get the updated QOW data from the server
     */
    public void startNewsUpdatesFromServer() {
        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT,AMApplication.getInstance().getApplicationContext());
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, null);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, AMConstants.AMS_Request_Get_News_Tag);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getNewsLastUpdatedTimestamp());
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_METHOD, SmartWebManager.REQUEST_TYPE.GET);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new AMServiceResponseListener() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    smartCaching.cacheResponse(response.getJSONArray("images"), "newsImages", true, new SmartCaching.OnResponseParsedListener() {
                        @Override
                        public void onParsed(HashMap<String, ArrayList<ContentValues>> mapTableNameAndData) {
                            if (mapTableNameAndData.get("newsImages") != null) {
                                Log.d(TAG, "obtained News data successfully");
                            }
                        }
                    }, /*runOnMainThread*/ false, "newsImages");
                    AMApplication.getInstance()
                            .writeSharedPreferences(AMConstants.KEY_NewsLastUpdatedTimestamp, response
                                    .getString(AMConstants.AMS_RequestParam_News_LastUpdatedTimestamp));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String failureMessage) {
                Log.e(TAG, "Error obtaining News data: " + failureMessage);
            }
        });
        SmartWebManager.getInstance(AMApplication.getInstance().getApplicationContext()).addToRequestQueue(requestParams, null, false);
    }

    // gets the latest timestamp cached on the client side
    // and addes it into the Sahebji Darshan endpoint as param
    private String getSahebjiDarshanUrlWithLatestCachedTimestamp() {
        String endpoint = mEnvironment.getSahebjiDarshanEndpiont();
        String lastUpdatedTimeStamp = AMApplication.getInstance()
                .readSharedPreferences().getString(AMConstants.KEY_SahebjiDarshanLastUpdatedTimestamp, "");
        return String.format(endpoint,lastUpdatedTimeStamp,getNetworkSpeedParamValue());
    }

    // gets the latest timestamp cached on the client side
    // and addes it into the ThakorjiToday endpoint as param
    private String getThakorjiTodayUrlWithLatestCachedTimestamp() {
        String endpoint = mEnvironment.getThakorjiTodayEndpoint();
        String lastUpdatedTimeStamp = AMApplication.getInstance()
                .readSharedPreferences().getString(AMConstants.KEY_ThakorjiTodayLastUpdatedTimestamp, "");
        return String.format(endpoint,lastUpdatedTimeStamp,getNetworkSpeedParamValue());
    }

    // gets the latest timestamp cached on the client side
    // and addes it into the Home Screen endpoint as param
    public String getHomeTilesUrlWithLatestCachedTimestamp() {
        String endpoint = mEnvironment.getHomeTilesEndpoint();
        String lastUpdatedTimeStamp = AMApplication.getInstance()
                .readSharedPreferences().getString(AMConstants.KEY_HomeScreenLastUpdatedTimestamp, "");
        return String.format(endpoint,lastUpdatedTimeStamp,getNetworkSpeedParamValue());
    }

    // gets the latest timestamp cached on the client side
    // and addes it into the Splash Screen endpoint as param
    public String getSplashScreenLastUpdatedTimeStamp() {
        String endpoint = mEnvironment.getSplashScreenEndpoint();
        String lastUpdatedTimeStamp = AMApplication.getInstance()
                .readSharedPreferences().getString(AMConstants.KEY_SplashScreenLastUpdatedTimestamp, "");
        return String.format(endpoint,lastUpdatedTimeStamp,getNetworkSpeedParamValue());
    }

    // gets the latest timestamp cached on the client side
    // and addes it into the Splash Screen endpoint as param
    public String getQuoteOfTheWeekLastUpdatedTimestamp() {
        String endpoint = mEnvironment.getQuoteOfTheWeekEndpoint();
        String lastUpdatedTimeStamp = AMApplication.getInstance()
                .readSharedPreferences().getString(AMConstants.KEY_QuoteOfTheWeekLastUpdatedTimestamp, "");
        return String.format(endpoint,lastUpdatedTimeStamp,getNetworkSpeedParamValue());
    }

    // gets the latest timestamp cached on the client side
    // and addes it into the News endpoint as param
    public String getNewsLastUpdatedTimestamp() {
        //String endpoint = mEnvironment.getNewsEndpoint();
        String endpoint = AMConstants.MOCK_MOCKY_Domain_Url+AMConstants.MOCK_MOCKY_News_Endpoint_Suffix;
        String lastUpdatedTimeStamp = AMApplication.getInstance()
                .readSharedPreferences().getString(AMConstants.KEY_NewsLastUpdatedTimestamp, "");
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
