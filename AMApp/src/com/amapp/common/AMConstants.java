package com.amapp.common;

/**
 * Created by dadesai on 12/7/15.
 */
public class AMConstants {
    //MOCK
    public static final String MOCK_Domain_Url = "http://www.mocky.io";
    public static final String MOCK_ThakorjiToday_Endpoint_Suffix = "/v2/565320442400005027629a41";
    public static final String MOCK_QuoteOfTheWeek_Endpoint_Suffix = "/v2/5665890a250000602c996e79";

    //LIVE
    public static final String LIVE_Domain_Url = "http://anoopam.org";
    public static final String LIVE_ThakorjiToday_Endpoint_Suffix = "/api/ams/v1/thakorji.php";
    public static final String LIVE_QuoteOfTheWeek_Endpoint_Suffix = "/v2/5665890a250000602c996e79";

    // other AMS constants
    public static final String AM_Application_Title = "Anoopam Mission";
    public static final String AMS_Request_Get_Temples_Tag = "ams_get_temples";
    public static final String AMS_Request_Get_Quotes_Tag = "ams_get_quotes";

    // AMS params
    public static final String AMS_RequestParam_ThakorjiToday_LastUpdatedTimestamp = "lastUpdatedTimestamp";

    // KEYS
    public static final String KEY_ThakorjiTodayLastUpdatedTimestamp = "thakorjiTodayLastUpdatedTimestamp";
}
