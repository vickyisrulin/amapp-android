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
import com.smart.caching.SmartCaching;
import com.smart.framework.SmartApplication;
import com.smart.framework.SmartUtils;
import com.smart.weservice.SmartWebManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

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
        ABOUT,
        CONTACT_US;
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

        frmListFragmentContainer = (FrameLayout) findViewById(R.id.frmListFragmentContainer);
        mHomeListFragment = new HomeListFragment();
        smartCaching = new SmartCaching(this);
    }

    @Override
    public void prepareViews() {
        getSupportFragmentManager().beginTransaction().add(R.id.frmListFragmentContainer, mHomeListFragment).commit();
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

    @Override
    protected void onStop() {
        super.onStop();

        SmartWebManager.getInstance(this).getRequestQueue().cancelAll(AMConstants.AMS_Request_Get_Temples_Tag);
    }

    private void getHomeScreenTilesFromServer() {

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT,this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, null);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, AMConstants.AMS_Request_Get_HomeScreen_List_Tag);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getHomeScreenUrlWithLatestCachedTimestamp());
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_METHOD, SmartWebManager.REQUEST_TYPE.GET);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, String errorMessage) {

                if (errorMessage != null && errorMessage.equalsIgnoreCase(getString(R.string.no_content_found))) {
                    SmartUtils.showSnackBar(HomeListActivity.this, getString(R.string.no_gym_found), Snackbar.LENGTH_LONG);
                } else {
                    try {
                        smartCaching.cacheResponse(response.getJSONArray("homeTiles"), "homeTiles", true, new SmartCaching.OnResponseParsedListener() {
                            @Override
                            public void onParsed(HashMap<String, ArrayList<ContentValues>> mapTableNameAndData) {
                                tiles = mapTableNameAndData.get("homeTiles");
                                setTempleDataInFragments(tiles, isCachedDataDisplayed);
                            }
                        }, /*runOnMainThread*/ true, "homeTiles");
                        SmartApplication.REF_SMART_APPLICATION
                                .writeSharedPreferences(AMConstants.KEY_HomeScreenLastUpdatedTimestamp, response
                                        .getString(AMConstants.AMS_RequestParam_HomeScreen_LastUpdatedTimestamp));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueue(requestParams, null, !isCachedDataDisplayed);
    }

    private void getHomeScreenTiles() {
        ArrayList<ContentValues> tiles = new SmartCaching(this).getDataFromCache("homeTiles");

        if (tiles == null || tiles.size() <= 0) {
            isCachedDataDisplayed = false;
            getHomeScreenTilesFromServer();
        } else {
            this.tiles = tiles;
            setTempleDataInFragments(tiles, isCachedDataDisplayed);
            isCachedDataDisplayed = true;
            // after using cached data, check to see if there is an update
            // TODO: dont try to get it from server until we add LIVE PHP API
            // getHomeScreenTilesFromServer();
        }
    }

    // gets the latest timestamp cached on the client side
    // and addes it into the Home Screen endpoint as param
    private String getHomeScreenUrlWithLatestCachedTimestamp() {
        String endpoint = AMConstants.MOCK_MOCKY_Domain_Url+AMConstants.MOCK_MOCKY_HomeScreen_Endpoint_Suffix;
        String lastUpdatedTimeStamp = SmartApplication.REF_SMART_APPLICATION
                .readSharedPreferences().getString(AMConstants.KEY_HomeScreenLastUpdatedTimestamp, "");
        return String.format(endpoint, lastUpdatedTimeStamp);
    }

    private void setTempleDataInFragments(ArrayList<ContentValues> tiles, boolean isCachedDataDisplayed) {
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
                invokeQuoteOfTheDayFlow();
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
