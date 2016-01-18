package org.anoopam.main.qow;

import android.content.ContentValues;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import org.anoopam.main.AMApplication;
import org.anoopam.main.R;
import org.anoopam.main.common.TouchImageView;
import com.androidquery.AQuery;
import org.anoopam.ext.smart.caching.SmartCaching;

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
        aq.id(mQuoteImage).image(imageUrl, true, true, 0, 0);
    }

    private String getQuoteUpdatedUrl() {
        ArrayList<ContentValues> quoteImages = mSmartCaching.getDataFromCache("qowImages");
        if (quoteImages != null && quoteImages.size() > 0) {
            return quoteImages.get(0).getAsString("imageUrl");
        }
        return "";
    }
}

