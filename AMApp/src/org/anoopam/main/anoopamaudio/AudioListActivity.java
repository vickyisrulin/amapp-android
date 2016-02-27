/*
 * Copyright (c) 2016. Anoopam Mission. This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License at "http://www.gnu.org/licenses/licenses.html" for more details.
 *
 */

package org.anoopam.main.anoopamaudio;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;

import org.anoopam.ext.smart.caching.SmartCaching;
import org.anoopam.ext.smart.customviews.SmartRecyclerView;
import org.anoopam.ext.smart.customviews.SmartTextView;
import org.anoopam.ext.smart.framework.Constants;
import org.anoopam.ext.smart.framework.SmartApplication;
import org.anoopam.ext.smart.framework.SmartUtils;
import org.anoopam.main.AMAppMasterActivity;
import org.anoopam.main.Environment;
import org.anoopam.main.R;
import org.anoopam.main.common.AMConstants;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by tasol on 16/7/15.
 */

public class AudioListActivity extends AMAppMasterActivity {


    public static  final String AUDIO_LIST = "audio_list";
    public static  final String ALBUM_NAME = "album_name";
    private static final String TAG = "AudioCatListActivity";

    private static Context mContext;
    private SmartRecyclerView mRecyclerView;
    private static AudioListAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<ContentValues> audioList =new ArrayList<>();
    private SmartTextView emptyView;

    private static LinearLayout linearLayoutPlayingSong;
    private static ImageView imageViewAlbumArt;
    private static TextView textNowPlaying;
    private Button btnPrevious;
    private static Button btnPlay;
    private static Button btnPause;
    private static TextView textMainAudioPlayerAlbumName;
    private static TextView textMainAudioPlayerSongName;

    private Button btnStop;
    private Button btnNext;
    private TextView textBufferDuration;
    private TextView textDuration;
    private SeekBar progressBar;

    private SmartCaching smartCaching;

    private ContentValues audioDetails;

    private static ImageView currentPlay;

    public static String currentAudio;

    private String currentAlbumName="Jay Shree Swaminarayan";

    private boolean isProgressBarTouching = false;

    @Override
    public View getLayoutView() {
        return null;
    }

    @Override
    public int getLayoutID() {
        return R.layout.audio_list;
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
        mAdapter = null;
        mContext = this;
        mRecyclerView = (SmartRecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        emptyView= (SmartTextView) findViewById(R.id.txtEmpty);

        linearLayoutPlayingSong = (LinearLayout)findViewById( R.id.linearLayoutPlayingSong );
        imageViewAlbumArt = (ImageView)findViewById( R.id.imageViewAlbumArt );
        textNowPlaying = (TextView)findViewById( R.id.textNowPlaying );
        btnPrevious = (Button)findViewById( R.id.btnPrevious );
        btnPlay = (Button)findViewById( R.id.btnPlay );
        btnPause = (Button)findViewById( R.id.btnPause );
        textMainAudioPlayerAlbumName = (TextView) findViewById(R.id.textMainPlayerAlbumName);
        textMainAudioPlayerSongName = (TextView) findViewById(R.id.textMainPlayerSongName);

        btnStop = (Button)findViewById( R.id.btnStop );
        btnNext = (Button)findViewById( R.id.btnNext );
        textBufferDuration = (TextView)findViewById( R.id.textBufferDuration );
        textDuration = (TextView)findViewById( R.id.textDuration );
        progressBar = (SeekBar)findViewById( R.id.progressBar );

        smartCaching = new SmartCaching(this);

        processIntent();
    }

    @Override
    public void prepareViews() {
        currentAudio = SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(AMConstants.KEY_CURRENT_AUDIO,"");

        getAudioListFromCache();
    }

    @Override
    public void setActionListeners() {
        super.setActionListeners();


        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Controls.playControl(getApplicationContext());
                if (currentPlay != null) {
                    try {
                        currentPlay.setImageResource(R.drawable.ic_action_av_pause_circle_outline);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Controls.pauseControl(getApplicationContext());
                if(currentPlay!=null){
                    try{
                        currentPlay.setImageResource(R.drawable.ic_action_av_play_circle_outline);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        btnNext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Controls.nextControl(getApplicationContext());
            }
        });
        btnPrevious.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Controls.previousControl(getApplicationContext());
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textBufferDuration.setText("0:0");
                textDuration.setText("0:0");

                Intent i = new Intent(getApplicationContext(), AudioService.class);
                stopService(i);
                stopPlayer();
            }
        });

        progressBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (isProgressBarTouching) {
                    textBufferDuration.setText(UtilFunctions.getDuration(seekBar.getProgress()));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                isProgressBarTouching = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                isProgressBarTouching = false;
                Controls.seekControl(getApplicationContext(), progressBar.getProgress());
            }
        });


        imageViewAlbumArt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //gdsg
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        selectDrawerItem(NAVIGATION_ITEMS.ANOOPAM_AUDIO);
        try{
            boolean isServiceRunning = UtilFunctions.isServiceRunning(AudioService.class.getName(), getApplicationContext());
            if (isServiceRunning) {
                updateUI();
            }else{
                linearLayoutPlayingSong.setVisibility(View.GONE);
            }
            changeButton();
            PlayerConstants.PROGRESSBAR_HANDLER = new Handler(){
                @Override
                public void handleMessage(Message msg){
                    Integer i[] = (Integer[])msg.obj;
                    textBufferDuration.setText(UtilFunctions.getDuration(i[0]));
                    textDuration.setText(UtilFunctions.getDuration(i[1]));
                    progressBar.setMax(i[1]);
                    if(!isProgressBarTouching){
                        progressBar.setProgress(i[0]);
                    }

                }
            };
        }catch(Exception e){
            e.printStackTrace();
        }
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
            if(b.get(AUDIO_LIST)!=null){
                audioDetails = (ContentValues) b.get(AUDIO_LIST);
            }

            if(b.getString(ALBUM_NAME)!= null) {
                currentAlbumName = b.getString(ALBUM_NAME);
            }

        }
    }

    private void getAudioListFromCache() {
        SmartUtils.showProgressDialog(this, "Loading...", true);

        AsyncTask<Void, Void, ArrayList<ContentValues>> task = new AsyncTask<Void, Void,ArrayList<ContentValues>>() {
            @Override
            protected ArrayList<ContentValues> doInBackground(Void... params) {
                try {
                    audioList = smartCaching.getDataFromCache("audios","SELECT * FROM audios WHERE catID='"+audioDetails.getAsString("catID")+"' ORDER BY audioTitle");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(ArrayList<ContentValues> result) {
                super.onPostExecute(result);
                SmartUtils.hideProgressDialog();

                if(audioList!=null && audioList.size()>0){
                    if(mAdapter==null){
                        mAdapter = new AudioListAdapter();
                        mRecyclerView.setAdapter(mAdapter);
                    }else{
                        mAdapter.notifyDataSetChanged();
                    }
                }

            }
        };

        if(android.os.Build.VERSION.SDK_INT>= Build.VERSION_CODES.HONEYCOMB){
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }else{
            task.execute();
        }

    }

    class AudioListAdapter extends RecyclerView.Adapter<AudioListAdapter.ViewHolder> implements Constants {

        public class ViewHolder extends RecyclerView.ViewHolder {

            public View view;
            public SmartTextView txtAudioTitle;
            public SmartTextView txtAudioDuration;
            public DonutProgress pbrLoading;
            public ImageView imgDownload;

            public ViewHolder(View view) {
                super(view);

                this.view = view;
                txtAudioTitle = (SmartTextView) view.findViewById(R.id.txtAudioTitle);
                txtAudioDuration = (SmartTextView) view.findViewById(R.id.txtAudioDuration);
                pbrLoading = (DonutProgress) view.findViewById(R.id.pbrLoading);
                imgDownload = (ImageView) view.findViewById(R.id.imgDownload);

            }
        }

        @Override
        public AudioListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.audio_list_item, parent, false);
            final ViewHolder vh = new ViewHolder(v);

            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, final int position) {

            final ContentValues audio= audioList.get(position);

            holder.txtAudioTitle.setText(audio.getAsString("audioTitle"));
            holder.txtAudioDuration.setText(audio.getAsString("duration"));
            if(audio.containsKey("loading") && audio.getAsString("loading").equalsIgnoreCase("1")){
                holder.pbrLoading.setVisibility(View.VISIBLE);
            }else{
                holder.pbrLoading.setVisibility(View.GONE);
            }

            final File destination = new File(SmartUtils.getAudioStorage(audioDetails.getAsString("catName"))+ File.separator + URLUtil.guessFileName(audio.getAsString("audioURL"), null, null));

            if(destination.exists()){
                if(!PlayerConstants.SONG_PAUSED && UtilFunctions.isServiceRunning(AudioService.class.getName(), getApplicationContext()) && audio.getAsString("audioURL").equals(currentAudio)) {
                    holder.imgDownload.setImageResource(R.drawable.ic_action_av_pause_circle_outline);
                    currentPlay = holder.imgDownload;
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(AMConstants.KEY_CURRENT_CAT_NAME, audioDetails.getAsString("catName"));
                    PlayerConstants.CATEGORY = audioDetails;
                    PlayerConstants.SONGS_LIST = audioList;
                }else{
                    holder.imgDownload.setImageResource(R.drawable.ic_action_av_play_circle_outline);
                }

            }else{
                holder.imgDownload.setImageResource(R.drawable.ic_action_file_cloud_download);

            }


            holder.imgDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (audio.containsKey("loading") && audio.getAsString("loading").equalsIgnoreCase("1")) {
                        return;
                    }

                    if (destination.exists()) {

                        if(!SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(AMConstants.KEY_CURRENT_CAT_NAME, "").equals(audioDetails.getAsString("catName"))){
                            PlayerConstants.SONG_NUMBER = -1;
                        }

                        try{
                            JSONObject currentCat = new JSONObject();
                            JSONArray catList = new JSONArray();

                            for (int i = 0; i < audioList.size() ; i++) {
                                JSONObject obj = new JSONObject();
                                obj.put("catID",audioList.get(i).getAsString("catID"));
                                obj.put("audioID",audioList.get(i).getAsString("audioID"));
                                obj.put("audioTitle",audioList.get(i).getAsString("audioTitle"));
                                obj.put("audioURL",audioList.get(i).getAsString("audioURL"));
                                obj.put("duration",audioList.get(i).getAsString("duration"));
                                catList.put(obj);
                            }
                            currentCat.put("audios",catList);
                            SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(AMConstants.KEY_CURRENT_AUDIO_LIST, currentCat.toString());
                            SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(AMConstants.KEY_CURRENT_CAT_NAME, audioDetails.getAsString("catName"));
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        PlayerConstants.CATEGORY = audioDetails;
                        PlayerConstants.SONGS_LIST = audioList;


                        if (currentPlay != null) {
                            try {
                                currentPlay.setImageResource(R.drawable.ic_action_av_play_circle_outline);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        currentPlay = (ImageView) v;

                        boolean isServiceRunning = UtilFunctions.isServiceRunning(AudioService.class.getName(), getApplicationContext());

                        if (!PlayerConstants.SONG_PAUSED && (PlayerConstants.SONG_NUMBER == position) && isServiceRunning) {
                            currentPlay.setImageResource(R.drawable.ic_action_av_play_circle_outline);
                            Controls.pauseControl(getApplicationContext());
                            PlayerConstants.SONG_PAUSED = true;
                        } else if (PlayerConstants.SONG_NUMBER == position && isServiceRunning) {
                            currentPlay.setImageResource(R.drawable.ic_action_av_pause_circle_outline);
                            Controls.playControl(getApplicationContext());
                            PlayerConstants.SONG_PAUSED = false;
                        } else {
                            currentPlay.setImageResource(R.drawable.ic_action_av_pause_circle_outline);
                            PlayerConstants.SONG_PAUSED = false;
                            PlayerConstants.SONG_NUMBER = position;

                            if (!isServiceRunning) {
                                Intent i = new Intent(getApplicationContext(), AudioService.class);
                                startService(i);
                            } else {
                                PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
                            }
                        }

                        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(AMConstants.KEY_CURRENT_AUDIO_NAME, audio.getAsString("audioTitle"));
                        SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(AMConstants.KEY_CURRENT_AUDIO, audio.getAsString("audioURL"));

                        currentAudio = audio.getAsString("audioURL");

                        updateUI();
                        changeButton();
                    } else {
                        holder.imgDownload.setVisibility(View.GONE);
                        holder.pbrLoading.setVisibility(View.VISIBLE);
                        audio.put("loading", "1");

                        Uri downloadUri = Uri.parse(audio.getAsString("audioURL").replaceAll(" ", "%20"));
                        Uri destinationUri = Uri.parse(destination.getAbsolutePath());

                        DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                                .setRetryPolicy(new DefaultRetryPolicy())
                                .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH)
                                .setDownloadListener(new DownloadStatusListener() {
                                    @Override
                                    public void onDownloadComplete(int id) {
                                        audio.put("loading", "0");
                                        holder.pbrLoading.setVisibility(View.GONE);
                                        holder.imgDownload.setVisibility(View.VISIBLE);
                                        holder.imgDownload.setImageResource(R.drawable.ic_action_av_play_circle_outline);
                                    }

                                    @Override
                                    public void onDownloadFailed(int id, int errorCode, String errorMessage) {
                                        audio.put("loading", "0");
                                        try{
                                            destination.delete();
                                        }catch (Error e){
                                            e.printStackTrace();
                                        }
                                        holder.imgDownload.setVisibility(View.VISIBLE);
                                        holder.pbrLoading.setVisibility(View.GONE);
                                    }

                                    @Override
                                    public void onProgress(int id, long totalBytes, long downloadedBytes, int progressCount) {
                                        holder.pbrLoading.setProgress(progressCount);
                                    }
                                });

                        SmartApplication.REF_SMART_APPLICATION.getThinDownloadManager().add(downloadRequest);
                    }

                }
            });

        }

        @Override
        public int getItemCount() {
            return audioList.size();
        }
    }

    public static void updateUI() {
        try{
            imageViewAlbumArt.setBackgroundDrawable(new BitmapDrawable(UtilFunctions.getDefaultAlbumArt(mContext)));
            linearLayoutPlayingSong.setVisibility(View.VISIBLE);
            String cachedAlbumName = SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(AMConstants.KEY_CURRENT_CAT_NAME, "");
            String cachedSongName = SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(AMConstants.KEY_CURRENT_AUDIO_NAME, "");

            textMainAudioPlayerAlbumName.setText(cachedAlbumName);
            textMainAudioPlayerSongName.setText(cachedSongName);

        }catch(Exception e){}
    }


    private static void notifyList(){
        try {
            mAdapter.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public static void changeButton() {
        if(PlayerConstants.SONG_PAUSED){
            btnPause.setVisibility(View.GONE);
            btnPlay.setVisibility(View.VISIBLE);
            if(currentPlay!=null){
                try{
                    currentPlay.setImageResource(R.drawable.ic_action_av_play_circle_outline);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }else{
            btnPause.setVisibility(View.VISIBLE);
            btnPlay.setVisibility(View.GONE);
            if(currentPlay!=null){
                try{
                    currentPlay.setImageResource(R.drawable.ic_action_av_pause_circle_outline);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        notifyList();
    }

    public static void changeUI(){
        updateUI();
        changeButton();
    }
    /**
     * returns the Anoopam Audio Endpoint
     * @return String
     */
    private String getAnoopamAudioListEndpoint() {
        // FIXME: Since AMS still doesnt have endpoint for audio, return MOCK endpoint
        return Environment.ENV_LIVE.getAnoopamAudioListEndpoint();
    }

    public static void stopPlayer(){

        try{
            linearLayoutPlayingSong.setVisibility(View.GONE);
        }catch (Throwable e){
            e.printStackTrace();
        }

        if(currentPlay!=null){
            try{
                currentPlay.setImageResource(R.drawable.ic_action_av_play_circle_outline);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        handleBackPress();

    }



    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        if(getIntent().getExtras().get(AUDIO_LIST)!=null){
            audioDetails = (ContentValues) getIntent().getExtras().get(AUDIO_LIST);
            PlayerConstants.CATEGORY = audioDetails;
        }

    }

    private void handleBackPress(){
        mAdapter = null;
        if(isTaskRoot()){
            Intent intent = new Intent(this, AudioCatListActivity.class);
            intent.putExtra(AudioListActivity.AUDIO_LIST, audioDetails);
            intent.putExtra(AMAppMasterActivity.MANAGE_UP_NAVIGATION, true);
            intent.putExtra(AudioCatListActivity.CAT_ID, audioDetails.getAsString("mainCatID"));
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this);
            ActivityCompat.startActivity(this, intent, options.toBundle());
        }
        supportFinishAfterTransition();
    }
}