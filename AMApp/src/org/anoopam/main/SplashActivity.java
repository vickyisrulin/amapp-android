/*
 * Copyright (c) 2016. Anoopam Mission. This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License at "http://www.gnu.org/licenses/licenses.html" for more details.
 *
 */

package org.anoopam.main;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.anoopam.ext.smart.caching.SmartCaching;
import org.anoopam.ext.smart.framework.SmartUtils;
import org.anoopam.main.common.DataDownloadUtil;
import org.anoopam.main.common.crashlytics.CrashlyticsUtils;
import org.anoopam.main.home.HomeListActivity;

import java.io.File;
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
        splashImage.setClickable(true);
        splashImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("http://www.anoopam.org/amapp/misc/splash-redirect.php"));
                startActivity(intent);
            }
        });
        setSplashScreenImage();
        CrashlyticsUtils.crashlyticsLog("Started Splash Screen");
    }

    private void setSplashScreenImage() {
        String imageUrl = getSplashScreenUpdatedUrl();

        if(imageUrl==null || imageUrl.length()<=0){
            return;
        }

        final File destination = new File(SmartUtils.getAnoopamMissionImageStorage()+ File.separator +URLUtil.guessFileName(imageUrl, null, null));
        Uri downloadUri = Uri.parse(imageUrl.replaceAll(" ", "%20"));
        DataDownloadUtil.downloadImageFromServerAndRender(downloadUri, destination, splashImage);
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
