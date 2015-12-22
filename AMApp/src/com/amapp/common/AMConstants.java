package com.amapp.common;

/**
 * Created by dadesai on 12/7/15.
 */
public class AMConstants {

    //MOCK MOCKY
    public static final String MOCK_MOCKY_Domain_Url = "http://www.mocky.io";
    public static final String MOCK_MOCKY_HomeScreen_Endpoint_Suffix = "/v2/56721972400000c74ad62bb6?lastUpdatedTimestamp=%s";
    public static final String MOCK_MOCKY_ThakorjiToday_Endpoint_Suffix = "/v2/565320442400005027629a41?lastUpdatedTimestamp=%s";
    public static final String MOCK_MOCKY_QuoteOfTheWeek_Endpoint_Suffix = "/v2/5665890a250000602c996e79";
    public static final String MOCK_MOCKY_SahebjiDarshan_Endpoint_Suffix = "/v2/566c23a11100006c1ec6a010";
    public static final String MOCK_MOCKY_AnoopamAudio_Endpoint_Suffix = "/v2/56698805250000a0291bdf21";

    //MOCK APIARY
    public static final String MOCK_APIARY_Domain_Url = "http://www.mocky.io";
    public static final String MOCK_APIARY_ThakorjiToday_Endpoint_Suffix = "/api/ams/v1/thakorji.php?lastUpdatedTimestamp=%s";
    public static final String MOCK_APIARY_QuoteOfTheWeek_Endpoint_Suffix = "/v2/5665890a250000602c996e79";
    public static final String MOCK_APIARY_SahebjiDarshan_Endpoint_Suffix = "/v2/566c23a11100006c1ec6a010";
    public static final String MOCK_APIARY_AnoopamAudio_Endpoint_Suffix = "/v2/56698805250000a0291bdf21";

    //LIVE
    public static final String LIVE_Domain_Url = "http://anoopam.org";
    public static final String LIVE_ThakorjiToday_Endpoint_Suffix = "/api/ams/v1/thakorji.php?lastUpdatedTimestamp=%s&network=%s";
    public static final String LIVE_QuoteOfTheWeek_Endpoint_Suffix = "/v2/5665890a250000602c996e79";
    public static final String LIVE_SahebjiDarshan_Endpoint_Suffix = "/api/ams/v1/sahebji.php?lastUpdatedTimestamp=%s";
    public static final String LIVE_AnoopamAudio_Endpoint_Suffix = "/v2/56698805250000a0291bdf21";

    // other AMS constants
    public static final String AM_Application_Title = "Anoopam Mission";
    public static final String AMS_Request_Get_Temples_Tag = "ams_get_temples";
    public static final String AMS_Request_Get_Sahebji_Tag = "ams_get_sahebji";
    public static final String AMS_Request_Get_Audio_Cat_Tag = "ams_get_audio_cat";
    public static final String AMS_Request_Get_Audio_List_Tag = "ams_get_audio_list";
    public static final String AMS_Request_Get_Quotes_Tag = "ams_get_quotes";
    public static final String AMS_Request_Get_HomeScreen_List_Tag = "ams_get_home_screen_list";

    // AMS params
    public static final String AMS_RequestParam_ThakorjiToday_LastUpdatedTimestamp = "lastUpdatedTimestamp";
    public static final String AMS_RequestParam_SahebjiDarshan_LastUpdatedTimestamp = "lastUpdatedTimestamp";
    public static final String AMS_RequestParam_HomeScreen_LastUpdatedTimestamp = "lastUpdatedTimestamp";

    // KEYS
    public static final String KEY_ThakorjiTodayLastUpdatedTimestamp = "thakorjiTodayLastUpdatedTimestamp";
    public static final String KEY_SahebjiDarshanLastUpdatedTimestamp = "sahebjiDarshanLastUpdatedTimestamp";
    public static final String KEY_HomeScreenLastUpdatedTimestamp = "homeScreenLastUpdatedTimestamp";
}
