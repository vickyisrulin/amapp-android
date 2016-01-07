package com.amapp.home;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amapp.AMApplication;
import com.amapp.R;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.smart.customviews.SmartRecyclerView;
import com.smart.customviews.SmartTextView;
import com.smart.framework.Constants;
import com.smart.framework.SmartActivity;
import com.smart.framework.SmartApplication;
import com.smart.framework.SmartFragment;
import com.smart.framework.SmartUtils;

import java.util.ArrayList;

/**
 * Created by ddesai on 12/15/15.
 */
public class HomeListFragment extends SmartFragment {

    private SmartRecyclerView mRecyclerView;
    private HomeAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<ContentValues> homeTiles;
    private SmartTextView emptyView;
    private ArrayList<Integer> homeTilesDefaultImages = new ArrayList<>();

    public HomeListFragment() {
    }

    @Override
    public int setLayoutId() {
        return R.layout.home_list_fragment;
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
        homeTilesDefaultImages.add(R.drawable.thakorji_today);
        homeTilesDefaultImages.add(R.drawable.sahebji_darshan);
        homeTilesDefaultImages.add(R.drawable.mantralekhan);
        homeTilesDefaultImages.add(R.drawable.weekly_quotes);
        homeTilesDefaultImages.add(R.drawable.anoopam_audio);
        homeTilesDefaultImages.add(R.drawable.contact_us);
        homeTilesDefaultImages.add(R.drawable.about_us);
    }

    @Override
    public void prepareViews(View currentView) {
        mAdapter = new HomeAdapter();
    }

    @Override
    public void setActionListeners(View currentView) {

    }

    public void setHomeTiles(ArrayList<ContentValues> homeTiles, boolean isCachedDataDisplayed) {

        this.homeTiles = homeTiles;

        if(isCachedDataDisplayed && mRecyclerView.getChildCount()>0){
            mAdapter.notifyDataSetChanged();
        }else{
            mRecyclerView.setAdapter(mAdapter);
            emptyView.setText("No Home Tiles Found");
            mRecyclerView.setEmptyView(emptyView);
        }
    }

    class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> implements Constants {

        public class ViewHolder extends RecyclerView.ViewHolder {

            public View view;
            public ImageView imgHomeTile;
            public SmartTextView txtName;

            public ViewHolder(View view) {
                super(view);

                this.view = view;
                imgHomeTile = (ImageView) view.findViewById(R.id.imgAlbum);
                txtName = (SmartTextView) view.findViewById(R.id.txtHomeTileName);
            }
        }

        @Override
        public HomeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_list_item, parent, false);

            final ViewHolder vh = new ViewHolder(v);
            if(SmartUtils.isOSPreLollipop()){
                ((CardView)v.findViewById(R.id.cardView)).setPreventCornerOverlap(false);
            }
            v.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    int tileId = mRecyclerView.getChildAdapterPosition(view);
                    ((HomeListActivity)getActivity()).handleTileClick(tileId);
                }
            });
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {

            ContentValues homeTile = homeTiles.get(position);
            int imageId = homeTilesDefaultImages.get(position).intValue();
            AQuery aq = AMApplication.getInstance().getAQuery();

            //REF: https://code.google.com/p/android-query/wiki/ImageLoading
            aq.id(holder.imgHomeTile)
                    .progress(R.id.progress)
                    .image(homeTile.getAsString("tileImage"), true, true, ((SmartActivity) getActivity()).getDeviceWidth(), imageId, null, AQuery.FADE_IN);

            holder.txtName.setText(homeTile.getAsString("tileName"));
        }

        @Override
        public int getItemCount() {
            return homeTiles.size();
        }
    }
}
