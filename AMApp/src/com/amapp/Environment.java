package com.amapp;

import com.amapp.common.AMConstants;

/**
 * Created by dadesai on 12/7/15.
 */
public enum Environment {

    ENV_MOCK_MOCKY(AMConstants.MOCK_MOCKY_Domain_Url,
            AMConstants.MOCK_MOCKY_HomeScreen_Endpoint_Suffix,
            AMConstants.MOCK_MOCKY_ThakorjiToday_Endpoint_Suffix,
            AMConstants.MOCK_MOCKY_SahebjiDarshan_Endpoint_Suffix,
            AMConstants.MOCK_MOCKY_AnoopamAudio_Endpoint_Suffix,
            AMConstants.MOCK_MOCKY_QuoteOfTheWeek_Endpoint_Suffix
            ),

    ENV_MOCK_APIARY(AMConstants.MOCK_APIARY_Domain_Url,
            AMConstants.MOCK_MOCKY_HomeScreen_Endpoint_Suffix,
            AMConstants.MOCK_APIARY_ThakorjiToday_Endpoint_Suffix,
            AMConstants.MOCK_APIARY_SahebjiDarshan_Endpoint_Suffix,
            AMConstants.MOCK_APIARY_AnoopamAudio_Endpoint_Suffix,
            AMConstants.MOCK_APIARY_QuoteOfTheWeek_Endpoint_Suffix
    ),

    ENV_LIVE(AMConstants.LIVE_Domain_Url,
            AMConstants.LIVE_HomeScreen_Endpoint_Suffix,
            AMConstants.LIVE_ThakorjiToday_Endpoint_Suffix,
            AMConstants.LIVE_SahebjiDarshan_Endpoint_Suffix,
            AMConstants.LIVE_AnoopamAudio_Endpoint_Suffix,
            AMConstants.LIVE_QuoteOfTheWeek_Endpoint_Suffix
    );

    private String mDomainUrl;
    private String mHomeTilesEndpoint;
    private String mThakorjiTodayEndpoint;
    private String mSahebjiDarshanEndpiont;
    private String mAnoopamAudioEndpoint;
    private String mQuoteOfTheDayEndpoint;

    Environment(String domainUrl, String homeTilesEndpointSuffix, String thakorjiTodayEndpointSuffix, String sahebjiDarshanEndpointSuffix,
                String anoopamAudioEndpointSuffix, String quoteOfTheDayEndpointSuffix) {
        mDomainUrl = domainUrl;
        mHomeTilesEndpoint = domainUrl + homeTilesEndpointSuffix;
        mThakorjiTodayEndpoint = domainUrl+thakorjiTodayEndpointSuffix;
        mSahebjiDarshanEndpiont = domainUrl+sahebjiDarshanEndpointSuffix;
        mAnoopamAudioEndpoint = domainUrl+anoopamAudioEndpointSuffix;
        mQuoteOfTheDayEndpoint = domainUrl+quoteOfTheDayEndpointSuffix;
    }

    /**
     * Returns the BASE URL of the Server Domain
     * @return String
     */
    public String getDomainUrl() {
        return mDomainUrl;
    }

    /**
     * returns the Home Tiles Endpoint
     * @return
     */
    public String getHomeTilesEndpoint() {
        return mHomeTilesEndpoint;
    }

    /**
     * returns the Thakorji Today Endpoint
     * @return String
     */
    public String getThakorjiTodayEndpoint() {
        return mThakorjiTodayEndpoint;
    }

    /**
     * returns the Sahebji Darshan Endpoint
     * @return String
     */
    public String getSahebjiDarshanEndpiont() { return mSahebjiDarshanEndpiont;}

    /**
     * returns the Anoopam Audio endpoint
     * @return String
     */
    public String getAnoopamAudioEndpoint() { return mAnoopamAudioEndpoint;}

    /**
     * returns the Quote of the Day Endpoint
     * @return String
     */
    public String getQuoteOfTheDayEndpoint() {
        return mQuoteOfTheDayEndpoint;
    }
}
