package com.amapp.qow;

import android.content.ContentValues;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import com.amapp.AMApplication;
import com.amapp.R;
import com.amapp.common.TouchImageView;
import com.androidquery.AQuery;
import com.smart.caching.SmartCaching;

import java.util.ArrayList;

public class QuoteActivity extends FragmentActivity {

    private TouchImageView mQuoteImage;
    private SmartCaching mSmartCaching;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_quote);
        mSmartCaching = new SmartCaching(this);
        mQuoteImage = (TouchImageView) findViewById(R.id.quote_image);
        setQuoteImage();
    }

    private void setQuoteImage() {
        AQuery aq =  AMApplication.getInstance().getAQuery();
        String imageUrl = getQuoteUpdatedUrl();
        aq.id(mQuoteImage).image(imageUrl, true, true, 0, R.drawable.splash);
    }

    private String getQuoteUpdatedUrl() {
        ArrayList<ContentValues> quoteImages = mSmartCaching.getDataFromCache("qowImages");
        if (quoteImages != null && quoteImages.size() > 0) {
            return quoteImages.get(0).getAsString("imageUrl");
        }
        return "";
    }
}

