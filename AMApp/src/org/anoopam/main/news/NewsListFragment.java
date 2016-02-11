/*
 * Copyright (c) 2016. Anoopam Mission. This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License at "http://www.gnu.org/licenses/licenses.html" for more details.
 *
 */

package org.anoopam.main.news;

import android.content.ContentValues;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.anoopam.main.R;
import org.anoopam.ext.smart.customviews.SmartRecyclerView;
import org.anoopam.ext.smart.customviews.SmartTextView;
import org.anoopam.ext.smart.framework.Constants;
import org.anoopam.ext.smart.framework.SmartFragment;
import org.anoopam.ext.smart.framework.SmartUtils;

import java.util.ArrayList;

/**
 * Created by dadesai on 1/1/16.
 */
public class NewsListFragment extends SmartFragment {

    private SmartRecyclerView mRecyclerView;
    private TempleAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<ContentValues> newsImages;
    private SmartTextView emptyView;

    public NewsListFragment() {

    }

    @Override
    public int setLayoutId() {
        return R.layout.news_list_fragment;
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

    public void setTemples(ArrayList<ContentValues> newsImages, boolean isCachedDataDisplayed) {

        this.newsImages = newsImages;

        if(isCachedDataDisplayed && mRecyclerView.getChildCount()>0){
            mAdapter.notifyDataSetChanged();
        }else{
            mRecyclerView.setAdapter(mAdapter);
            emptyView.setText("No News Found");
            mRecyclerView.setEmptyView(emptyView);
        }
    }

    class TempleAdapter extends RecyclerView.Adapter<TempleAdapter.ViewHolder> implements Constants {

        public class ViewHolder extends RecyclerView.ViewHolder {

            public View view;
            public SmartTextView newsText;

            public ViewHolder(View view) {
                super(view);

                this.view = view;
                newsText = (SmartTextView) view.findViewById(R.id.txtNewsMessage);
            }
        }

        @Override
        public TempleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            final View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list_item, parent, false);

            final ViewHolder vh = new ViewHolder(v);
            if(SmartUtils.isOSPreLollipop()){
                ((CardView)v.findViewById(R.id.cardView)).setPreventCornerOverlap(false);
            }
            v.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                }
            });
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            ContentValues news = newsImages.get(position);
            holder.newsText.setText(news.getAsString("imageMessage"));
        }

        @Override
        public int getItemCount() {
            return newsImages.size();
        }
    }
}
