package com.amapp;

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

import com.smart.caching.SmartCaching;
import com.smart.framework.Constants;
import com.smart.framework.SmartApplication;
import com.smart.framework.SmartUtils;
import com.smart.weservice.SmartWebManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

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
        templeListFragment = new TempleListFragment();
        smartCaching = new SmartCaching(this);
    }

    @Override
    public void prepareViews() {
        getSupportFragmentManager().beginTransaction().add(R.id.frmListFragmentContainer, templeListFragment).commit();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getCachedTemples();
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
        toolbar.setTitle(SmartApplication.REF_SMART_APPLICATION.APP_NAME);
        SpannableString spannableString=new SpannableString(getString(R.string.app_subtitle));
        spannableString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spannableString.length(), 0);
        toolbar.setSubtitle(spannableString);
    }











    @Override
    public boolean shouldKeyboardHideOnOutsideTouch() {
        return false;
    }

    private void getTemples() {

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT,this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, null);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG,Constants.WEB_GET_TEMPLES);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, SmartApplication.REF_SMART_APPLICATION.DOMAIN_NAME);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, String errorMessage) {

                if (errorMessage != null && errorMessage.equalsIgnoreCase(getString(R.string.no_content_found))) {
                    SmartUtils.showSnackBar(TempleListActivity.this, getString(R.string.no_gym_found), Snackbar.LENGTH_LONG);
                } else {

                    try {
                        smartCaching.cacheResponse(response.getJSONArray("temples"), "temples", true, new SmartCaching.OnResponseParsedListener() {
                            @Override
                            public void onParsed(HashMap<String, ArrayList<ContentValues>> mapTableNameAndData) {
                                temples = mapTableNameAndData.get("temples");
                                setTempleDataInFragments(temples, isCachedDataDisplayed);
                            }
                        }, "images");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }
        });

        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueue(requestParams,null, !isCachedDataDisplayed);

    }



    @Override
    protected void onStop() {
        super.onStop();

        SmartWebManager.getInstance(this).getRequestQueue().cancelAll(Constants.WEB_GET_TEMPLES);
    }

    private void getCachedTemples() {

        ArrayList<ContentValues> temples = new SmartCaching(this).getDataFromCache("temples");

        if (temples == null || temples.size() <= 0) {
            isCachedDataDisplayed = false;
            getTemples();
        } else {
            this.temples = temples;
            setTempleDataInFragments(temples, isCachedDataDisplayed);
            isCachedDataDisplayed = true;
        }
    }


    private void setTempleDataInFragments(ArrayList<ContentValues> temples, boolean isCachedDataDisplayed) {

        templeListFragment.setTemples(temples, isCachedDataDisplayed);

    }

}