package com.amapp.anoopamaudio;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.design.widget.Snackbar;
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

import com.amapp.AMAppMasterActivity;
import com.amapp.Environment;
import com.amapp.R;
import com.amapp.common.AMConstants;
import com.amapp.common.NetworkCircularImageView;
import com.smart.caching.SmartCaching;
import com.smart.customviews.SmartRecyclerView;
import com.smart.customviews.SmartTextView;
import com.smart.framework.Constants;
import com.smart.framework.SmartUtils;
import com.smart.weservice.SmartWebManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tasol on 16/7/15.
 */

public class AudioListActivity extends AMAppMasterActivity {

    public static  final String AUDIO_LIST = "audio_list";
    private static final String TAG = "AudioCatListActivity";

    private SmartRecyclerView mRecyclerView;
    private AudioCatAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<ContentValues> audioList =new ArrayList<>();
    private SmartTextView emptyView;

    private SmartCaching smartCaching;

    private ContentValues audioDetails;


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
        mAdapter = new AudioCatAdapter();
        mRecyclerView.setAdapter(mAdapter);
        getAudioList();
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
        toolbar.setTitle(AMConstants.AM_Application_Title);
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
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.URL, audioDetails.getAsString("listURL")); //Passing parameter
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.REQUEST_METHOD, SmartWebManager.REQUEST_TYPE.GET);
        requestParams.put(SmartWebManager.REQUEST_METHOD_PARAMS.RESPONSE_LISTENER, new SmartWebManager.OnResponseReceivedListener() {

            @Override
            public void onResponseReceived(final JSONObject response, String errorMessage) {

                if (errorMessage != null && errorMessage.equalsIgnoreCase(getString(R.string.no_content_found))) {
                    SmartUtils.showSnackBar(AudioListActivity.this, getString(R.string.no_gym_found), Snackbar.LENGTH_LONG);
                } else {

                    try{
                        audioList = smartCaching.parseResponse(response.getJSONArray("audios"),"audios").get("audios");
                        mAdapter.notifyDataSetChanged();
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
        });

        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueue(requestParams, null, true);
    }

    @Override
    protected void onStop() {
        super.onStop();

        SmartWebManager.getInstance(this).getRequestQueue().cancelAll(AMConstants.AMS_Request_Get_Audio_List_Tag);
    }

    class AudioCatAdapter extends RecyclerView.Adapter<AudioCatAdapter.ViewHolder> implements Constants {

        public class ViewHolder extends RecyclerView.ViewHolder {

            public View view;
            public NetworkCircularImageView imgAudio;
            public SmartTextView txtAudioTitle;
            public SmartTextView txtAudioDuration;

            public ViewHolder(View view) {
                super(view);

                this.view = view;
                imgAudio = (NetworkCircularImageView) view.findViewById(R.id.imgAudio);
                txtAudioTitle = (SmartTextView) view.findViewById(R.id.txtAudioTitle);
                txtAudioDuration = (SmartTextView) view.findViewById(R.id.txtAudioDuration);
            }
        }

        @Override
        public AudioCatAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.audio_list_item, parent, false);

            final ViewHolder vh = new ViewHolder(v);

            v.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    int index = mRecyclerView.getChildAdapterPosition(view);
                    Intent intent = new Intent(AudioListActivity.this, AudioPlayerActivity.class);
                    intent.putExtra(AudioPlayerActivity.AUDIO, audioList.get(index));
                    intent.putExtra(AudioPlayerActivity.AUDIO_CAT, audioDetails.getAsString("catName"));
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(AudioListActivity.this);
                    ActivityCompat.startActivity(AudioListActivity.this, intent, options.toBundle());
                }
            });
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            ContentValues audio= audioList.get(position);

            holder.txtAudioTitle.setText(audio.getAsString("audioTitle"));
            holder.txtAudioDuration.setText("Duration : " + audio.getAsString("duration"));

            if(audio.containsKey("audioImage")){
                holder.imgAudio.setVisibility(View.VISIBLE);
                holder.imgAudio.setImageUrl(audio.getAsString("audioImage"), SmartWebManager.getInstance(AudioListActivity.this).getImageLoader());
            }
        }

        @Override
        public int getItemCount() {
            return audioList.size();
        }
    }




}