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
