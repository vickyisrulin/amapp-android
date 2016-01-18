package org.anoopam.main.sahebjidarshan;

import android.content.ContentValues;
import android.graphics.Typeface;
import android.os.Handler;
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
import org.anoopam.main.AMApplication;
import org.anoopam.main.R;
import org.anoopam.main.common.AMConstants;
import org.anoopam.main.common.AMServiceResponseListener;
import org.anoopam.ext.smart.caching.SmartCaching;
import org.anoopam.ext.smart.framework.SmartApplication;
import org.anoopam.ext.smart.weservice.SmartWebManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tasol on 16/7/15.
 */

public class SahebjiAlbumListActivity extends AMAppMasterActivity {

    private static final String TAG = "SahebjiAlbumActivity";

    private FrameLayout frmListFragmentContainer;

    private SahebjiAlbumListFragment albumListFragment;

    private ArrayList<ContentValues> albums = new ArrayList<>();

    private SmartCaching smartCaching;

    private boolean isCachedDataDisplayed = false;

    @Override
    public View getLayoutView() {
        return null;
    }

    @Override
    public int getLayoutID() {
        return R.layout.sahebji_album_activity;
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
        albumListFragment = new SahebjiAlbumListFragment();
        smartCaching = new SmartCaching(this);
    }

    @Override
    public void prepareViews() {
        getSupportFragmentManager().beginTransaction().add(R.id.frmListFragmentContainer, albumListFragment).commit();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getCachedAlbums();
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
        selectDrawerItem(NAVIGATION_ITEMS.SAHEBJI_DARSHAN);
    }
    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar, ActionBarDrawerToggle actionBarDrawerToggle) {
        toolbar.setTitle(getString(R.string.nav_sahebji_Darshan));
        SpannableString spannableString=new SpannableString(getString(R.string.app_subtitle));
        spannableString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spannableString.length(), 0);
        toolbar.setSubtitle(spannableString);
    }

    @Override
    public boolean shouldKeyboardHideOnOutsideTouch() {
        return false;
    }

    private void getAlbums() {

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT,this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, null);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, AMConstants.AMS_Request_Get_Sahebji_Tag);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getSahebjiDarshanUrlWithLatestCachedTimestamp());
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_METHOD, SmartWebManager.REQUEST_TYPE.GET);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new AMServiceResponseListener() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        try {
                            JSONArray albumsArray = response.getJSONArray("albums");
                            if(albumsArray != null) {
                                smartCaching.cacheResponse(albumsArray, "albums", true, new SmartCaching.OnResponseParsedListener() {
                                    @Override
                                    public void onParsed(HashMap<String, ArrayList<ContentValues>> mapTableNameAndData) {
                                        albums = mapTableNameAndData.get("albums");
                                        setAlbumDataInFragments(albums, isCachedDataDisplayed);
                                    }
                                }, /*runOnMainThread*/ true, "images");
                                SmartApplication.REF_SMART_APPLICATION
                                        .writeSharedPreferences(AMConstants.KEY_SahebjiDarshanLastUpdatedTimestamp, response
                                                .getString(AMConstants.AMS_RequestParam_SahebjiDarshan_LastUpdatedTimestamp));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(String failureMessage) {
                        Log.e(TAG, "Error obtaining Sahebji Album data: " + failureMessage);
                    }
                });
        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueue(requestParams, null, !isCachedDataDisplayed);
    }

    @Override
    protected void onStop() {
        super.onStop();

        SmartWebManager.getInstance(this).getRequestQueue().cancelAll(AMConstants.AMS_Request_Get_Sahebji_Tag);
    }

    private void getCachedAlbums() {

        ArrayList<ContentValues> albums = new SmartCaching(this).getDataFromCache("albums");

        if (albums == null || albums.size() <= 0) {
            isCachedDataDisplayed = false;
            getAlbums();
        } else {
            this.albums = albums;
            setAlbumDataInFragments(albums, isCachedDataDisplayed);
            isCachedDataDisplayed = true;
            // after using cached data, check to see if there is an update
            getAlbums();
        }
    }

    // gets the latest timestamp cached on the client side
    // and addes it into the SahebjiDarshan endpoint as param
    private String getSahebjiDarshanUrlWithLatestCachedTimestamp() {
        String endpoint = AMApplication.getInstance().getEnv().getSahebjiDarshanEndpiont();
        String lastUpdatedTimeStamp = SmartApplication.REF_SMART_APPLICATION
                .readSharedPreferences().getString(AMConstants.KEY_SahebjiDarshanLastUpdatedTimestamp, "");
        return String.format(endpoint, lastUpdatedTimeStamp);
    }

    private void setAlbumDataInFragments(ArrayList<ContentValues> albums, boolean isCachedDataDisplayed) {

        albumListFragment.setAlbums(albums, isCachedDataDisplayed);

    }

}