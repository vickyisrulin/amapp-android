package org.anoopam.main.sahebjidarshan;

import android.content.ContentValues;

import org.anoopam.main.AMAppMasterActivity;
import org.anoopam.main.AMApplication;
import org.anoopam.main.R;
import org.anoopam.main.common.TouchImageView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.androidquery.AQuery;
import org.anoopam.ext.smart.caching.SmartCaching;

import java.util.ArrayList;

public class SahebjiDarshanActivity extends AMAppMasterActivity {

    private TouchImageView mSahebjiDarshanImage;
    private SmartCaching mSmartCaching;


    private void setSahebjiDarshanImage() {
        AQuery aq =  AMApplication.getInstance().getAQuery();
        String imageUrl = getSahebjiDarshanUpdatedUrl();
        aq.id(mSahebjiDarshanImage).image(imageUrl, true, true, 0, 0);
    }

    private String getSahebjiDarshanUpdatedUrl() {
        ArrayList<ContentValues> sahebjiDarshanImages = mSmartCaching.getDataFromCache("sahebjiDarshanImages");
        if (sahebjiDarshanImages != null && sahebjiDarshanImages.size() > 0) {
            return sahebjiDarshanImages.get(0).getAsString("imageUrl");
        }
        return "";
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
        return R.layout.activity_sahebji_darshan;
    }

    @Override
    public void initComponents() {
        mSmartCaching = new SmartCaching(this);
        mSahebjiDarshanImage = (TouchImageView) findViewById(R.id.sahebji_darshan_image);
    }

    @Override
    public void prepareViews() {
        setSahebjiDarshanImage();
    }

    @Override
    public void setActionListeners() {

    }

    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar, ActionBarDrawerToggle actionBarDrawerToggle) {

    }
}
