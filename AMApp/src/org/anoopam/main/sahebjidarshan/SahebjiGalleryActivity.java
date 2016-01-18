package org.anoopam.main.sahebjidarshan;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import org.anoopam.main.AMAppMasterActivity;
import org.anoopam.main.R;
import org.anoopam.main.common.TouchImageView;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import org.smart.caching.SmartCaching;
import org.smart.framework.Constants;
import org.smart.framework.SmartApplication;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by tasol on 23/7/15.
 */

public class SahebjiGalleryActivity extends AMAppMasterActivity implements Constants {

    public static final String ALBUM_DETAIL = "ALBUM_DETAIL";
    private ContentValues albumDetail;
    private ArrayList<ContentValues> albumImages;
    private ViewPager viewPager;

    @Override
    public View getLayoutView() {
        return null;
    }

    @Override
    public int getLayoutID() {
        return R.layout.sahebji_gallery_activity;
    }

    @Override
    public void setAnimations() {
        super.setAnimations();
        getWindow().setEnterTransition(new Fade());
        getWindow().setExitTransition(new Fade());
    }

    @Override
    public void preOnCreate() {

    }

    @Override
    public void initComponents() {
        disableSideMenu();
        viewPager= (ViewPager) findViewById(R.id.viewPager);
    }

    @Override
    public void prepareViews() {

        processIntent();
        viewPager.setAdapter(new AlbumPagerAdapter(albumImages));
    }

    private void processIntent() {

        if(getIntent()!=null && getIntent().getExtras()!=null){

            if(getIntent().getExtras().get(ALBUM_DETAIL)!=null){

                albumDetail = (ContentValues) getIntent().getExtras().get(ALBUM_DETAIL);
                try {
                    albumImages =new SmartCaching(this).parseResponse(new JSONArray(albumDetail.getAsString("images")),"images").get("images");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    @Override
    public void setActionListeners() {

    }


    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar, ActionBarDrawerToggle actionBarDrawerToggle) {

    }

    @Override
    public int getDrawerLayoutID() {
        return 0;
    }

    public class AlbumPagerAdapter extends PagerAdapter {

        ArrayList<ContentValues> images;

        public AlbumPagerAdapter(ArrayList<ContentValues> images) {

            this.images=images;
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((FrameLayout) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            View itemView = LayoutInflater.from(SahebjiGalleryActivity.this).inflate(R.layout.sahebji_pager_item, container, false);
            final TouchImageView imgAlbum= (TouchImageView) itemView.findViewById(R.id.imgAlbum);
            if(position==0){
                setViewTransitionName(imgAlbum,"image");
            }
            SmartApplication.REF_SMART_APPLICATION.getAQuery().id(imgAlbum).image(images.get(position).getAsString("image"),true,true,getDeviceWidth(),0,new BitmapAjaxCallback(){
                @Override
                protected void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status) {
                    super.callback(url, iv, bm, status);

                }
            });
            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((FrameLayout) object);
        }

    }

}
