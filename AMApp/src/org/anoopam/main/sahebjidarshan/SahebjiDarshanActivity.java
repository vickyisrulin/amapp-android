/*
 * Copyright (c) 2016. Anoopam Mission. This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License at "http://www.gnu.org/licenses/licenses.html" for more details.
 *
 */

package org.anoopam.main.sahebjidarshan;

import android.content.ContentValues;

import org.anoopam.ext.smart.customviews.Log;
import org.anoopam.ext.smart.framework.SmartUtils;
import org.anoopam.main.AMAppMasterActivity;
import org.anoopam.main.R;
import org.anoopam.main.common.DataDownloadUtil;
import org.anoopam.main.common.TouchImageView;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;

import org.anoopam.ext.smart.caching.SmartCaching;
import org.anoopam.main.common.crashlytics.CrashlyticsUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class SahebjiDarshanActivity extends AMAppMasterActivity {

    private TouchImageView mSahebjiDarshanImage;
    private SmartCaching mSmartCaching;
    private String downloadFileName;
    private String downloadFilePath;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu_home, menu);
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
        selectDrawerItem(NAVIGATION_ITEMS.SAHEBJI_DARSHAN);
    }

    private void setSahebjiDarshanImage() {
        String imageUrl = getSahebjiDarshanUpdatedUrl();
        downloadFileName= URLUtil.guessFileName(imageUrl, null, null);
        downloadFilePath = SmartUtils.getAnoopamMissionDailyRefreshImageStorage() + File.separator;

        final File destination = new File(SmartUtils.getAnoopamMissionDailyRefreshImageStorage() + File.separator + URLUtil.guessFileName(imageUrl, null, null));
        Uri downloadUri = Uri.parse(imageUrl.replaceAll(" ", "%20"));
        DataDownloadUtil.downloadImageFromServerAndRender(downloadUri, destination, mSahebjiDarshanImage);
    }

    private String getSahebjiDarshanUpdatedUrl() {
        ArrayList<ContentValues> sahebjiDarshanImages = mSmartCaching.getDataFromCache("sahebjiDarshanImages");
        if (sahebjiDarshanImages != null && sahebjiDarshanImages.size() > 0) {
            return sahebjiDarshanImages.get(0).getAsString("imageUrl");
        }
        return "";
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
    public View getLayoutView() {
        return null;
    }

    @Override
    public int getLayoutID() {
        return R.layout.activity_sahebji_darshan;
    }

    @Override
    public void initComponents() {
        super.initComponents();
        disableSideMenu();
        mSmartCaching = new SmartCaching(this);
        mSahebjiDarshanImage = (TouchImageView) findViewById(R.id.sahebji_darshan_image);
        mSahebjiDarshanImage.setOnLongClickListener(new PrivateOnLongClickListener());
        // hide the Action Bar for the first time
        toggleActionBarDisplay();
        CrashlyticsUtils.crashlyticsLog("Sahebji Darshan Init");
    }

    @Override
    public void prepareViews() {
        setSahebjiDarshanImage();
    }

    @Override
    public void setActionListeners() {

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
        toolbar.setTitle(getString(R.string.nav_sahebji_Darshan));
    }

    private class PrivateOnLongClickListener implements View.OnLongClickListener {
        @Override
        public boolean onLongClick(View v) {
            toggleActionBarDisplay();
            return true;
        }
    }

    public boolean onOptionsItemSelected(MenuItem item)  {
        switch (item.getItemId()) {
            case R.id.action_download:
                saveImageToGallery();
                return true;

            case R.id.action_share:
                shareImage();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
    protected void saveImageToGallery() {
        String fromDir = downloadFilePath;
        String toDir = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"AnoopamMission"+File.separator;
        File checkfile = new File(fromDir+downloadFileName);
        if (checkfile.isFile()) {
            SmartUtils.copyFile(fromDir, downloadFileName, toDir);
        }
        SmartUtils.ting(this, "Downloaded Successfully");
        //  MediaScannerConnection mediaScanner =  new MediaScannerConnection(getApplicationContext(),null);
        //  mediaScanner.scanFile(toDir+File.separator+downloadFileName, null);
    }

    protected void shareImage () {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("image/*");
        String combinedImagePath = downloadFilePath+downloadFileName;
        try {
            String path = MediaStore.Images.Media.insertImage(getContentResolver(), combinedImagePath, downloadFileName, null);
            Uri imageUri = Uri.parse(path);
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        } catch (Exception e)
        {
            Log.e("tag", e.getMessage());
        }

    }



}
