/*
 * Copyright (c) 2016. Anoopam Mission. This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License at "http://www.gnu.org/licenses/licenses.html" for more details.
 *
 */

package org.anoopam.main;

import org.anoopam.main.common.AMConstants;

/**
 * Created by dadesai on 12/7/15.
 */
public enum Environment {

    ENV_MOCK_MOCKY(AMConstants.MOCK_MOCKY_Domain_Url,
            AMConstants.MOCK_MOCKY_SplashScreen_Endpoint_Suffix,
            AMConstants.MOCK_MOCKY_HomeScreen_Endpoint_Suffix,
            AMConstants.MOCK_MOCKY_ThakorjiToday_Endpoint_Suffix,
            AMConstants.MOCK_MOCKY_SahebjiDarshan_Endpoint_Suffix,
            AMConstants.MOCK_MOCKY_AnoopamAudio_Endpoint_Suffix,
            AMConstants.MOCK_MOCKY_QuoteOfTheWeek_Endpoint_Suffix,
            AMConstants.MOCK_MOCKY_News_Endpoint_Suffix
    ),

    ENV_MOCK_APIARY(AMConstants.MOCK_APIARY_Domain_Url,
            AMConstants.MOCK_MOCKY_SplashScreen_Endpoint_Suffix,
            AMConstants.MOCK_MOCKY_HomeScreen_Endpoint_Suffix,
            AMConstants.MOCK_APIARY_ThakorjiToday_Endpoint_Suffix,
            AMConstants.MOCK_APIARY_SahebjiDarshan_Endpoint_Suffix,
            AMConstants.MOCK_APIARY_AnoopamAudio_Endpoint_Suffix,
            AMConstants.MOCK_APIARY_QuoteOfTheWeek_Endpoint_Suffix,
            AMConstants.MOCK_MOCKY_News_Endpoint_Suffix
    ),

    ENV_LIVE(AMConstants.LIVE_Domain_Url,
            AMConstants.LIVE_SplashScreen_Endpoint_Suffix,
            AMConstants.LIVE_HomeScreen_Endpoint_Suffix,
            AMConstants.LIVE_ThakorjiToday_Endpoint_Suffix,
            AMConstants.LIVE_SahebjiDarshan_Endpoint_Suffix,
            AMConstants.LIVE_AnoopamAudio_Endpoint_Suffix,
            AMConstants.LIVE_QuoteOfTheWeek_Endpoint_Suffix,
            AMConstants.LIVE_News_Endpoint_Suffix
    );

    private String mDomainUrl;
    private String mSplashScreenEndpoint;
    private String mHomeTilesEndpoint;
    private String mThakorjiTodayEndpoint;
    private String mSahebjiDarshanEndpiont;
    private String mAnoopamAudioEndpoint;
    private String mQuoteOfTheWeekEndpoint;
    private String mNewsEndpoint;


    Environment(String domainUrl, String splashScreenEndpointSuffix, String homeTilesEndpointSuffix, String thakorjiTodayEndpointSuffix, String sahebjiDarshanEndpointSuffix,
                String anoopamAudioEndpointSuffix, String quoteOfTheWeekEndpointSuffix, String newsEndpointSuffix) {
        mDomainUrl = domainUrl;
        mSplashScreenEndpoint = domainUrl + splashScreenEndpointSuffix;
        mHomeTilesEndpoint = domainUrl + homeTilesEndpointSuffix;
        mThakorjiTodayEndpoint = domainUrl+thakorjiTodayEndpointSuffix;
        mSahebjiDarshanEndpiont = domainUrl+sahebjiDarshanEndpointSuffix;
        mAnoopamAudioEndpoint = domainUrl+anoopamAudioEndpointSuffix;
        mQuoteOfTheWeekEndpoint = domainUrl+quoteOfTheWeekEndpointSuffix;
        mNewsEndpoint = domainUrl + newsEndpointSuffix;
    }

    /**
     * Returns the BASE URL of the Server Domain
     * @return String
     */
    public String getDomainUrl() {
        return mDomainUrl;
    }

    /**
     * returns the Splash Screen Update Endpoint
     * @return String
     */
    public String getSplashScreenEndpoint() {
        return mSplashScreenEndpoint;
    }

    /**
     * returns the Home Tiles Endpoint
     * @return String
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
     * returns the Anoopam Audio Categories endpoint
     * @return String
     */
    public String getAnoopamAudioEndpoint() { return mAnoopamAudioEndpoint;}

    /**
     * returns the Quote of the Day Endpoint
     * @return String
     */
    public String getQuoteOfTheWeekEndpoint() {
        return mQuoteOfTheWeekEndpoint;
    }

    /**
     * returns the News Endpoint
     * @return String
     */
    public String getNewsEndpoint() {
        return mNewsEndpoint;
    }
}
