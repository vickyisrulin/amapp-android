package org.anoopam.main.thakorjitoday;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.net.Uri;
import android.os.Handler;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ProgressBar;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.thin.downloadmanager.DefaultRetryPolicy;
import com.thin.downloadmanager.DownloadRequest;
import com.thin.downloadmanager.DownloadStatusListener;

import org.anoopam.ext.smart.framework.SmartApplication;
import org.anoopam.ext.smart.framework.SmartFragment;
import org.anoopam.ext.smart.framework.SmartUtils;
import org.anoopam.main.AMAppMasterActivity;
import org.anoopam.main.AMApplication;
import org.anoopam.main.R;
import org.anoopam.main.common.TouchImageView;

import java.io.File;

public class ImageFragment extends SmartFragment  {

    private TouchImageView imgTemple;
    private ProgressBar progress;
    private ContentValues imgageData;
    private int pos;
    boolean isImageAvailable = false;


    public ImageFragment() {}

    @SuppressLint("ValidFragment")
    public ImageFragment(ContentValues imgageData,int pos) {
        super();
        this.imgageData = imgageData;
        this.pos = pos;
    }


    /**
     * Overrides methods
     */

    @Override
    public int setLayoutId() {
        return R.layout.temple_pager_item;
    }

    @Override
    public View setLayoutView() {
        return null;
    }

    @Override
    public void initComponents(View currentView) {

        imgTemple= (TouchImageView) currentView.findViewById(R.id.imgAlbum);

        progress = (ProgressBar) currentView.findViewById(R.id.progress);

        final File destination = new File(SmartUtils.getAnoopamMissionDailyRefreshImageStorage(((TempleGalleryActivity)getActivity()).getTempleID())+File.separator + ((TempleGalleryActivity)getActivity()).getTempleID() +"_"+ URLUtil.guessFileName(imgageData.getAsString("image"), null, null));
        Uri downloadUri = Uri.parse(imgageData.getAsString("image").replaceAll(" ", "%20"));
        int hasImage = SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getInt(imgageData.getAsString("image"),0);
        if(destination.exists() && hasImage==1){

            isImageAvailable = true;
            Picasso.with(AMApplication.getInstance().getApplicationContext())
                    .load(destination)
                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                    .memoryPolicy(MemoryPolicy.NO_STORE)
                    .skipMemoryCache()
                    .into(imgTemple);
            showMenu();

        }else{
            showMenu();
            SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(imgageData.getAsString("image"),0);
            isImageAvailable = false;
            DownloadRequest downloadRequest = new DownloadRequest(downloadUri)
                    .setRetryPolicy(new DefaultRetryPolicy())
                    .setDestinationURI(Uri.parse(destination.getAbsolutePath())).setPriority(DownloadRequest.Priority.HIGH)
                    .setDownloadListener(new DownloadStatusListener() {
                        @Override
                        public void onDownloadComplete(int id) {
                            SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(imgageData.getAsString("image"),1);
                            isImageAvailable = true;
                            showMenu();
                            Picasso.with(AMApplication.getInstance().getApplicationContext())
                                    .load(destination)
                                    .memoryPolicy(MemoryPolicy.NO_CACHE)
                                    .memoryPolicy(MemoryPolicy.NO_STORE)
                                    .skipMemoryCache()
                                    .into(imgTemple);
                        }

                        @Override
                        public void onDownloadFailed(int id, int errorCode, String errorMessage) {
                            try{
                                destination.delete();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onProgress(int id, long totalBytes, long downloadedBytes, int progressCount) {
                        }
                    });
            SmartApplication.REF_SMART_APPLICATION.getThinDownloadManager().add(downloadRequest);
        }

    }

    @Override
    public void prepareViews(View currentView) {

    }

    @Override
    public void setActionListeners(View currentView) {
        imgTemple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AMAppMasterActivity)getActivity()).toggleActionBarDisplay();
            }
        });
    }

    @Override
    public void setMenuVisibility(final boolean menuVisible) {
        super.setMenuVisibility(menuVisible);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try{
                    if(menuVisible){
                        TempleGalleryActivity.itemDownload.setVisible(isImageAvailable);
                        TempleGalleryActivity.itemShare.setVisible(isImageAvailable);
                        ((TempleGalleryActivity)getActivity()).setDownloadPath(pos);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        },50);

    }

    private void showMenu(){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try{
                    if(((TempleGalleryActivity)getActivity()).getViewPager().getCurrentItem()==pos){

                        TempleGalleryActivity.itemDownload.setVisible(isImageAvailable);
                        TempleGalleryActivity.itemShare.setVisible(isImageAvailable);
                        ((TempleGalleryActivity)getActivity()).setDownloadPath(pos);
                    }
                }catch (Throwable e){
                    e.printStackTrace();
                }
            }
        },50);

    }

}