package com.amapp;

import com.amapp.common.AMConstants;

/**
 * Created by dadesai on 12/7/15.
 */
public enum Environment {

    ENV_MOCK_MOCKY(AMConstants.MOCK_MOCKY_Domain_Url,
            AMConstants.MOCK_MOCKY_ThakorjiToday_Endpoint_Suffix,
            AMConstants.MOCK_MOCKY_QuoteOfTheWeek_Endpoint_Suffix
            ),

    ENV_MOCK_APIARY(AMConstants.MOCK_APIARY_Domain_Url,
            AMConstants.MOCK_APIARY_ThakorjiToday_Endpoint_Suffix,
            AMConstants.MOCK_APIARY_QuoteOfTheWeek_Endpoint_Suffix
    ),

    ENV_LIVE(AMConstants.LIVE_Domain_Url,
            AMConstants.LIVE_ThakorjiToday_Endpoint_Suffix,
            AMConstants.LIVE_QuoteOfTheWeek_Endpoint_Suffix
    );

    private String mDomainUrl;
    private String mThakorjiTodayEndpoint;
    private String mQuoteOfTheDayEndpoint;

    Environment(String domainUrl, String thakorjiTodayEndpointSuffix, String quoteOfTheDayEndpointSuffix) {
        mDomainUrl = domainUrl;
        mThakorjiTodayEndpoint = domainUrl+thakorjiTodayEndpointSuffix;
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
     * returns the Thakorji Today Endpoint
     * @return String
     */
    public String getThakorjiTodayEndpoint() {
        return mThakorjiTodayEndpoint;
    }

    /**
     * returns the Quote of the Day Endpoint
     * @return String
     */
    public String getQuoteOfTheDayEndpoint() {
        return mQuoteOfTheDayEndpoint;
    }
}
