/*
 * Copyright (c) 2016. Anoopam Mission. This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License at "http://www.gnu.org/licenses/licenses.html" for more details.
 *
 */

package org.anoopam.main.common;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;

import org.anoopam.ext.smart.framework.SmartApplication;
import org.anoopam.main.AMApplication;

import java.io.File;

/**
 * Created by dadesai on 1/5/16.
 */
public class DataDownloadUtil {

    public static void prefetchImageFromCache(String url, int targetWidth) {
        ImageView tempImage = new ImageView(AMApplication.getInstance().getApplicationContext());
//        SmartApplication.REF_SMART_APPLICATION.getAQuery().
//                id(tempImage).
//                image(url, true, true, targetWidth, 0);

    }

    private static void loadImageUsingPicasso(File destinationFile, final ImageView targetImageView) {
        Picasso.with(AMApplication.getInstance().getApplicationContext())
                .load(destinationFile)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_STORE)
                .skipMemoryCache()
                .into(targetImageView);
    }

    /**
     * 1) If destinationFile exist locally on device, displays it in targetImageView
     * 2) Otherwise, downloads the image from downloadFromUri and stores it at destinationFile and
     *               then displays it in targetImageView
     *
     * @param downloadFromUri Uri
     * @param destinationFile File
     * @param targetImageView ImageView
     */
    public static void downloadImageFromServerAndRender(final Uri downloadFromUri, final File destinationFile, final ImageView targetImageView) {
        downloadImageFromServerAndRender(downloadFromUri, destinationFile, targetImageView, null);
    }

    /**
     * 1) If destinationFile exist locally on device, displays it in targetImageView
     * 2) Otherwise, downloads the image from downloadFromUri and stores it at destinationFile and
     *               then displays it in targetImageView
     * 3) if progressBar is passed, then it would hide the progressBar
     *
     * @param downloadFromUri Uri
     * @param destinationFile File
     * @param targetImageView ImageView
     * @param progressBar Progress
     */
    public static void downloadImageFromServerAndRender(final Uri downloadFromUri, final File destinationFile, final ImageView targetImageView, final ProgressBar progressBar) {
        if(destinationFile.exists()){
            loadImageUsingPicasso(destinationFile, targetImageView);
        }else{
            Uri destinationUri = Uri.parse(destinationFile.getAbsolutePath());
            DownloadRequest downloadRequest = new DownloadRequest(downloadFromUri)
                    .setRetryPolicy(new DefaultRetryPolicy())
                    .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH)
                    .setDownloadListener(new DownloadStatusListener() {
                        @Override
                        public void onDownloadComplete(int id) {
                            if (progressBar != null) {
                                progressBar.setVisibility(View.GONE);
                            }
                            loadImageUsingPicasso(destinationFile, targetImageView);
                        }

                        @Override
                        public void onDownloadFailed(int id, int errorCode, String errorMessage) {
                            if (progressBar != null) {
                                progressBar.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onProgress(int id, long totalBytes, long downloadedBytes, int progressCount) {
                        }
                    });
            SmartApplication.REF_SMART_APPLICATION.getThinDownloadManager().add(downloadRequest);
        }
    }

    /**
     * 1) If destinationFile exist locally on device, NO-ACTION required
     * 2) Otherwise, downloads the image from downloadFromUri and stores it at destinationFile
     *
     * @param downloadFromUri Uri
     * @param destinationFile File
     */
    public static void downloadImageFromServer(final Uri downloadFromUri, final File destinationFile) {
        if(destinationFile.exists()){
            return;
        }else {
            Uri destinationUri = Uri.parse(destinationFile.getAbsolutePath());
            DownloadRequest downloadRequest = new DownloadRequest(downloadFromUri)
                    .setRetryPolicy(new DefaultRetryPolicy())
                    .setDestinationURI(destinationUri).setPriority(DownloadRequest.Priority.HIGH);
            SmartApplication.REF_SMART_APPLICATION.getThinDownloadManager().add(downloadRequest);
        }
    }
}
