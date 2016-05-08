/*
 * Copyright (c) 2016. Anoopam Mission. This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License at "http://www.gnu.org/licenses/licenses.html" for more details.
 *
 */

package org.anoopam.main.qow;

import android.content.ContentValues;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.URLUtil;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;

import org.anoopam.ext.smart.caching.SmartCaching;
import org.anoopam.ext.smart.framework.SmartApplication;
import org.anoopam.ext.smart.framework.SmartUtils;
import org.anoopam.main.AMAppMasterActivity;
import org.anoopam.main.AMApplication;
import org.anoopam.main.R;
import org.anoopam.main.common.DataDownloadUtil;
import org.anoopam.main.common.TouchImageView;

import java.io.File;
import java.util.ArrayList;

public class QuoteActivity extends AMAppMasterActivity {

    private TouchImageView mQuoteImage;
    private SmartCaching mSmartCaching;
    private String destinationImageFileName;
    private String destinationImageFilePathPrefix;
    MenuItem itemDownload;
    MenuItem itemShare;
    boolean isImageAvailable = false;

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu_home, menu);
        itemDownload = menu.findItem(R.id.action_download);
        itemShare = menu.findItem(R.id.action_share);

        itemDownload.setVisible(isImageAvailable);
        itemShare.setVisible(isImageAvailable);
        return true;
    }

    private void setQuoteImage() {
        final String imageUrl = getQuoteUpdatedUrl();
        destinationImageFileName = URLUtil.guessFileName(imageUrl, null, null);
        destinationImageFilePathPrefix = SmartUtils.getAnoopamMissionImageStorage()+ File.separator;

        final File destination = new File(destinationImageFilePathPrefix + destinationImageFileName);
        Uri downloadUri = Uri.parse(imageUrl.replaceAll(" ", "%20"));
        int hasImage = SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getInt(imageUrl, 0);

        if(destination.exists()&& hasImage==1){
            isImageAvailable = true;
            Picasso.with(AMApplication.getInstance().getApplicationContext())
                    .load(destination)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_STORE)
                    .skipMemoryCache()
                    .into(mQuoteImage);

        }else{
            isImageAvailable=false;
            SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(imageUrl,0);
            try{
                itemDownload.setVisible(false);
                itemShare.setVisible(false);
            }catch (Exception e){
                e.printStackTrace();
            }
            DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                    .setRetryPolicy(new DefaultRetryPolicy())
                    .setDestinationURI(Uri.parse(destination.getAbsolutePath())).setPriority(DownloadRequest.Priority.HIGH)
                    .setDownloadListener(new DownloadStatusListener() {
                        @Override
                        public void onDownloadComplete(int id) {
                            isImageAvailable = true;
                            SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(imageUrl,1);
                            Picasso.with(AMApplication.getInstance().getApplicationContext())
                                    .load(destination)
                                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                                    .memoryPolicy(MemoryPolicy.NO_STORE)
                                    .skipMemoryCache()
                                    .into(mQuoteImage);
                            try{
                                itemDownload.setVisible(true);
                                itemShare.setVisible(true);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onDownloadFailed(int id, int errorCode, String errorMessage) {
                        }

                        @Override
                        public void onProgress(int id, long totalBytes, long downloadedBytes, int progressCount) {
                        }
                    });
            SmartApplication.REF_SMART_APPLICATION.getThinDownloadManager().add(downloadRequest);
        }

//        DataDownloadUtil.downloadImageFromServerAndRender(downloadUri, destination, mQuoteImage);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
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

    protected void saveImageToGallery() {
        DataDownloadUtil.saveImageToGallery(destinationImageFilePathPrefix, destinationImageFileName);
        SmartUtils.ting(this, "Downloaded Successfully");
    }

    private String getQuoteUpdatedUrl() {
        ArrayList<ContentValues> quoteImages = mSmartCaching.getDataFromCache("qowImages");
        if (quoteImages != null && quoteImages.size() > 0) {
            return quoteImages.get(0).getAsString("imageUrl");
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
        return R.layout.content_quote;
    }

    @Override
    public void initComponents() {
        super.initComponents();
        disableSideMenu();
        mSmartCaching = new SmartCaching(this);
        mQuoteImage = (TouchImageView) findViewById(R.id.quote_image);
        mQuoteImage.setOnClickListener(new PrivateOnLongClickListener());
    }

    @Override
    public void setActionListeners() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        selectDrawerItem(NAVIGATION_ITEMS.QUOTE_OF_DAY);
    }

    @Override
    public void prepareViews() {
        setQuoteImage();
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
        toolbar.setTitle(getString(R.string.nav_quote_of_the_week));
    }

    private class PrivateOnLongClickListener implements View.OnClickListener{

        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        @Override
        public void onClick(View v) {
            toggleActionBarDisplay();
        }
    }
}

