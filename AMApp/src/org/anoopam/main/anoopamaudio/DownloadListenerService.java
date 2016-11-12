package org.anoopam.main.anoopamaudio;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import org.anoopam.ext.smart.framework.SmartUtils;

import java.io.File;

public class DownloadListenerService extends BroadcastReceiver {

    private  DownloadManager downloadManager;
    private  DownloadManagerPro     downloadManagerPro;

    @Override
    public void onReceive(final Context context, Intent intent) {
        System.out.println("got here");

        try{
            String dirName="";
            String fileName="";
            long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0L);
            downloadManager = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
            downloadManagerPro = new DownloadManagerPro(downloadManager);
            int[] bytesAndStatus = downloadManagerPro.getBytesAndStatus(downloadId);

            if(bytesAndStatus[2]==DownloadManager.STATUS_SUCCESSFUL){

                dirName = downloadManagerPro.getString(downloadId,DownloadManager.COLUMN_DESCRIPTION);
                fileName = downloadManagerPro.getString(downloadId,DownloadManager.COLUMN_TITLE);

                SmartUtils.copyFile(SmartUtils.getAudioTempDownloadStorage(dirName)+File.separator,fileName,SmartUtils.getAudioStorage(dirName)+File.separator,false,true);


            }else{
                downloadManager.remove(downloadId);
                new File(SmartUtils.getAudioTempDownloadStorage(dirName)+File.separator,fileName);
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