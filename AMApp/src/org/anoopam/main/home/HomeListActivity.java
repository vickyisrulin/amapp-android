/*
 * Copyright (c) 2016. Anoopam Mission. This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License at "http://www.gnu.org/licenses/licenses.html" for more details.
 *
 */

package org.anoopam.main.home;

import android.content.ContentValues;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import org.anoopam.main.AMAppMasterActivity;
import org.anoopam.main.R;
import org.anoopam.main.common.AMServiceRequest;
import org.anoopam.main.common.crashlytics.CrashlyticsUtils;
import org.anoopam.main.common.events.EventBus;
import org.anoopam.main.common.events.HomeTilesUpdateFailedEvent;
import org.anoopam.main.common.events.HomeTilesUpdateSuccessEvent;
import org.anoopam.ext.smart.caching.SmartCaching;
import org.anoopam.ext.smart.framework.SmartUtils;

import com.github.tbouron.shakedetector.library.ShakeDetector;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;

/**
 * Created by ddesai on 12/15/15.
 */
public class HomeListActivity extends AMAppMasterActivity {

    private static final String TAG = "HomeListActivity";

    private FrameLayout frmListFragmentContainer;

    private HomeListFragment mHomeListFragment;

    private ArrayList<ContentValues> tiles = new ArrayList<>();

    private SmartCaching smartCaching;

    private boolean isCachedDataDisplayed = false;

    public enum HomeTileIndex {
        THAKORJI_TODAY,
        SAHEBJI_DARSHAN,
        MANTRALEKHAN,
        QUOTE_OF_DAY,
        ANOOPAM_AUDIO,
        ANOOPAM_VIDEO;
    }

    private void registerForEvents() {
        EventBus.getInstance().register(this);
    }

    private void unregisterForEvents() {
        EventBus.getInstance().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectDrawerItem(NAVIGATION_ITEMS.HOME);
        registerForEvents();
        ShakeDetector.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        ShakeDetector.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ShakeDetector.destroy();
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
        return R.layout.home_list_activity;
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
        // disable feedback/response option on shaking
        //activateShakeDetector(this);
    }

    @Override
    public void initComponents() {
        super.initComponents();

        frmListFragmentContainer = (FrameLayout) findViewById(R.id.homeListFragmentContainer);
        mHomeListFragment = new HomeListFragment();
        smartCaching = new SmartCaching(this);
        //TODO: is this the best way/place for this?
        AMServiceRequest.getInstance().fetchUpdatedServerData();

        String deviceCountry = AMServiceRequest.getDeviceCountry();
        Log.d(TAG, "deviceCountry:  " + deviceCountry);
        CrashlyticsUtils.crashlyticsLog("Home Screen Init from Country: " + deviceCountry);
    }

    @Override
    public void prepareViews() {
        getSupportFragmentManager().beginTransaction().add(R.id.homeListFragmentContainer, mHomeListFragment).commit();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getHomeScreenTiles();
            }
        }, 500);
    }

    @Override
    public void setActionListeners() {
        super.setActionListeners();
    }

    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar, ActionBarDrawerToggle actionBarDrawerToggle) {
        toolbar.setTitle(getString(R.string.app_name));
        SpannableString spannableString=new SpannableString(getString(R.string.app_subtitle));
        spannableString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spannableString.length(), 0);
        toolbar.setSubtitle(spannableString);
    }

    @Override
    public boolean shouldKeyboardHideOnOutsideTouch() {
        return false;
    }

    @Subscribe
    public void onHomeScreenTilesUpdated(HomeTilesUpdateSuccessEvent event) {
        getHomeScreenTiles();
    }

    @Subscribe
    public void onHomeScreenTilesUpdated(HomeTilesUpdateFailedEvent event) {
        if(tiles == null || tiles.size() <= 0) {
            SmartUtils.showSnackBar(HomeListActivity.this, getString(R.string.generic_error), Snackbar.LENGTH_LONG);
        }
    }

    private void getHomeScreenTiles() {
        ArrayList<ContentValues> tiles = new SmartCaching(this).getDataFromCache("homeTiles");

        if (tiles == null || tiles.size() <= 0) {
            isCachedDataDisplayed = false;
            AMServiceRequest.getInstance().startHomeScreenTilesUpdatesFromServer();
        } else {
            this.tiles = tiles;
            setHomeTilesDataInFragments(tiles, isCachedDataDisplayed);
            isCachedDataDisplayed = true;
        }
    }

    private void setHomeTilesDataInFragments(ArrayList<ContentValues> tiles, boolean isCachedDataDisplayed) {
        mHomeListFragment.setHomeTiles(tiles, isCachedDataDisplayed);
    }

    public void handleTileClick(int tileId) {
        HomeTileIndex tileIndex = HomeTileIndex.values()[tileId];

        switch(tileIndex) {
            case THAKORJI_TODAY:
                invokeThakorjiTodayFlow();
                return;

            case SAHEBJI_DARSHAN:
                invokeSahebjiDarshan();
                return;

            case MANTRALEKHAN:
                gotoMantralekhanApp();
                return;

            case ANOOPAM_AUDIO:
                invokeAudioFlow();
                return;

            case QUOTE_OF_DAY:
                invokeQuoteOfTheWeekFlow();
                return;

            case ANOOPAM_VIDEO:
                invokeVideoFlow();
                return;

            default:
                return;
        }
    }
}
