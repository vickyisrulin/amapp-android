package com.amapp.qow;
import android.content.ContentValues;
import android.graphics.Typeface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.View;

import com.amapp.AMAppMasterActivity;
import com.amapp.AMApplication;
import com.amapp.R;
import com.amapp.common.TouchImageView;
import com.androidquery.AQuery;
import com.smart.caching.SmartCaching;

import java.util.ArrayList;

public class QuoteActivity extends AMAppMasterActivity {

    private TouchImageView mQuoteImage;
    private SmartCaching mSmartCaching;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mQuoteImage = (TouchImageView) findViewById(R.id.quote_image);
        setQuoteImage();
    }

    private void setQuoteImage() {
        AQuery aq =  AMApplication.getInstance().getAQuery();
        String imageUrl = getQuoteUpdatedUrl();
        aq.id(mQuoteImage).image(imageUrl, true, true, getDeviceWidth(), R.drawable.splash);
    }

    private String getQuoteUpdatedUrl() {
        ArrayList<ContentValues> quoteImages = mSmartCaching.getDataFromCache("qowImages");
        if (quoteImages != null && quoteImages.size() > 0) {
            return quoteImages.get(0).getAsString("imageUrl");
        }
        return "";
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public void preOnCreate() {

    }

    @Override
    public View getLayoutView() {
        return null;
    }

    @Override
    public int getLayoutID() {
        return R.layout.content_quote;
    }

    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar, ActionBarDrawerToggle actionBarDrawerToggle) {
        toolbar.setTitle(getString(R.string.app_name));
        SpannableString spannableString=new SpannableString(getString(R.string.app_subtitle));
        spannableString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spannableString.length(), 0);
        toolbar.setSubtitle(spannableString);
    }

    @Override
    public void initComponents() {
        super.initComponents();
        mSmartCaching = new SmartCaching(this);
    }

    @Override
    public void setActionListeners() {
    }
}

