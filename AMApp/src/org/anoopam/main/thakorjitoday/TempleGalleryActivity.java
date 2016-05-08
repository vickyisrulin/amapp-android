/*
 * Copyright (c) 2016. Anoopam Mission. This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License at "http://www.gnu.org/licenses/licenses.html" for more details.
 *
 */

package org.anoopam.main.thakorjitoday;

import android.content.ContentValues;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;

import org.anoopam.ext.smart.caching.SmartCaching;
import org.anoopam.ext.smart.framework.Constants;
import org.anoopam.ext.smart.framework.SmartUtils;
import org.anoopam.main.AMAppMasterActivity;
import org.anoopam.main.R;
import org.anoopam.main.common.DataDownloadUtil;
import org.anoopam.main.common.ExtendedViewPager;
import org.anoopam.main.common.TouchImageView;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;


public class TempleGalleryActivity extends AMAppMasterActivity implements Constants {

    public static final String TEMPLE_DETAIL = "ALBUM_DETAIL";
    private ContentValues templeDetail;
    private ArrayList<ContentValues> templeImages;
    private ExtendedViewPager viewPager;
    private String destinationImageFileName;
    private String destinationImageFilePathPrefix;
    public static MenuItem itemDownload;
    public static MenuItem itemShare;
    boolean isImageAvailable = false;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu_home, menu);
        itemDownload = menu.findItem(R.id.action_download);
        itemShare = menu.findItem(R.id.action_share);

        itemDownload.setVisible(isImageAvailable);
        itemShare.setVisible(isImageAvailable);
        return true;
        }



    @Override
    public View getLayoutView() {
        return null;
    }

    @Override
    public int getLayoutID() {
        return R.layout.temple_gallery_activity;
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
    public void initComponents() {
        super.initComponents();
        disableSideMenu();
        viewPager= (ExtendedViewPager) findViewById(R.id.viewPager);
    }

    @Override
    public void prepareViews() {

        processIntent();
        viewPager.setAdapter(new ImagePageAdapter(getSupportFragmentManager()));
    }

    private void processIntent() {

        if(getIntent()!=null && getIntent().getExtras()!=null){

            if(getIntent().getExtras().get(TEMPLE_DETAIL)!=null){

                templeDetail = (ContentValues) getIntent().getExtras().get(TEMPLE_DETAIL);
                try {
                    templeImages =new SmartCaching(this).parseResponse(new JSONArray(templeDetail.getAsString("images")),"images").get("images");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
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
        toolbar.setSubtitle(templeDetail.getAsString("templePlace"));
    }


    public void setDownloadPath(int pos){
        destinationImageFileName = templeDetail.getAsString("templeID") +"_"+ URLUtil.guessFileName(templeImages.get(pos).getAsString("image"),null,null);
        destinationImageFilePathPrefix = SmartUtils.getAnoopamMissionDailyRefreshImageStorage()+File.separator;
    }

    public ViewPager getViewPager(){
        return viewPager;
    }



    public boolean onOptionsItemSelected(MenuItem item)  {
        switch (item.getItemId()) {
            case R.id.action_download:
                saveImageToGallery();
                return true;

            case R.id.action_share:
                DataDownloadUtil.shareImage(this, destinationImageFilePathPrefix, destinationImageFileName);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void saveImageToGallery() {
        DataDownloadUtil.saveImageToGallery(destinationImageFilePathPrefix, destinationImageFileName);
        SmartUtils.ting(this, "Downloaded Successfully");
    }


    private class ImagePageAdapter extends FragmentPagerAdapter {

        public ImagePageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {

            return new ImageFragment(templeImages.get(pos),pos);

        }

        @Override
        public int getCount() {
            return templeImages.size();
        }

    }

    public String getTempleID(){
        return templeDetail.getAsString("templeID");
    }

    private class PrivateOnLongClickListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View v) {
            toggleActionBarDisplay();
            return true;
        }
    }
}

