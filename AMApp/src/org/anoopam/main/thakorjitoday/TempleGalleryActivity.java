/*
 * Copyright (c) 2016. Anoopam Mission. This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License at "http://www.gnu.org/licenses/licenses.html" for more details.
 *
 */

package org.anoopam.main.thakorjitoday;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ProgressBar;

import org.anoopam.ext.smart.caching.SmartCaching;
import org.anoopam.ext.smart.customviews.Log;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_menu_home, menu);
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
        viewPager.setAdapter(new TemplePagerAdapter(templeImages));
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
    public void setActionListeners() {

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position > 0) {
                    View view = viewPager.getChildAt(position - 1);
                    if (view != null) {
                        TouchImageView img = (TouchImageView) view.findViewById(R.id.imgAlbum);
                        img.resetZoom();
                    }
                }

                if (position > 0) {
                    View view = viewPager.getChildAt(position + 1);
                    if (view != null) {
                        TouchImageView img = (TouchImageView) view.findViewById(R.id.imgAlbum);
                        img.resetZoom();
                    }
                }

                if (position < viewPager.getChildCount() - 1) {
                    View view = viewPager.getChildAt(position + 1);
                    if (view != null) {
                        TouchImageView img = (TouchImageView) view.findViewById(R.id.imgAlbum);
                        img.resetZoom();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

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

    public class TemplePagerAdapter extends PagerAdapter {

        ArrayList<ContentValues> images;

        public TemplePagerAdapter(ArrayList<ContentValues> images) {

            this.images=images;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void setPrimaryItem(ViewGroup viewGroup, int position, Object object){
            destinationImageFileName = templeDetail.getAsString("templeID") +"_"+ URLUtil.guessFileName(images.get(position).getAsString("image"),null,null);
            destinationImageFilePathPrefix = SmartUtils.getAnoopamMissionDailyRefreshImageStorage()+File.separator;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = LayoutInflater.from(TempleGalleryActivity.this).inflate(R.layout.temple_pager_item, container, false);
            final TouchImageView imgTemple= (TouchImageView) itemView.findViewById(R.id.imgAlbum);
            imgTemple.setOnLongClickListener(new PrivateOnLongClickListener());
            final ProgressBar progress = (ProgressBar) itemView.findViewById(R.id.progress);

            final File destination = new File(SmartUtils.getAnoopamMissionDailyRefreshImageStorage()+File.separator + templeDetail.getAsString("templeID") +"_"+ URLUtil.guessFileName(images.get(position).getAsString("image"),null,null));

            Uri downloadUri = Uri.parse(images.get(position).getAsString("image").replaceAll(" ", "%20"));
            DataDownloadUtil.downloadImageFromServerAndRender(downloadUri, destination, imgTemple, progress);

            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

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
                DataDownloadUtil.shareImage(this, destinationImageFilePathPrefix, destinationImageFileName);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    void saveImageToGallery() {
        DataDownloadUtil.saveImageToGallery(destinationImageFilePathPrefix, destinationImageFileName);
        SmartUtils.ting(this, "Downloaded Successfully");
    }

}

