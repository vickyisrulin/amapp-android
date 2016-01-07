package com.amapp.thakorjitoday;

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

import com.amapp.AMAppMasterActivity;
import com.amapp.AMApplication;
import com.amapp.R;
import com.amapp.common.TouchImageView;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import com.smart.caching.SmartCaching;
import com.smart.framework.Constants;
import com.smart.framework.SmartApplication;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by tasol on 23/7/15.
 */

public class TempleGalleryActivity extends AMAppMasterActivity implements Constants {

    public static final String TEMPLE_DETAIL = "ALBUM_DETAIL";
    private ContentValues templeDetail;
    private ArrayList<ContentValues> templeImages;
    private ViewPager viewPager;

    @Override
    public View getLayoutView() {
        return null;
    }

    @Override
    public int getLayoutID() {
        return R.layout.temple_gallery_activity;
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
        viewPager.setAdapter(new TemplePagerAdapter(templeImages));
    }

    private void processIntent() {

        if(getIntent()!=null && getIntent().getExtras()!=null){

            if(getIntent().getExtras().get(TEMPLE_DETAIL)!=null){

                templeDetail = (ContentValues) getIntent().getExtras().get(TEMPLE_DETAIL);
                try {
                    templeImages =new SmartCaching(this).parseResponse(new JSONArray(templeDetail.getAsString("images")),"images").get("images");
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

    public class TemplePagerAdapter extends PagerAdapter {

        ArrayList<ContentValues> images;

        public TemplePagerAdapter(ArrayList<ContentValues> images) {

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
            View itemView = LayoutInflater.from(TempleGalleryActivity.this).inflate(R.layout.temple_pager_item, container, false);
            final TouchImageView imgTemple= (TouchImageView) itemView.findViewById(R.id.imgAlbum);
            AQuery aq = AMApplication.getInstance().getAQuery();

            if(position==0){
                setViewTransitionName(imgTemple,"image");
            }

            String imageUrl = images.get(position).getAsString("image");

            // fetches the image from the network, if it's not in the local cache
            // displays it on the screen using imgTemple object
            aq.id(imgTemple)
                    .progress(R.id.progress)
                    .image(imageUrl, true, true, getDeviceWidth(), 0, null, AQuery.FADE_IN);

            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((FrameLayout) object);
        }

    }

}
