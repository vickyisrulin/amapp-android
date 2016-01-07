package com.amapp.thakorjitoday;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.amapp.R;
import com.amapp.common.ImagePrefetchUtil;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.androidquery.util.Progress;
import com.smart.caching.SmartCaching;
import com.smart.customviews.SmartRecyclerView;
import com.smart.customviews.SmartTextView;
import com.smart.framework.Constants;
import com.smart.framework.SmartActivity;
import com.smart.framework.SmartApplication;
import com.smart.framework.SmartFragment;
import com.smart.framework.SmartUtils;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by tasol on 21/8/15.
 */
public class TempleListFragment extends SmartFragment {

    private SmartRecyclerView mRecyclerView;
    private TempleAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<ContentValues> temples;
    private SmartTextView emptyView;

    public TempleListFragment() {

    }

    @Override
    public int setLayoutId() {
        return R.layout.temples_list_fragment;
    }

    @Override
    public View setLayoutView() {
        return null;
    }

    @Override
    public void initComponents(View currentView) {

        mRecyclerView = (SmartRecyclerView) currentView.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        emptyView= (SmartTextView) currentView.findViewById(R.id.txtEmpty);
    }

    @Override
    public void prepareViews(View currentView) {
        mAdapter = new TempleAdapter();
    }

    @Override
    public void setActionListeners(View currentView) {

    }

    public void setTemples(ArrayList<ContentValues> temples, boolean isCachedDataDisplayed) {

        this.temples = temples;

        if(isCachedDataDisplayed && mRecyclerView.getChildCount()>0){
            mAdapter.notifyDataSetChanged();
        }else{
            mRecyclerView.setAdapter(mAdapter);
            emptyView.setText("No Temples Found");
            mRecyclerView.setEmptyView(emptyView);
        }
    }

    class TempleAdapter extends RecyclerView.Adapter<TempleAdapter.ViewHolder> implements Constants {

        public class ViewHolder extends RecyclerView.ViewHolder {

            public View view;
            public ImageView imgTemple;
            public SmartTextView txtTemplePlace;
            private SmartTextView txtTempleLastUpdatedDate;
            private ProgressBar progressBar;

            public ViewHolder(View view) {
                super(view);

                this.view = view;

                progressBar = (ProgressBar) view.findViewById(R.id.templeListProgressBar);
                imgTemple = (ImageView) view.findViewById(R.id.imgAlbum);
                txtTemplePlace = (SmartTextView) view.findViewById(R.id.txtTemplePlace);
                txtTempleLastUpdatedDate = (SmartTextView) view.findViewById(R.id.txtTempleLastUpdatedDate);
            }
        }

        @Override
        public TempleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.temple_list_item, parent, false);

            final ViewHolder vh = new ViewHolder(v);
            if(SmartUtils.isOSPreLollipop()){
                ((CardView)v.findViewById(R.id.cardView)).setPreventCornerOverlap(false);
            }

            //clear all memory cached images when system is in low memory
            //note that you can configure the max image cache count, see CONFIGURATION
            BitmapAjaxCallback.clearCache();

            v.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    int index = mRecyclerView.getChildAdapterPosition(view);

                    // attempts to pre-fetch all the images for this temple
                    prefetchThakorjiPicsForTemple(index);

                    Intent intent = new Intent(getActivity(), TempleGalleryActivity.class);
                    intent.putExtra(TempleGalleryActivity.TEMPLE_DETAIL, temples.get(index));
                    Pair<View, String> p1 = Pair.create(view.findViewById(R.id.imgAlbum), "image");
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), p1);
                    ActivityCompat.startActivity(getActivity(), intent, options.toBundle());
                }
            });
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            ContentValues temple= temples.get(position);

            SmartApplication.REF_SMART_APPLICATION.getAQuery().id(holder.imgTemple).image(temple.getAsString("mainImage"), true, true, ((SmartActivity) getActivity()).getDeviceWidth(), 0, new BitmapAjaxCallback() {
                @Override
                protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                    super.callback(url, iv, bm, status);

                }
            });

            holder.progressBar.setVisibility(View.GONE);
            holder.txtTemplePlace.setText(temple.getAsString("templePlace"));
            holder.txtTempleLastUpdatedDate.setText(temple.getAsString("lastUpdatedTimestamp"));
        }

        @Override
        public int getItemCount() {
            return temples.size();
        }

        /**
         * 1) Will load all the image URLs for the given temple referenced by templeIndex
         * 2) Downloads the image from the given URL from the network, if it's not already in the memory/storage
         * @param templeCenterIndex
         */
        private void prefetchThakorjiPicsForTemple(int templeCenterIndex) {
            ArrayList<ContentValues> templeImages = null;

            // grabs all the image URLs for the given temple
            try {
                templeImages =new SmartCaching(getActivity().getBaseContext()).
                        parseResponse(new JSONArray(temples.get(templeCenterIndex).getAsString("images")),
                                "images").get("images");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            // iterates over all the URLs to load the images in the memory
            for(int i=0; i<templeImages.size(); i++) {
                String imageUrl = templeImages.get(i).getAsString("image");
                ImagePrefetchUtil.prefetchImageFromCache(imageUrl, ((SmartActivity)getActivity()).getDeviceWidth());
            }
        }
    }
}
