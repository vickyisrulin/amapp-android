package org.anoopam.main.qow;

import android.content.ContentValues;

import org.anoopam.ext.smart.framework.SmartApplication;
import org.anoopam.ext.smart.framework.SmartUtils;
import org.anoopam.main.AMAppMasterActivity;
import org.anoopam.main.AMApplication;
import org.anoopam.main.R;
import org.anoopam.main.common.TouchImageView;

import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.URLUtil;

import com.squareup.picasso.Picasso;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;

import org.anoopam.ext.smart.caching.SmartCaching;

import java.io.File;
import java.util.ArrayList;

public class QuoteActivity extends AMAppMasterActivity {

    private TouchImageView mQuoteImage;
    private SmartCaching mSmartCaching;

    private void setQuoteImage() {
        String imageUrl = getQuoteUpdatedUrl();

        final File destination = new File(SmartUtils.getAnoopamMissionImageStorage()+ File.separator + URLUtil.guessFileName(imageUrl, null, null));

        if(destination.exists()){
            Picasso.with(this)
                    .load(destination)
                    .into(mQuoteImage);

        }else{
            Uri downloadUri = Uri.parse(imageUrl.replaceAll(" ", "%20"));
            Uri destinationUri = Uri.parse(destination.getAbsolutePath());

            DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                    .setRetryPolicy(new DefaultRetryPolicy())
                    .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH)
                    .setDownloadListener(new DownloadStatusListener() {
                        @Override
                        public void onDownloadComplete(int id) {
                            Picasso.with(QuoteActivity.this)
                                    .load(destination)
                                    .into(mQuoteImage);
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

    private String getQuoteUpdatedUrl() {
        ArrayList<ContentValues> quoteImages = mSmartCaching.getDataFromCache("qowImages");
        if (quoteImages != null && quoteImages.size() > 0) {
            return quoteImages.get(0).getAsString("imageUrl");
        }
        return "";
    }

    @Override
    public void preOnCreate() {

    }

    @Override
    public View getLayoutView() {
        return null;
    }

    @Override
    public int getLayoutID() {
        return R.layout.content_quote;
    }

    @Override
    public void initComponents() {
        disableSideMenu();
        mSmartCaching = new SmartCaching(this);
        mQuoteImage = (TouchImageView) findViewById(R.id.quote_image);
    }

    @Override
    public void setActionListeners() {

    }

    @Override
    public void prepareViews() {
        setQuoteImage();
    }

    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar, ActionBarDrawerToggle actionBarDrawerToggle) {

    }
}

