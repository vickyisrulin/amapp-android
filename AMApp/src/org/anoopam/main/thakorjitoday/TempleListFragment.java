package org.anoopam.main.thakorjitoday;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.ProgressBar;

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

            v.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    int index = mRecyclerView.getChildAdapterPosition(view);

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

            final File destination = new File(SmartUtils.getAnoopamMissionDailyRefreshImageStorage()+ File.separator +temple.getAsString("templeID") +"_"+ URLUtil.guessFileName(temple.getAsString("mainImage"), null, null));

            if(destination.exists()){
                Picasso.with(getActivity())
                        .load(destination)
                        .into(holder.imgTemple);

            }else{
                Uri downloadUri = Uri.parse(temple.getAsString("mainImage").replaceAll(" ", "%20"));
                Uri destinationUri = Uri.parse(destination.getAbsolutePath());

                DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                        .setRetryPolicy(new DefaultRetryPolicy())
                        .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH)
                        .setDownloadListener(new DownloadStatusListener() {
                            @Override
                            public void onDownloadComplete(int id) {
                                Picasso.with(getActivity())
                                        .load(destination)
                                        .into(holder.imgTemple);
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


            holder.progressBar.setVisibility(View.VISIBLE);
            holder.txtTemplePlace.setText(temple.getAsString("templePlace"));
            holder.txtTempleLastUpdatedDate.setText(temple.getAsString("lastUpdatedTimestamp"));
        }

        @Override
        public int getItemCount() {
            return temples.size();
        }


    }
}
