package com.amapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import com.amapp.thakorjitoday.TempleListActivity;
import com.android.volley.toolbox.NetworkImageView;
import com.smart.customviews.SmartTextView;
import com.smart.framework.Constants;
import com.smart.framework.SmartApplication;
import com.smart.framework.SmartSuperMaster;

/**
 * Created by tasol on 23/6/15.
 */
public abstract class AMAppMasterActivity extends SmartSuperMaster implements Constants{

    public enum NAVIGATION_ITEMS{HOME,HISTORY,PROMOTIONS,FREE_PASSES,RATE_US,CONTACT_US,LOGOUT}
    protected enum LOGIN_OPTIONS {LOCAL_SERVER, FACEBOOK, GOOGLE_PLUS}
    private static final String AM_MANTRALEKHAN_APP_PACKANGE_NAME="com.web.anoopam";

    protected NavigationView navigationView;
    private NetworkImageView profilePic;
    private SmartTextView profileName;
    private Bundle activityInvocationOptionsBunble;

    // change this to MOCK or LIVE to change all service calls
    protected Environment environment = Environment.ENV_MOCK_MOCKY;


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

        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setAllowEnterTransitionOverlap(true);
        getWindow().setAllowReturnTransitionOverlap(true);
    }

    @Override
    public void initComponents() {
        activityInvocationOptionsBunble = ActivityOptionsCompat.makeSceneTransitionAnimation(AMAppMasterActivity.this).toBundle();
        navigationView= (NavigationView) findViewById(R.id.navigationView);
        profilePic = (NetworkImageView) navigationView.findViewById(R.id.imgProfilePic);
        profileName= (SmartTextView) navigationView.findViewById(R.id.txtProfileName);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        /*if(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(USER_IMAGE,null)!=null){

            profilePic.setImageUrl(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(USER_IMAGE, ""), SmartWebManager.getInstance(this).getImageLoader());
        }

        if(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(USER_NAME,null)!=null){

            ((SmartTextView)navigationView.findViewById(R.id.txtProfileName)).setText(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(USER_NAME,""));

        }*/

        setLogoutIcon();

        String s= navigationView.getMenu().getItem(NAVIGATION_ITEMS.FREE_PASSES.ordinal()).getTitle().toString()+"              "+"5";
        int freePasses=Integer.parseInt(s.replaceAll("[\\D]", ""));
        SpannableString ss1=  new SpannableString(s);
        ss1.setSpan(new RelativeSizeSpan(1.3f), s.indexOf(String.valueOf(freePasses)), s.length(), 0); // set size
//        ss1.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.primary900)), s.indexOf(String.valueOf(freePasses)),s.length(), 0);// set color
        navigationView.getMenu().getItem(NAVIGATION_ITEMS.FREE_PASSES.ordinal()).setTitle(ss1);
    }

    @Override
    public void prepareViews() {
    }

    private void setLogoutIcon() {

        int loggedInWith=SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getInt(LOGGED_IN_WITH,-1);

        if(loggedInWith==LOGIN_OPTIONS.LOCAL_SERVER.ordinal()) {


        }else if(loggedInWith==LOGIN_OPTIONS.FACEBOOK.ordinal()) {

            navigationView.getMenu().getItem(NAVIGATION_ITEMS.LOGOUT.ordinal()).setIcon(R.drawable.ic_facebook_box_white_24dp);
        }else if(loggedInWith==LOGIN_OPTIONS.GOOGLE_PLUS.ordinal()) {

            navigationView.getMenu().getItem(NAVIGATION_ITEMS.LOGOUT.ordinal()).setIcon(R.drawable.ic_google_plus_white_24dp);
        }
    }

    @Override
    public void setActionListeners() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                closeDrawer();
                menuItem.setChecked(true);

                switch (menuItem.getItemId()) {
                    case R.id.navHome:
                        invokeHome();
                        return true;

                    case R.id.navMantralekhan:
                        gotoMantralekhanApp();
                        return true;

                    case R.id.navAudio:
                        invokeAudioFlow();
                        return true;

                    case R.id.navQuoteOfTheDay:
                        invokeQuoteOfTheDayFlow();
                        return true;

                    case R.id.navAbout:
                        invokeAboutFlow();
                        return true;

                    case R.id.navContactUs:
                        invokeContactUsFlow();
                        return true;

                    default:
                        return true;
                }
            }
        });
    }

    private void invokeHome() {
        Intent intent = new Intent(AMAppMasterActivity.this, TempleListActivity.class);
        ActivityCompat.startActivity(AMAppMasterActivity.this, intent, activityInvocationOptionsBunble);
    }

    private void invokeAudioFlow() {
    }

    private void invokeQuoteOfTheDayFlow() {
    }

    private void invokeAboutFlow() {
    }

    private void invokeContactUsFlow() {
    }

    /**
     * 1) Invokes the Mantralekhan app if it's already available on the phone
     * 2) If not, takes the user to the App store to download the app
     */
    private void gotoMantralekhanApp() {
        try {
            Intent mantralekhanAppIntent = getPackageManager().getLaunchIntentForPackage(AM_MANTRALEKHAN_APP_PACKANGE_NAME);
            ActivityCompat.startActivity(AMAppMasterActivity.this, mantralekhanAppIntent, activityInvocationOptionsBunble);
        } catch (Exception e) {
            try {
                ActivityCompat.startActivity(AMAppMasterActivity.this, new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + AM_MANTRALEKHAN_APP_PACKANGE_NAME)),activityInvocationOptionsBunble);
            } catch (android.content.ActivityNotFoundException anfe) {
                ActivityCompat.startActivity(AMAppMasterActivity.this, new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + AM_MANTRALEKHAN_APP_PACKANGE_NAME)), activityInvocationOptionsBunble);
            }
        }
    }

    @Override
    public void postOnCreate() {

    }

    @Override
    public boolean shouldKeyboardHideOnOutsideTouch() {
        return true;
    }

    @Override
    public int getDrawerLayoutID() {

        return R.layout.drawer;
    }

    protected void selectDrawerItem(NAVIGATION_ITEMS item) {

        navigationView.getMenu().getItem(item.ordinal()).setChecked(true);

    }
}
