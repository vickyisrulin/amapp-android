/*
 * Copyright (c) 2016. Anoopam Mission. This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License at "http://www.gnu.org/licenses/licenses.html" for more details.
 *
 */

package org.anoopam.main;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
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
        Fabric.with(this, new Crashlytics());
        mEnvironment = Environment.ENV_LIVE;
        AMServiceRequest.getInstance().startHomeScreenTilesUpdatesFromServer();
    }

    @Override
    public void onLowMemory(){
        super.onLowMemory();
        //note that you can configure the max image cache count, see CONFIGURATION
    }
}
