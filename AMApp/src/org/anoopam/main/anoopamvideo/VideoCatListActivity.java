/*
 * Copyright (c) 2016. Anoopam Mission. This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License at "http://www.gnu.org/licenses/licenses.html" for more details.
 *
 */

package org.anoopam.main.anoopamvideo;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;

import org.anoopam.ext.smart.caching.SmartCaching;
import org.anoopam.ext.smart.customviews.SmartRecyclerView;
import org.anoopam.ext.smart.customviews.SmartTextView;
import org.anoopam.ext.smart.framework.Constants;
import org.anoopam.ext.smart.framework.SmartUtils;
import org.anoopam.main.AMAppMasterActivity;
import org.anoopam.main.R;
import org.anoopam.main.anoopamaudio.AudioListActivity;
import org.anoopam.main.common.AMServiceRequest;
import org.anoopam.main.common.CircleImageView;
import org.anoopam.main.common.DataDownloadUtil;
import org.anoopam.main.home.HomeListActivity;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by tasol on 16/7/15.
 */

public class VideoCatListActivity extends AMAppMasterActivity {

    private static final String TAG = "videoCatListActivity";

    private SmartRecyclerView mRecyclerView;
    private AudioCatAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<ContentValues> videoCat =new ArrayList<>();
    private SmartTextView emptyView;

    private SmartCaching smartCaching;
    private boolean showProgress;

    public static  final String CAT_ID = "catid";

    private String IN_CATID;
    private String albumName = "Jai Shree Swaminarayan";

    @Override
    public View getLayoutView() {
        return null;
    }

    @Override
    public int getLayoutID() {
        return R.layout.video_cat_list;
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

        disableSideMenu();

        mRecyclerView = (SmartRecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        emptyView= (SmartTextView) findViewById(R.id.txtEmpty);

        smartCaching = new SmartCaching(this);
    }

    @Override
    public void prepareViews() {
        Bundle b = getIntent().getExtras();

        IN_CATID = getIntent().getStringExtra(CAT_ID);

        if(b != null && b.getString(AudioListActivity.ALBUM_NAME) != null) {
            albumName = b.getString(AudioListActivity.ALBUM_NAME);
        }

        getVideoCategoryFormCache();
    }

    @Override
    public void setActionListeners() {
        super.setActionListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        selectDrawerItem(NAVIGATION_ITEMS.ANOOPAM_AUDIO);
    }

    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar, ActionBarDrawerToggle actionBarDrawerToggle) {
        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                handleBackPress();

            }
        });
        toolbar.setTitle(getString(R.string.nav_video_title));
        SpannableString spannableString=new SpannableString(albumName);
        spannableString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spannableString.length(), 0);
        toolbar.setSubtitle(spannableString);
    }

    @Override
    public boolean shouldKeyboardHideOnOutsideTouch() {
        return false;
    }

    private void getVideoCategories() {
        AsyncTask<Void, Void, ArrayList<ContentValues>> task = new AsyncTask<Void, Void, ArrayList<ContentValues>>() {
            @Override
            protected ArrayList<ContentValues> doInBackground(Void... params) {
                try {
                    return videoCat = smartCaching.getDataFromCache("videocategories", "SELECT * FROM videocategories WHERE mainCatID='1' ORDER BY catName ASC");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<ContentValues> result) {
                super.onPostExecute(result);
                SmartUtils.hideProgressDialog();

                if (videoCat == null || videoCat.size() <= 0) {
                    AMServiceRequest.getInstance().startFetchingAnoopamAudioFromServer();
                } else {
                    if(mAdapter==null){
                        mAdapter = new AudioCatAdapter();
                        mRecyclerView.setAdapter(mAdapter);
                    }else{
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            task.execute();
        }
    }

    private void getVideoCategoryFormCache(){

        SmartUtils.showProgressDialog(this, "Loading...", true);

        AsyncTask<Void, Void, ArrayList<ContentValues>> task = new AsyncTask<Void, Void,ArrayList<ContentValues>>() {
            @Override
            protected ArrayList<ContentValues> doInBackground(Void... params) {

                if(IN_CATID!=null && IN_CATID.length()>0){
                    try {
                        videoCat = smartCaching.getDataFromCache("videocategories","SELECT * FROM videocategories WHERE mainCatID='"+IN_CATID+"' ORDER BY catName ASC");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    try {
                        videoCat = smartCaching.getDataFromCache("videocategories","SELECT * FROM videocategories WHERE mainCatID='1' ORDER BY catName ASC");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<ContentValues> result) {
                super.onPostExecute(result);
                SmartUtils.hideProgressDialog();

                if(videoCat !=null && videoCat.size()>0){
                    if(mAdapter==null){
                        mAdapter = new AudioCatAdapter();
                        mRecyclerView.setAdapter(mAdapter);
                    }else{
                        mAdapter.notifyDataSetChanged();
                    }
                    showProgress=false;
                }else{
                    showProgress=true;
                }

                if(IN_CATID==null || IN_CATID.length()<=0 || IN_CATID.equals("1")){
                    getVideoCategories();
                }

            }
        };

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB){
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }else{
            task.execute();
        }
    }

    private boolean hasSubCategory(final String catID){
        try {
           ArrayList<ContentValues> subCat = smartCaching.getDataFromCache("videocategories","SELECT catName FROM videocategories WHERE mainCatID='"+catID+"'");
           if(subCat!=null && subCat.size()>0){
               if(subCat.get(0).getAsString("mainCatID").equals(subCat.get(0).getAsString("catID"))){
                return false;
               }
                return true;
           }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    class AudioCatAdapter extends RecyclerView.Adapter<AudioCatAdapter.ViewHolder> implements Constants {

        public class ViewHolder extends RecyclerView.ViewHolder {
            public View view;
            public SmartTextView txtCatName;
            public CircleImageView imgVideoCat;

            public ViewHolder(View view) {
                super(view);
                this.view = view;
                txtCatName = (SmartTextView) view.findViewById(R.id.txtCatName);
                imgVideoCat = (CircleImageView) view.findViewById(R.id.imgVideoCat);
            }
        }

        @Override
        public AudioCatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_cat_list_item, parent, false);
            final ViewHolder vh = new ViewHolder(v);

            v.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    int index = mRecyclerView.getChildAdapterPosition(view);

                    if(hasSubCategory(videoCat.get(index).getAsString("catID"))){
                        Intent intent = new Intent(VideoCatListActivity.this, VideoCatListActivity.class);
                        intent.putExtra(VideoCatListActivity.CAT_ID, videoCat.get(index).getAsString("catID"));
                        intent.putExtra(AudioListActivity.ALBUM_NAME, videoCat.get(index).getAsString("catName"));
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(VideoCatListActivity.this);
                        ActivityCompat.startActivity(VideoCatListActivity.this, intent, options.toBundle());
                    }else{
                        Intent intent = new Intent(VideoCatListActivity.this, VideoListActivity.class);
                        intent.putExtra(VideoListActivity.VIDEO_LIST, videoCat.get(index));
                        intent.putExtra(VideoListActivity.ALBUM_NAME, videoCat.get(index).getAsString("catName"));
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(VideoCatListActivity.this);
                        ActivityCompat.startActivity(VideoCatListActivity.this, intent, options.toBundle());
                    }

                }
            });
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            ContentValues audioCat= VideoCatListActivity.this.videoCat.get(position);
            holder.txtCatName.setText(audioCat.getAsString("catName"));

            final File destination = new File(SmartUtils.getAnoopamMissionImageStorage()+ File.separator +URLUtil.guessFileName(audioCat.getAsString("catImage"), null, null));
            final Uri downloadUri = Uri.parse(audioCat.getAsString("catImage").replaceAll(" ", "%20"));

            DataDownloadUtil.downloadImageFromServerAndRender(downloadUri, destination, holder.imgVideoCat);
        }

        @Override
        public int getItemCount() {
            return videoCat.size();
        }
    }


    @Override
    public void onBackPressed() {
        handleBackPress();
    }

    private void handleBackPress(){
        if (getIntent().getBooleanExtra(MANAGE_UP_NAVIGATION, false)){

            if(!IN_CATID.equals("1")){
                Intent intent = new Intent(this, VideoCatListActivity.class);
                intent.putExtra(AMAppMasterActivity.MANAGE_UP_NAVIGATION, true);
                intent.putExtra(VideoCatListActivity.CAT_ID, "1");
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this);
                ActivityCompat.startActivity(this, intent, options.toBundle());
            }else{
                Intent intent = new Intent(this, HomeListActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this);
                ActivityCompat.startActivity(this, intent, options.toBundle());
            }
        }
        supportFinishAfterTransition();
    }
}