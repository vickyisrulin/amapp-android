package org.anoopam.main.common.crashlytics;

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
}
