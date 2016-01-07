package com.amapp.home;

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
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import com.amapp.AMAppMasterActivity;
import com.amapp.R;
import com.amapp.common.AMConstants;
import com.amapp.common.AMServiceRequest;
import com.amapp.common.events.EventBus;
import com.amapp.common.events.HomeTilesUpdateFailedEvent;
import com.amapp.common.events.HomeTilesUpdateSuccessEvent;
import com.smart.caching.SmartCaching;
import com.smart.framework.SmartUtils;
import com.smart.weservice.SmartWebManager;
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
        CONTACT_US,
        ABOUT;
    }

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

    }

    @Override
    public void initComponents() {
        super.initComponents();

        frmListFragmentContainer = (FrameLayout) findViewById(R.id.homeListFragmentContainer);
        mHomeListFragment = new HomeListFragment();
        smartCaching = new SmartCaching(this);
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
    protected void onResume() {
        super.onResume();
        selectDrawerItem(NAVIGATION_ITEMS.HOME);
        registerForEvents();
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

    @Override
    protected void onStop() {
        super.onStop();
        SmartWebManager.getInstance(this).getRequestQueue().cancelAll(AMConstants.AMS_Request_Get_Temples_Tag);
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

            case ABOUT:
                invokeAboutFlow();
                return;

            case CONTACT_US:
                invokeContactUsFlow();
                return;

            default:
                return;
        }
    }
}
