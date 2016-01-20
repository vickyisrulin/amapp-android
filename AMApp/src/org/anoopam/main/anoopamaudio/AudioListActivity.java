package org.anoopam.main.anoopamaudio;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.anoopam.main.AMAppMasterActivity;
import org.anoopam.main.R;

import org.anoopam.main.Environment;
import org.anoopam.main.common.AMConstants;
import org.anoopam.main.common.AMServiceResponseListener;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import org.anoopam.ext.smart.caching.SmartCaching;
import org.anoopam.ext.smart.customviews.SmartRecyclerView;
import org.anoopam.ext.smart.customviews.SmartTextView;
import org.anoopam.ext.smart.framework.Constants;
import org.anoopam.ext.smart.framework.SmartApplication;
import org.anoopam.ext.smart.weservice.SmartWebManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tasol on 16/7/15.
 */

public class AudioListActivity extends AMAppMasterActivity {


    public static  final String AUDIO_LIST = "audio_list";
    private static final String TAG = "AudioCatListActivity";

    private static Context mContext;
    private SmartRecyclerView mRecyclerView;
    private AudioCatAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<ContentValues> audioList =new ArrayList<>();
    private SmartTextView emptyView;

    private static LinearLayout linearLayoutPlayingSong;
    private static ImageView imageViewAlbumArt;
    private static TextView textNowPlaying;
    private Button btnPrevious;
    private static Button btnPlay;
    private static Button btnPause;
    private Button btnStop;
    private Button btnNext;
    private Button btnMusicPlayer;
    private TextView textBufferDuration;
    private TextView textDuration;
    private ProgressBar progressBar;

    private SmartCaching smartCaching;

    private ContentValues audioDetails;

    private static ImageView currentPlay;

    private String currentAudio;

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
        btnStop = (Button)findViewById( R.id.btnStop );
        btnNext = (Button)findViewById( R.id.btnNext );
        btnMusicPlayer = (Button)findViewById( R.id.btnMusicPlayer );
        textBufferDuration = (TextView)findViewById( R.id.textBufferDuration );
        textDuration = (TextView)findViewById( R.id.textDuration );
        progressBar = (ProgressBar)findViewById( R.id.progressBar );

        smartCaching = new SmartCaching(this);

        processIntent();
    }

    @Override
    public void prepareViews() {
        mAdapter = new AudioCatAdapter();
        mRecyclerView.setAdapter(mAdapter);
        currentAudio = SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(AMConstants.KEY_CURRENT_AUDIO,"");
        getAudioList();
    }

    @Override
    public void setActionListeners() {
        super.setActionListeners();

        btnMusicPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Controls.playControl(getApplicationContext());
                if(currentPlay!=null){
                    try{
                        currentPlay.setImageResource(R.drawable.ic_action_av_pause_circle_outline);
                    }catch (Exception e){
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
                Intent i = new Intent(getApplicationContext(), AudioService.class);
                stopService(i);
                linearLayoutPlayingSong.setVisibility(View.GONE);
                if(currentPlay!=null){
                    try{
                        currentPlay.setImageResource(R.drawable.ic_action_av_play_circle_outline);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
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
                    progressBar.setProgress(i[2]);
                }
            };
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar, ActionBarDrawerToggle actionBarDrawerToggle) {
        toolbar.setTitle(getString(R.string.nav_audio_title));
        SpannableString spannableString=new SpannableString(getString(R.string.app_subtitle));
        spannableString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spannableString.length(), 0);
        toolbar.setSubtitle(spannableString);
    }

    @Override
    public boolean shouldKeyboardHideOnOutsideTouch() {
        return false;
    }



    private void processIntent() {

        if(getIntent()!=null && getIntent().getExtras()!=null){

            if(getIntent().getExtras().get(AUDIO_LIST)!=null){
                audioDetails = (ContentValues) getIntent().getExtras().get(AUDIO_LIST);
            }

        }
    }

    private void getAudioList() {

        HashMap<SmartWebManager.REQUEST_METHOD_PARAMS, Object> requestParams = new HashMap<>();
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.CONTEXT,this);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.PARAMS, null);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_TYPES, SmartWebManager.REQUEST_TYPE.JSON_OBJECT);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.TAG, AMConstants.AMS_Request_Get_Audio_List_Tag);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, String.format(getAnoopamAudioListEndpoint(),audioDetails.getAsString("catID"))); //Passing parameter
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_METHOD, SmartWebManager.REQUEST_TYPE.GET);

        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new AMServiceResponseListener() {
            @Override
            public void onSuccess(JSONObject response) {
                try {
                    JSONArray audios = response.getJSONArray("audios");
                    if (audios != null) {
                        audioList = smartCaching.parseResponse(audios, "audios").get("audios");
                        PlayerConstants.SONGS_LIST = audioList;
                        mAdapter.notifyDataSetChanged();
                    }
                    SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(AMConstants.KEY_CURRENT_AUDIO_LIST, response.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String failureMessage) {
                Log.e(TAG, "Error obtaining Audio data: " + failureMessage);
            }
        });
        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueue(requestParams, null, true);
    }

    class AudioCatAdapter extends RecyclerView.Adapter<AudioCatAdapter.ViewHolder> implements Constants {

        public class ViewHolder extends RecyclerView.ViewHolder {

            public View view;
            public SmartTextView txtAudioTitle;
            public SmartTextView txtAudioDuration;
            public ProgressBar pbrLoading;
            public ImageView imgDownload;

            public ViewHolder(View view) {
                super(view);

                this.view = view;
                txtAudioTitle = (SmartTextView) view.findViewById(R.id.txtAudioTitle);
                txtAudioDuration = (SmartTextView) view.findViewById(R.id.txtAudioDuration);
                pbrLoading = (ProgressBar) view.findViewById(R.id.pbrLoading);
                imgDownload = (ImageView) view.findViewById(R.id.imgDownload);

            }
        }

        @Override
        public AudioCatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

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
            if(SmartApplication.REF_SMART_APPLICATION.getAQuery().getCachedFile(audio.getAsString("audioURL"))!=null){

                if(!PlayerConstants.SONG_PAUSED && UtilFunctions.isServiceRunning(AudioService.class.getName(), getApplicationContext()) && audio.getAsString("audioURL").equals(currentAudio)) {
                    holder.imgDownload.setImageResource(R.drawable.ic_action_av_pause_circle_outline);
                }else{
                    holder.imgDownload.setImageResource(R.drawable.ic_action_av_play_circle_outline);
                }
            }else{
                holder.imgDownload.setImageResource(R.drawable.ic_action_file_cloud_download);
            }


            holder.imgDownload.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(audio.containsKey("loading") && audio.getAsString("loading").equalsIgnoreCase("1")){
                        return;
                    }

                    if(SmartApplication.REF_SMART_APPLICATION.getAQuery().getCachedFile(audio.getAsString("audioURL"))==null){
                        holder.pbrLoading.setVisibility(View.VISIBLE);
                        audio.put("loading", "1");
                        SmartApplication.REF_SMART_APPLICATION.getAQuery().ajax(audio.getAsString("audioURL"),File.class,0,new AjaxCallback<File>(){

                            @Override
                            public AjaxCallback<File> progress(Object progress) {
                                return super.progress(progress);
                            }

                            @Override
                            public void callback(String url, File object, AjaxStatus status) {
                                super.callback(url, object, status);
                                audio.put("loading", "0");
                                holder.pbrLoading.setVisibility(View.GONE);
                                holder.imgDownload.setImageResource(R.drawable.ic_action_av_play_circle_outline);
                            }
                        });

                    }else{

                        if(currentPlay!=null){
                            try{
                                currentPlay.setImageResource(R.drawable.ic_action_av_play_circle_outline);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        currentPlay = (ImageView)v;

                        boolean isServiceRunning = UtilFunctions.isServiceRunning(AudioService.class.getName(), getApplicationContext());

                        if(!PlayerConstants.SONG_PAUSED && (PlayerConstants.SONG_NUMBER==position) && isServiceRunning){
                            currentPlay.setImageResource(R.drawable.ic_action_av_play_circle_outline);
                            Controls.pauseControl(getApplicationContext());
                            PlayerConstants.SONG_PAUSED = true;
                        } else if(PlayerConstants.SONG_NUMBER==position && isServiceRunning) {
                            currentPlay.setImageResource(R.drawable.ic_action_av_pause_circle_outline);
                            Controls.playControl(getApplicationContext());
                            PlayerConstants.SONG_PAUSED = false;
                        }else {
                            currentPlay.setImageResource(R.drawable.ic_action_av_pause_circle_outline);
                            PlayerConstants.SONG_PAUSED = false;
                            PlayerConstants.SONG_NUMBER = position;

                            if (!isServiceRunning) {
                                Intent i = new Intent(getApplicationContext(),AudioService.class);
                                startService(i);
                            } else {
                                PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
                            }
                        }
                        updateUI();
                        changeButton();
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
        }catch(Exception e){}
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

}