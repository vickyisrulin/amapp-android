/*
 * Copyright (c) 2016. Anoopam Mission. This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License at "http://www.gnu.org/licenses/licenses.html" for more details.
 *
 */

package org.anoopam.main.anoopamaudio;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;

import com.squareup.picasso.Picasso;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;

import org.anoopam.ext.smart.caching.SmartCaching;
import org.anoopam.ext.smart.customviews.SmartRecyclerView;
import org.anoopam.ext.smart.customviews.SmartTextView;
import org.anoopam.ext.smart.framework.Constants;
import org.anoopam.ext.smart.framework.SmartApplication;
import org.anoopam.ext.smart.framework.SmartUtils;
import org.anoopam.ext.smart.weservice.SmartWebManager;
import org.anoopam.main.AMAppMasterActivity;
import org.anoopam.main.AMApplication;
import org.anoopam.main.R;
import org.anoopam.main.common.AMConstants;
import org.anoopam.main.common.AMServiceResponseListener;
import org.anoopam.main.common.CircleImageView;
import org.anoopam.main.home.HomeListActivity;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tasol on 16/7/15.
 */

public class AudioCatListActivity extends AMAppMasterActivity {

    private static final String TAG = "AudioCatListActivity";

    private SmartRecyclerView mRecyclerView;
    private AudioCatAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<ContentValues> audioCat=new ArrayList<>();
    private SmartTextView emptyView;

    private SmartCaching smartCaching;
    private boolean showProgress;

    public static  final String CAT_ID = "catid";

    private String IN_CATID;

    @Override
    public View getLayoutView() {
        return null;
    }

    @Override
    public int getLayoutID() {
        return R.layout.audio_cat_list;
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
        IN_CATID = getIntent().getStringExtra(CAT_ID);

        getAudioCategoryFormCache();
    }

    @Override
    public void setActionListeners() {
        super.setActionListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectDrawerItem(NAVIGATION_ITEMS.ANOOPAM_AUDIO);
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
        toolbar.setTitle(getString(R.string.nav_audio_title));
        SpannableString spannableString=new SpannableString(getString(R.string.app_subtitle));
        spannableString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spannableString.length(), 0);
        toolbar.setSubtitle(spannableString);
    }

    @Override
    public boolean shouldKeyboardHideOnOutsideTouch() {
        return false;
    }

    private void getAudioCategories() {

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT,this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, null);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, AMConstants.AMS_Request_Get_Audio_Cat_Tag);

        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, getAnoopamAudioEndpoint());
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_METHOD, SmartWebManager.REQUEST_TYPE.GET);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new AMServiceResponseListener() {
            @Override
            public void onSuccess(final JSONObject response) {


                AsyncTask<Void, Void, ArrayList<ContentValues>> task = new AsyncTask<Void, Void, ArrayList<ContentValues>>() {
                    @Override
                    protected ArrayList<ContentValues> doInBackground(Void... params) {

                        try {
                            smartCaching.cacheResponse(response.getJSONArray("categories"), "categories", false);
                            audioCat = smartCaching.getDataFromCache("categories", "SELECT * FROM categories WHERE mainCatID='0'");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            smartCaching.cacheResponse(response.getJSONArray("audios"), "audios", false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        return null;
                    }

                    @Override
                    protected void onPostExecute(ArrayList<ContentValues> result) {
                        super.onPostExecute(result);
                        SmartUtils.hideProgressDialog();

                        if (audioCat != null && audioCat.size() > 0) {

                            if(mAdapter==null){
                                mAdapter = new AudioCatAdapter();
                                mRecyclerView.setAdapter(mAdapter);
                            }else{
                                mAdapter.notifyDataSetChanged();
                            }
                        }

                    }
                };

                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                } else {
                    task.execute();
                }

            }

            @Override
            public void onFailure(String failureMessage) {
                Log.e(TAG, "Error obtaining Audio Category data: " + failureMessage);
            }
        });
        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueue(requestParams, null, showProgress);
    }


    private void getAudioCategoryFormCache(){

        SmartUtils.showProgressDialog(this, "Loading...", true);

        AsyncTask<Void, Void, ArrayList<ContentValues>> task = new AsyncTask<Void, Void,ArrayList<ContentValues>>() {
            @Override
            protected ArrayList<ContentValues> doInBackground(Void... params) {

                if(IN_CATID!=null && IN_CATID.length()>0){
                    try {
                        audioCat = smartCaching.getDataFromCache("categories","SELECT * FROM categories WHERE mainCatID='"+IN_CATID+"'");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    try {
                        audioCat = smartCaching.getDataFromCache("categories","SELECT * FROM categories WHERE mainCatID='0'");
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

                if(audioCat!=null && audioCat.size()>0){
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

                if(IN_CATID==null || IN_CATID.length()<=0 || IN_CATID.equals("0")){
                    getAudioCategories();
                }

            }
        };

        if(android.os.Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB){
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }else{
            task.execute();
        }
    }

    private boolean hasSubCategory(final String catID){
        try {
           ArrayList<ContentValues> subCat = smartCaching.getDataFromCache("categories","SELECT catName FROM categories WHERE mainCatID='"+catID+"'");
           if(subCat!=null && subCat.size()>0){
                return true;
           }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;


    }


    /**
     * returns the Anoopam Audio Endpoint
     * @return String
     */
    private String getAnoopamAudioEndpoint() {

        return AMApplication.getInstance().getEnv().getAnoopamAudioEndpoint();
    }


    class AudioCatAdapter extends RecyclerView.Adapter<AudioCatAdapter.ViewHolder> implements Constants {

        public class ViewHolder extends RecyclerView.ViewHolder {

            public View view;
            public SmartTextView txtCatName;
            public CircleImageView imgAudioCat;

            public ViewHolder(View view) {
                super(view);

                this.view = view;
                txtCatName = (SmartTextView) view.findViewById(R.id.txtCatName);
                imgAudioCat = (CircleImageView) view.findViewById(R.id.imgAudioCat);


            }
        }

        @Override
        public AudioCatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.audio_cat_list_item, parent, false);

            final ViewHolder vh = new ViewHolder(v);

            v.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    int index = mRecyclerView.getChildAdapterPosition(view);

                    if(hasSubCategory(audioCat.get(index).getAsString("catID"))){
                        Intent intent = new Intent(AudioCatListActivity.this, AudioCatListActivity.class);
                        intent.putExtra(AudioCatListActivity.CAT_ID,audioCat.get(index).getAsString("catID"));
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(AudioCatListActivity.this);
                        ActivityCompat.startActivity(AudioCatListActivity.this, intent, options.toBundle());
                    }else{
                        Intent intent = new Intent(AudioCatListActivity.this, AudioListActivity.class);
                        intent.putExtra(AudioListActivity.AUDIO_LIST, audioCat.get(index));
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(AudioCatListActivity.this);
                        ActivityCompat.startActivity(AudioCatListActivity.this, intent, options.toBundle());
                    }

                }
            });
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            ContentValues audioCat= AudioCatListActivity.this.audioCat.get(position);
            holder.txtCatName.setText(audioCat.getAsString("catName"));

            final File destination = new File(SmartUtils.getAnoopamMissionImageStorage()+ File.separator +URLUtil.guessFileName(audioCat.getAsString("catImage"), null, null));

            if(destination.exists()){
                Picasso.with(AudioCatListActivity.this)
                        .load(destination)
                        .into(holder.imgAudioCat);

            }else{
                Uri downloadUri = Uri.parse(audioCat.getAsString("catImage").replaceAll(" ", "%20"));
                Uri destinationUri = Uri.parse(destination.getAbsolutePath());

                DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                        .setRetryPolicy(new DefaultRetryPolicy())
                        .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH)
                        .setDownloadListener(new DownloadStatusListener() {
                            @Override
                            public void onDownloadComplete(int id) {
                                Picasso.with(AudioCatListActivity.this)
                                        .load(destination)
                                        .into(holder.imgAudioCat);
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

        }

        @Override
        public int getItemCount() {
            return audioCat.size();
        }
    }


    @Override
    public void onBackPressed() {
        handleBackPress();
    }

    private void handleBackPress(){
        if (getIntent().getBooleanExtra(MANAGE_UP_NAVIGATION, false)){

            if(!IN_CATID.equals("0")){
                Intent intent = new Intent(this, AudioCatListActivity.class);
                intent.putExtra(AMAppMasterActivity.MANAGE_UP_NAVIGATION, true);
                intent.putExtra(AudioCatListActivity.CAT_ID, "0");
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