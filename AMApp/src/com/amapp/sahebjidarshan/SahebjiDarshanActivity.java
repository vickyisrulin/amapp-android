package com.amapp.sahebjidarshan;

import android.content.ContentValues;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import com.amapp.AMApplication;
import com.amapp.R;
import com.amapp.common.TouchImageView;
import com.androidquery.AQuery;
import com.smart.caching.SmartCaching;

import java.util.ArrayList;

public class SahebjiDarshanActivity extends FragmentActivity {

    private TouchImageView mSahebjiDarshanImage;
    private SmartCaching mSmartCaching;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSmartCaching = new SmartCaching(this);
        setContentView(R.layout.activity_sahebji_darshan);
        mSahebjiDarshanImage = (TouchImageView) findViewById(R.id.sahebji_darshan_image);
        setSahebjiDarshanImage();
    }

    private void setSahebjiDarshanImage() {
        AQuery aq =  AMApplication.getInstance().getAQuery();
        String imageUrl = getSahebjiDarshanUpdatedUrl();
        aq.id(mSahebjiDarshanImage).image(imageUrl, true, true, 0, R.drawable.splash);
    }

    private String getSahebjiDarshanUpdatedUrl() {
        ArrayList<ContentValues> sahebjiDarshanImages = mSmartCaching.getDataFromCache("sahebjiDarshanImages");
        if (sahebjiDarshanImages != null && sahebjiDarshanImages.size() > 0) {
            return sahebjiDarshanImages.get(0).getAsString("imageUrl");
        }
        return "";
    }
}
