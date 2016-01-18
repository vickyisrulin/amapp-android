package org.anoopam.main;

import org.anoopam.main.common.AMServiceRequest;
import com.androidquery.callback.BitmapAjaxCallback;
import org.smart.framework.SmartApplication;

/**
 * Created by dadesai on 12/23/15.
 */
public class AMApplication extends SmartApplication {

    // change this to MOCK or LIVE to change all service calls
    private Environment mEnvironment;

    /**
     * returns the Environment
     * @return Environment
     */
    public Environment getEnv() {
        return mEnvironment;
    }

    public static AMApplication getInstance() {
        return (AMApplication) REF_SMART_APPLICATION;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mEnvironment = Environment.ENV_LIVE;
        //TODO: Optimize these calls to get the data in one server request
        AMServiceRequest.getInstance().startThakorjiTodayUpdatesFromServer();
        AMServiceRequest.getInstance().startSahebjiDarshanUpdatesFromServer();
        AMServiceRequest.getInstance().startHomeScreenTilesUpdatesFromServer();
        AMServiceRequest.getInstance().startFetchingNewSplashScreenFromServer();
        AMServiceRequest.getInstance().startQuoteOfTheWeekUpdatesFromServer();
        AMServiceRequest.getInstance().startNewsUpdatesFromServer();
    }

    @Override
    public void onLowMemory(){
        super.onLowMemory();
        //clear all memory cached images when system is in low memory
        //note that you can configure the max image cache count, see CONFIGURATION
        BitmapAjaxCallback.clearCache();
    }
}
