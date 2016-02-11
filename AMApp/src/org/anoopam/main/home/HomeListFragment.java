/*
 * Copyright (c) 2016. Anoopam Mission. This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License at "http://www.gnu.org/licenses/licenses.html" for more details.
 *
 */

package org.anoopam.main.home;

import android.content.ContentValues;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;

import org.anoopam.ext.smart.customviews.SmartRecyclerView;
import org.anoopam.ext.smart.customviews.SmartTextView;
import org.anoopam.ext.smart.framework.Constants;
import org.anoopam.ext.smart.framework.SmartApplication;
import org.anoopam.ext.smart.framework.SmartFragment;
import org.anoopam.ext.smart.framework.SmartUtils;
import org.anoopam.main.R;

import java.io.File;
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

            holder.txtName.setText(homeTile.getAsString("tileName"));

            final File destination = new File(SmartUtils.getImageStorage()+ File.separator +URLUtil.guessFileName(homeTile.getAsString("tileImage"), null, null));

            if(destination.exists()){
                Picasso.with(getActivity())
                        .load(destination)
                        .into(holder.imgHomeTile);

            }else{
                Uri downloadUri = Uri.parse(homeTile.getAsString("tileImage").replaceAll(" ", "%20"));
                Uri destinationUri = Uri.parse(destination.getAbsolutePath());

                DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                        .setRetryPolicy(new DefaultRetryPolicy())
                        .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH)
                        .setDownloadListener(new DownloadStatusListener() {
                            @Override
                            public void onDownloadComplete(int id) {
                                Picasso.with(getActivity())
                                        .load(destination)
                                        .into(holder.imgHomeTile);
                            }

                            @Override
                            public void onDownloadFailed(int id, int errorCode, String errorMessage) {
                                System.out.println("");
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
            return homeTiles.size();
        }
    }
}
