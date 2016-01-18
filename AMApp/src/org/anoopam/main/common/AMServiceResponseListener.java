package org.anoopam.main.common;

import org.anoopam.ext.smart.weservice.SmartWebManager;
import org.json.JSONObject;

/**
 * Created by dadesai on 1/1/16.
 */
public abstract class AMServiceResponseListener implements SmartWebManager.OnResponseReceivedListener<JSONObject> {
    abstract public void onSuccess(JSONObject response);
    abstract public void onFailure(String failureMessage);
}
