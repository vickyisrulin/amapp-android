/*
 * Copyright (c) 2016. Anoopam Mission. This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License at "http://www.gnu.org/licenses/licenses.html" for more details.
 *
 */

package org.anoopam.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.github.tbouron.shakedetector.library.ShakeDetector;

import org.anoopam.ext.smart.framework.SharedPreferenceConstants;
import org.anoopam.ext.smart.framework.SmartActivity;
import org.anoopam.main.aboutapp.AboutAppActivity;
import org.anoopam.main.anoopamaudio.AudioCatListActivity;
import org.anoopam.main.common.AMConstants;
import org.anoopam.main.home.HomeListActivity;
import org.anoopam.main.news.NewsListActivity;
import org.anoopam.main.qow.QuoteActivity;
import org.anoopam.main.sahebjidarshan.SahebjiDarshanActivity;
import org.anoopam.main.thakorjitoday.TempleListActivity;
import org.anoopam.ext.smart.framework.Constants;

/**
 * Created by tasol on 23/6/15.
 */
public abstract class AMAppMasterActivity extends SmartActivity implements SharedPreferenceConstants, Constants {

    public static String MANAGE_UP_NAVIGATION = "manage_up_navigation";

    public enum NAVIGATION_ITEMS{HOME, THAKORJI_TODAY,SAHEBJI_DARSHAN,MANTRALEKHAN,QUOTE_OF_DAY,ANOOPAM_AUDIO,ABOUT,APP_FEEDBACK}
    private static final String AM_MANTRALEKHAN_APP_PACKANGE_NAME="com.web.anoopam";

    protected NavigationView navigationView;
    private ImageView imgLogoPic;
    private Bundle activityInvocationOptionsBunble;

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

        navigationView= (NavigationView) findViewById(R.id.navigationView);
        View headerLayout = navigationView.getHeaderView(0);
        imgLogoPic = (ImageView) headerLayout.findViewById(R.id.imgLogoPic);
        imgLogoPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invokePhilosophyFlow(v);
            }
        });
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public void prepareViews() {
    }

    @Override
    public void setActionListeners() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                closeDrawer();

                if(menuItem.isChecked()){
                    return true;
                }
                menuItem.setChecked(true);

                switch (menuItem.getItemId()) {
                    case R.id.navHome:
                        invokeHome();
                        return true;

                    case R.id.navThakorjiToday:
                        invokeThakorjiTodayFlow();
                        return true;

                    case R.id.navSahebjiDarshan:
                        invokeSahebjiDarshan();
                        return true;

                    case R.id.navMantralekhan:
                        gotoMantralekhanApp();
                        return true;

                    case R.id.navAudio:
                        invokeAudioFlow();
                        return true;

                    case R.id.navQuoteOfTheWeek:
                        invokeQuoteOfTheWeekFlow();
                        return true;

                    /*case R.id.navAnoopamEvents:
                        invokeEvents();
                        return true;*/

//                    case R.id.navAbout:
//                        invokeAboutFlow();
//                        return true;

//                    case R.id.navContactUs:
//                        invokeContactUsFlow();
//                        return true;

                    case R.id.navAbout:
                        invokeAboutAppFlow();
                        return true;

                    case R.id.navAppFeedback:
                        invokeFeedbackEmailIntent();
                        return true;

                    default:
                        invokeHome();
                        return true;
                }
            }
        });
    }

    protected void invokeHome() {
        Intent intent = new Intent(AMAppMasterActivity.this, HomeListActivity.class);
        activityInvocationOptionsBunble = ActivityOptionsCompat.makeSceneTransitionAnimation(AMAppMasterActivity.this).toBundle();
        ActivityCompat.startActivity(AMAppMasterActivity.this, intent, activityInvocationOptionsBunble);
    }

    protected void invokeThakorjiTodayFlow() {
        Intent intent = new Intent(AMAppMasterActivity.this, TempleListActivity.class);
        activityInvocationOptionsBunble = ActivityOptionsCompat.makeSceneTransitionAnimation(AMAppMasterActivity.this).toBundle();
        ActivityCompat.startActivity(AMAppMasterActivity.this, intent, activityInvocationOptionsBunble);
    }

    protected void invokeSahebjiDarshan() {
        Intent intent = new Intent(AMAppMasterActivity.this, SahebjiDarshanActivity.class);
        activityInvocationOptionsBunble = ActivityOptionsCompat.makeSceneTransitionAnimation(AMAppMasterActivity.this).toBundle();
        ActivityCompat.startActivity(AMAppMasterActivity.this, intent, null);
    }

    protected void invokeAudioFlow() {
        Intent intent = new Intent(AMAppMasterActivity.this, AudioCatListActivity.class);
        activityInvocationOptionsBunble = ActivityOptionsCompat.makeSceneTransitionAnimation(AMAppMasterActivity.this).toBundle();
        ActivityCompat.startActivity(AMAppMasterActivity.this, intent, activityInvocationOptionsBunble);
    }

    protected void invokeNewsUpdatesFlow() {
        Intent intent = new Intent(AMAppMasterActivity.this, NewsListActivity.class);
        ActivityCompat.startActivity(AMAppMasterActivity.this, intent, activityInvocationOptionsBunble);
    }

    protected void invokeQuoteOfTheWeekFlow() {
        Intent intent = new Intent(AMAppMasterActivity.this, QuoteActivity.class);
        activityInvocationOptionsBunble = ActivityOptionsCompat.makeSceneTransitionAnimation(AMAppMasterActivity.this).toBundle();
        ActivityCompat.startActivity(AMAppMasterActivity.this, intent, activityInvocationOptionsBunble);
    }

    protected void invokeAboutFlow() {
        invokeBrowserForThisUrl(AMConstants.URL_AboutUs);
    }

    protected void invokePhilosophyFlow(View V) {
        invokeBrowserForThisUrl(AMConstants.URL_Philosophy);
    }

    protected void invokeContactUsFlow() {
        invokeBrowserForThisUrl(AMConstants.URL_ContactUs);
    }

    protected void invokeAboutAppFlow() {
        Intent intent = new Intent(AMAppMasterActivity.this, AboutAppActivity.class);
        ActivityCompat.startActivity(AMAppMasterActivity.this, intent, null);
    }

    protected void invokeEvents() {
        invokeBrowserForThisUrl(AMConstants.URL_Events);
    }

    /**
     * Starts the browser for the given URL String
     * @param urlString
     */
    private void invokeBrowserForThisUrl(String urlString) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(urlString));
        startActivity(intent);
    }

    /**
     * 1) Invokes the Mantralekhan app if it's already available on the phone
     * 2) If not, takes the user to the App store to download the app
     */
    protected void gotoMantralekhanApp() {
        try {
            Intent mantralekhanAppIntent = getPackageManager().getLaunchIntentForPackage(AM_MANTRALEKHAN_APP_PACKANGE_NAME);
            ActivityCompat.startActivity(AMAppMasterActivity.this, mantralekhanAppIntent, activityInvocationOptionsBunble);
        } catch (Exception e) {
            try {
                ActivityCompat.startActivity(AMAppMasterActivity.this, new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + AM_MANTRALEKHAN_APP_PACKANGE_NAME)), activityInvocationOptionsBunble);
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

    /**
     *
     * @param context
     */
    public void activateShakeDetector(Context context) {
        ShakeDetector.create(context, new ShakeDetector.OnShakeListener() {
            @Override
            public void OnShake() {
                invokeFeedbackEmailIntent();
            }
        });
    }

    /**
     * invokes an intent to send Feedback email to AM
     */
    protected void invokeFeedbackEmailIntent() {
        String subject = AMConstants.CONST_FeedbackEmailSubject;
        String messageBody = AMConstants.CONST_FeedbackEmailBody;
        String toEmailAddress = AMConstants.CONST_FeedbackEmailAddress;
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", toEmailAddress, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, messageBody);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{toEmailAddress});
        startActivity(Intent.createChooser(emailIntent, "Send Feedback Email to Anoopam Mission..."));
    }

    /**
     * toggle the display of Action Bar
     */
    protected void toggleActionBarDisplay() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) return;
        if(actionBar.isShowing()) {
            actionBar.hide();
        } else {
            actionBar.show();
        }
    }
}
