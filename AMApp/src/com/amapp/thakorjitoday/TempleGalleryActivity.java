package com.amapp.thakorjitoday;

import android.content.ContentValues;
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

import com.amapp.AMAppMasterActivity;
import com.amapp.R;
import com.amapp.common.NetworkImageTouchView;
import com.smart.caching.SmartCaching;
import com.smart.framework.Constants;
import com.smart.weservice.SmartWebManager;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by tasol on 23/7/15.
 */

public class TempleGalleryActivity extends AMAppMasterActivity implements Constants {

    public static final String TEMPLE_DETAIL = "TEMPLE_DETAIL";
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
            final NetworkImageTouchView imgTemple= (NetworkImageTouchView) itemView.findViewById(R.id.imgTemple);
            if(position==0){
                setViewTransitionName(imgTemple,"image");
            }
            imgTemple.setImageUrl(images.get(position).getAsString("image"), SmartWebManager.getInstance(TempleGalleryActivity.this).getImageLoader());
            container.addView(itemView);
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((FrameLayout) object);
        }

    }

}
