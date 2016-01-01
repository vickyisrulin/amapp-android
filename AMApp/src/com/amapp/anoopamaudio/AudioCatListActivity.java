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
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.amapp.AMAppMasterActivity;
import com.amapp.Environment;
import com.amapp.R;
import com.amapp.common.AMConstants;
import com.amapp.common.AMServiceResponseListener;
import com.smart.caching.SmartCaching;
import com.smart.customviews.SmartRecyclerView;
import com.smart.customviews.SmartTextView;
import com.smart.framework.Constants;
import com.smart.framework.SmartUtils;
import com.smart.weservice.SmartWebManager;

import org.json.JSONArray;
import org.json.JSONObject;

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
        mRecyclerView = (SmartRecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        emptyView= (SmartTextView) findViewById(R.id.txtEmpty);

        smartCaching = new SmartCaching(this);
    }

    @Override
    public void prepareViews() {
        mAdapter = new AudioCatAdapter();
        mRecyclerView.setAdapter(mAdapter);
        getAudioCategories();
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
                    public void onSuccess(JSONObject response) {
                        try {
                            JSONArray categories = response.getJSONArray("categories");
                            if(categories != null) {
                                audioCat = smartCaching.parseResponse(categories, "categories").get("categories");
                                mAdapter.notifyDataSetChanged();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(String failureMessage) {
                        Log.e(TAG, "Error obtaining Audio Category data: " + failureMessage);
                    }
                });
        SmartWebManager.getInstance(getApplicationContext()).addToRequestQueue(requestParams, null, true);
    }

    /**
     * returns the Anoopam Audio Endpoint
     * @return String
     */
    private String getAnoopamAudioEndpoint() {
        // FIXME: Since AMS still doesnt have endpoint for audio, return MOCK endpoint
        return Environment.ENV_MOCK_MOCKY.getAnoopamAudioEndpoint();
    }

    @Override
    protected void onStop() {
        super.onStop();

        SmartWebManager.getInstance(this).getRequestQueue().cancelAll(AMConstants.AMS_Request_Get_Audio_Cat_Tag);
    }

    class AudioCatAdapter extends RecyclerView.Adapter<AudioCatAdapter.ViewHolder> implements Constants {

        public class ViewHolder extends RecyclerView.ViewHolder {

            public View view;
            public SmartTextView txtCatName;

            public ViewHolder(View view) {
                super(view);

                this.view = view;
                txtCatName = (SmartTextView) view.findViewById(R.id.txtCatName);
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
                    Intent intent = new Intent(AudioCatListActivity.this, AudioListActivity.class);
                    intent.putExtra(AudioListActivity.AUDIO_LIST, audioCat.get(index));
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(AudioCatListActivity.this);
                    ActivityCompat.startActivity(AudioCatListActivity.this, intent, options.toBundle());
                }
            });
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            ContentValues audioCat= AudioCatListActivity.this.audioCat.get(position);

            holder.txtCatName.setText(audioCat.getAsString("catName"));
        }

        @Override
        public int getItemCount() {
            return audioCat.size();
        }
    }




}