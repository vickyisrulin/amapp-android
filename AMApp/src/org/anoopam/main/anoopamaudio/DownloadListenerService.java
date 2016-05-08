package org.anoopam.main.anoopamaudio;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;

public class DownloadListenerService extends BroadcastReceiver {

    private  DownloadManager downloadManager;
    private  DownloadManagerPro     downloadManagerPro;

    @Override
    public void onReceive(final Context context, Intent intent) {
        System.out.println("got here");

        try{
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0L);

            downloadManager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
            downloadManagerPro = new DownloadManagerPro(downloadManager);
            int[] bytesAndStatus = downloadManagerPro.getBytesAndStatus(downloadId);

            if(AudioListActivity.isDownloading((Integer) bytesAndStatus[2])){
                return;
            }else{
                if(!((Integer)bytesAndStatus[2]==DownloadManager.STATUS_SUCCESSFUL)){

                    File removeFile = new File(getFilePathFromUri(context,Uri.parse(downloadManagerPro.getUri(downloadId))));
                    removeFile.delete();
                }
            }
        }catch (Throwable e){
            e.printStackTrace();
        }


    }

    public static String getFilePathFromUri(Context c, Uri uri) {
        String filePath = null;
        if ("content".equals(uri.getScheme())) {

            String[] filePathColumn = { MediaStore.MediaColumns.DATA };
            ContentResolver contentResolver = c.getContentResolver();

            Cursor cursor = contentResolver.query(uri, filePathColumn, null,
                    null, null);

            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            filePath = cursor.getString(columnIndex);
            cursor.close();
        } else if ("file".equals(uri.getScheme())) {
            filePath = new File(uri.getPath()).getAbsolutePath();
        }
        return filePath;
    }
}