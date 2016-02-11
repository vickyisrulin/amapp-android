/*
 * Copyright (c) 2016. Anoopam Mission. This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License at "http://www.gnu.org/licenses/licenses.html" for more details.
 *
 */

package org.anoopam.main.common.crashlytics;

import com.crashlytics.android.Crashlytics;

/**
 * Created by dadesai on 2/1/16.
 */
public class CrashlyticsUtils {

    /**
     * Force a crash
     */
    public static void forceCrash() {
        throw new RuntimeException("This is a Forced Crash for testing");
    }

    /***
     * Logs the message in Crashlytics
     * @param message
     */
    public static void crashlyticsLog(String message) {
        Crashlytics.log(message);
    }
}
