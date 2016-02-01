package org.anoopam.main;

import org.anoopam.main.common.AMServiceRequest;
import org.anoopam.ext.smart.framework.SmartApplication;

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
        AMServiceRequest.getInstance().startHomeScreenTilesUpdatesFromServer();
    }

    @Override
    public void onLowMemory(){
        super.onLowMemory();
        //note that you can configure the max image cache count, see CONFIGURATION
    }
}
