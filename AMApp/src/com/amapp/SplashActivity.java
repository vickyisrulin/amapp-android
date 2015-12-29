package com.amapp;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.amapp.common.AMServiceRequest;
import com.amapp.home.HomeListActivity;
import com.androidquery.AQuery;
import com.smart.caching.SmartCaching;

import java.util.ArrayList;

/**
 * Created by tasol on 23/6/15.
 */

public class SplashActivity extends AMAppMasterActivity {

    Handler handler=new Handler();
    ImageView splashImage;
    FrameLayout mLayout;
    private SmartCaching mSmartCaching;

    @Override
    public View getLayoutView() {
        return null;
    }

    @Override
    public int getLayoutID() {
        return R.layout.splash_activity;
    }

    @Override
    public void setAnimations() {
        super.setAnimations();
        getWindow().setEnterTransition(new Fade());
        getWindow().setExitTransition(new Fade());
    }

    @Override
    public void preOnCreate() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLayout = (FrameLayout) findViewById(getLayoutID());
        splashImage = (ImageView) findViewById(R.id.splashscreen);
        setSplashScreenImage();
    }

    private void setSplashScreenImage() {
        AQuery aq =  AMApplication.getInstance().getAQuery();
        String imageUrl = getSplashScreenUpdatedUrl();
        aq.id(splashImage).image(imageUrl, true, true, getDeviceWidth(), R.drawable.splash);
    }

    private String getSplashScreenUpdatedUrl() {
        ArrayList<ContentValues> splashScreen = mSmartCaching.getDataFromCache("splashMessages");
        if (splashScreen != null && splashScreen.size() > 0) {
            return splashScreen.get(0).getAsString("imageUrl");
        }
        return "";
    }

    @Override
    public void initComponents() {
        mSmartCaching = new SmartCaching(this);
    }

    @Override
    public void prepareViews() {

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(SplashActivity.this);
                Intent intent = new Intent();
                intent.setClass(SplashActivity.this,HomeListActivity.class);
                ActivityCompat.startActivity(SplashActivity.this, intent, options.toBundle());
                finish();
            }
        },5000);
    }

    @Override
    public void setActionListeners() {
    }

    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar,ActionBarDrawerToggle actionBarDrawerToggle) {
        actionBar.hide();
    }

    @Override
    public int getDrawerLayoutID() {
        return 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
