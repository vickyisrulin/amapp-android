package org.anoopam.main;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.anoopam.ext.smart.caching.SmartCaching;
import org.anoopam.ext.smart.weservice.SmartWebManager;
import org.anoopam.main.common.AMConstants;
import org.anoopam.main.common.AMServiceResponseListener;
import org.anoopam.main.common.notifications.NotificationsUtil;
import org.json.JSONObject;

import java.util.HashMap;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    private SmartCaching smartCaching;


    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.

        AMApplication.getInstance()
                .writeSharedPreferences("DeviceRegistrationId",refreshedToken);

        sendRegistrationToServer(refreshedToken);
    }
    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {

        Log.i(TAG,"---------sendRegistrationToServer--1-"+NotificationsUtil.getAndroidID(getApplicationContext()));
        Log.i(TAG,"---------sendRegistrationToServer--2-"+token);

        String WEBSERVICE_URL = "http://anoopam.org/api/ams/v2/deviceRegistration.php?action=notification-registration&device_reg_id="+token+"&deviceid="+ NotificationsUtil.getAndroidID(getApplicationContext())+"&country="+getApplicationContext().getResources().getConfiguration().locale.getCountry();

        smartCaching = new SmartCaching(AMApplication.getInstance().getApplicationContext());
        // TODO: Implement this method to send token to your app server.

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT,AMApplication.getInstance().getApplicationContext());
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, null);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, AMConstants.AMS_Request_Get_Audio_Cat_Tag);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, WEBSERVICE_URL);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_METHOD, SmartWebManager.REQUEST_TYPE.GET);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new AMServiceResponseListener() {
            @Override
            public void onSuccess(JSONObject response) {
                try {

                    Log.i(TAG,">>>>>>>>>>>>>>>>>>>>>>>"+String.valueOf(response.toString()));
                 /*   smartCaching.cacheResponse(response.getJSONArray("categories"), "categories", true);
                    smartCaching.cacheResponse(response.getJSONArray("audios"), "audios", true);*/
                    /*AMApplication.getInstance()
                            .writeSharedPreferences(AMConstants.KEY_AnoopamAudioLastUpdatedTimestamp, response
                                    .getString(AMConstants.AMS_RequestParam_AnoopamAudio_LastUpdatedTimestamp));*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String failureMessage) {
                Log.e(TAG, "Error obtaining Audio metadata: " + failureMessage);
            }
        });
        SmartWebManager.getInstance(AMApplication.getInstance().getApplicationContext()).addToRequestQueue(requestParams, null, false);

    }
}
