/*
 * Copyright (c) 2016. Anoopam Mission. This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License at "http://www.gnu.org/licenses/licenses.html" for more details.
 *
 */

package org.anoopam.main.anoopamvideo;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
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
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;

import org.anoopam.ext.smart.caching.SmartCaching;
import org.anoopam.ext.smart.customviews.SmartRecyclerView;
import org.anoopam.ext.smart.customviews.SmartTextView;
import org.anoopam.ext.smart.framework.Constants;
import org.anoopam.ext.smart.framework.SmartUtils;
import org.anoopam.main.AMAppMasterActivity;
import org.anoopam.main.R;

import java.util.ArrayList;

/**
 * Created by tasol on 16/7/15.
 */

public class VideoListActivity extends AMAppMasterActivity {

    public static  final String VIDEO_LIST = "video_list";
    public static  final String ALBUM_NAME = "album_name";
    private static final String TAG = "VideoCatListActivity";

    private SmartRecyclerView mRecyclerView;
    private VideoListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<ContentValues> videoList =new ArrayList<>();
    private SmartTextView emptyView;

    private SmartCaching smartCaching;
    private  AQuery aQuery;
    private ContentValues videoDetails;
    private String currentAlbumName;

    @Override
    public View getLayoutView() {
        return null;
    }

    @Override
    public int getLayoutID() {
        return R.layout.video_list;
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
        aQuery = new AQuery(this);
        mAdapter = null;
        mRecyclerView = (SmartRecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        emptyView= (SmartTextView) findViewById(R.id.txtEmpty);

        smartCaching = new SmartCaching(this);
        processIntent();
    }

    @Override
    public void prepareViews() {
        getVideoListFromCache();
    }

    @Override
    public void setActionListeners() {
        super.setActionListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        selectDrawerItem(NAVIGATION_ITEMS.ANOOPAM_VIDEO);
    }

    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar, ActionBarDrawerToggle actionBarDrawerToggle) {

        actionBar.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbar.setTitle(getString(R.string.nav_video));
        SpannableString spannableString=new SpannableString(currentAlbumName);
        spannableString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spannableString.length(), 0);
        toolbar.setSubtitle(spannableString);
    }

    @Override
    public boolean shouldKeyboardHideOnOutsideTouch() {
        return false;
    }


    private void processIntent() {

        if(getIntent()!=null && getIntent().getExtras()!=null){

            Bundle b = getIntent().getExtras();
            if(b.get(VIDEO_LIST)!=null){
                videoDetails = (ContentValues) b.get(VIDEO_LIST);
            }

            if(b.getString(ALBUM_NAME)!= null) {
                currentAlbumName = b.getString(ALBUM_NAME);
            }

        }
    }


    private void getVideoListFromCache() {
        SmartUtils.showProgressDialog(this, "Loading...", true);

        AsyncTask<Void, Void, ArrayList<ContentValues>> task = new AsyncTask<Void, Void,ArrayList<ContentValues>>() {
            @Override
            protected ArrayList<ContentValues> doInBackground(Void... params) {
                try {
                    videoList = smartCaching.getDataFromCache("videos","SELECT * FROM videos WHERE catID='"+ videoDetails.getAsString("catID")+"' ORDER BY videoTitle ASC");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<ContentValues> result) {
                super.onPostExecute(result);
                SmartUtils.hideProgressDialog();

                if(videoList !=null && videoList.size()>0){
                    if(mAdapter==null){
                        mAdapter = new VideoListAdapter();
                        mRecyclerView.setAdapter(mAdapter);
                    }else{
                        mAdapter.notifyDataSetChanged();
                    }
                }

            }
        };

        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB){
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }else{
            task.execute();
        }

    }

    class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.ViewHolder> implements Constants {

        public class ViewHolder extends RecyclerView.ViewHolder {

            public View view;
            public SmartTextView txtVideoTitle;
            public SmartTextView txtVideoDuration;
            public ImageView imgVideoThumb;
            public ImageView imgPlay;
            public ContentValues video;

            public ViewHolder(View view) {
                super(view);

                this.view = view;
                txtVideoTitle = (SmartTextView) view.findViewById(R.id.txtVideoTitle);
                txtVideoDuration = (SmartTextView) view.findViewById(R.id.txtVideoDuration);
                imgPlay = (ImageView) view.findViewById(R.id.imgPlay);
                imgVideoThumb = (ImageView) view.findViewById(R.id.imgVideoThumb);

            }
        }

        @Override
        public VideoListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.video_list_item, parent, false);
            final ViewHolder vh = new ViewHolder(v);
            v.setTag(vh);

            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            holder.video = videoList.get(position);
            holder.txtVideoTitle.setText(holder.video.getAsString("videoTitle"));
            holder.txtVideoDuration.setText(holder.video.getAsString("duration"));

            final String thumbURL = "http://img.youtube.com/vi/"+AMYoutubePlayer.getYoutubeVideoID(holder.video.getAsString("videoURL"))+"/hqdefault.jpg";

            aQuery.ajax(thumbURL, Bitmap.class,0,new AjaxCallback<Bitmap>(){
                @Override
                public void callback(String url, Bitmap object, AjaxStatus status) {
                    super.callback(url, object, status);
                    holder.imgVideoThumb.setImageBitmap(object);
                }
            });


            holder.imgPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(VideoListActivity.this, AMYoutubePlayer.class);
                    intent.putExtra("YOUTUBE_VIDEO_URL",holder.video.getAsString("videoURL"));
                    ActivityCompat.startActivity(VideoListActivity.this, intent, null);
                }
            });
        }

        @Override
        public int getItemCount() {
            return videoList.size();
        }
    }


}