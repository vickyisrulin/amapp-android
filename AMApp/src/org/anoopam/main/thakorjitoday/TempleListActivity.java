/*
 * Copyright (c) 2016. Anoopam Mission. This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License at "http://www.gnu.org/licenses/licenses.html" for more details.
 *
 */

package org.anoopam.main.thakorjitoday;

import android.content.ContentValues;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.squareup.otto.Subscribe;

import org.anoopam.ext.smart.caching.SmartCaching;
import org.anoopam.ext.smart.framework.SmartUtils;
import org.anoopam.main.AMAppMasterActivity;
import org.anoopam.main.R;
import org.anoopam.main.common.AMServiceRequest;
import org.anoopam.main.common.crashlytics.CrashlyticsUtils;
import org.anoopam.main.common.events.EventBus;
import org.anoopam.main.common.events.ThakorjiTodayUpdateFailedEvent;
import org.anoopam.main.common.events.ThakorjiTodayUpdateSuccessEvent;

import java.util.ArrayList;

/**
 * Created by tasol on 16/7/15.
 */

public class TempleListActivity extends AMAppMasterActivity {

    private static final String TAG = "TempleListActivity";
    private FrameLayout frmListFragmentContainer;
    private TempleListFragment templeListFragment;
    private ArrayList<ContentValues> temples = new ArrayList<>();
    private SmartCaching smartCaching;
    private boolean isCachedDataDisplayed = false;

    private void registerForEvents() {
        EventBus.getInstance().register(this);
    }

    private void unregisterForEvents() {
        EventBus.getInstance().unregister(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterForEvents();
    }

    @Override
    public View getLayoutView() {
        return null;
    }

    @Override
    public int getLayoutID() {
        return R.layout.temples_activity;
    }

    @Override
    public View getFooterLayoutView() {
        return null;
    }

    @Override
    public int getFooterLayoutID() {
        return 0;
    }

    @Override
    public View getHeaderLayoutView() {
        return null;
    }

    @Override
    public int getHeaderLayoutID() {
        return 0;
    }

    @Override
    public void setAnimations() {
        super.setAnimations();
        getWindow().setEnterTransition(new Slide(Gravity.RIGHT));
        getWindow().setReturnTransition(new Slide(Gravity.BOTTOM));
    }

    @Override
    public void preOnCreate() {

    }

    @Override
    public void initComponents() {
        super.initComponents();

        disableSideMenu();
        frmListFragmentContainer = (FrameLayout) findViewById(R.id.frmListFragmentContainer);
        templeListFragment = new TempleListFragment();
        smartCaching = new SmartCaching(this);
        CrashlyticsUtils.crashlyticsLog("Thakorji Today Temple List Init");
    }

    @Override
    public void prepareViews() {
        getSupportFragmentManager().beginTransaction().add(R.id.frmListFragmentContainer, templeListFragment).commit();
        getCachedTemples();
    }

    @Override
    public void setActionListeners() {
        super.setActionListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectDrawerItem(NAVIGATION_ITEMS.THAKORJI_TODAY);
        registerForEvents();
    }

    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar, ActionBarDrawerToggle actionBarDrawerToggle) {
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                supportFinishAfterTransition();

            }
        });
        toolbar.setTitle(getString(R.string.nav_thakorji_today_title));
        SpannableString spannableString=new SpannableString(getString(R.string.app_subtitle));
        spannableString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spannableString.length(), 0);
        toolbar.setSubtitle(spannableString);
    }

    @Override
    public boolean shouldKeyboardHideOnOutsideTouch() {
        return false;
    }

    @Subscribe
    public void onThakorjiTodayUpdated(ThakorjiTodayUpdateSuccessEvent event) {
        getCachedTemples();
    }

    @Subscribe
    public void onThakorjiTodayUpdated(ThakorjiTodayUpdateFailedEvent event) {
        if(temples == null || temples.size() <= 0) {
            SmartUtils.showSnackBar(TempleListActivity.this, getString(R.string.generic_error), Snackbar.LENGTH_LONG);
        }
    }

    private void getCachedTemples() {
        SmartUtils.showProgressDialog(this, "Loading...", false);
        AsyncTask<Void, Void, ArrayList<ContentValues>> task = new AsyncTask<Void, Void,ArrayList<ContentValues>>() {
            @Override
            protected ArrayList<ContentValues> doInBackground(Void... params) {
                ArrayList<ContentValues> temples = smartCaching.getDataFromCache("temples");
                return temples;
            }

            @Override
            protected void onPostExecute(ArrayList<ContentValues> result) {
                super.onPostExecute(result);
                SmartUtils.hideProgressDialog();
                if (result == null || result.size() <= 0) {
                    AMServiceRequest.getInstance().startThakorjiTodayUpdatesFromServer();
                    isCachedDataDisplayed = false;
                } else {
                    TempleListActivity.this.temples = result;
                    setTempleDataInFragments(temples, isCachedDataDisplayed);
                    isCachedDataDisplayed = true;
                }

            }
        };

        if(android.os.Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB){
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }else{
            task.execute();
        }


    }

    private void setTempleDataInFragments(ArrayList<ContentValues> temples, boolean isCachedDataDisplayed) {
        templeListFragment.setTemples(temples, isCachedDataDisplayed);
    }
}