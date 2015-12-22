package com.amapp.common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * Created by dadesai on 12/21/15.
 */
public class NetworkConnectionInfo {

    /**
     * returns the Telephony network type
     * @param context
     * @return int
     */
    private static int getTelephonyNetworkType(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getNetworkType();
    }

    /**
     * get the object of Current NetworkInfo
     * @param context
     * @return instance of NetworkInfo
     */
    private static NetworkInfo getCurrentNetworkInfo(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo();
    }

    /**
     * returns true if there is network connectivity
     * @param context
     * @return boolean
     */
    private static boolean isConnected(Context context){
        NetworkInfo networkInfo = getCurrentNetworkInfo(context);
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * returns true if it's Wifi connection
     * @param context
     * @return boolean
     */
    public static boolean isWifiConnected(Context context){
        NetworkInfo networkInfo = getCurrentNetworkInfo(context);
        return (networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI);
    }

    /**
     * returns true if it's Mobile DATA connection
     * @param context
     * @return boolean
     */
    public static boolean isMobileDataConnected(Context context){
        NetworkInfo networkInfo = getCurrentNetworkInfo(context);
        return (networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE);
    }
}
