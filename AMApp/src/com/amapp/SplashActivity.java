package com.amapp;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.view.View;

/**
 * Created by tasol on 23/6/15.
 */

public class SplashActivity extends AMAppMaster {

    Handler handler=new Handler();

    @Override
    public View getLayoutView() {
        return null;
    }

    @Override
    public int getLayoutID() {
        return R.layout.splash_activity;
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

    }

    @Override
    public void prepareViews() {

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

               /* ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(SplashActivity.this);
                Intent intent = new Intent();
                boolean isFBUserLoggedIn=SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getBoolean(IS_USER_LOGGED_IN,false);
                intent.setClass(SplashActivity.this,isFBUserLoggedIn?DPHomeActivity.class:LoginActivity.class);
                ActivityCompat.startActivity(SplashActivity.this,intent, options.toBundle());
                finish();*/

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(SplashActivity.this);
                Intent intent = new Intent();
                intent.setClass(SplashActivity.this,TempleListActivity.class);
                ActivityCompat.startActivity(SplashActivity.this, intent, options.toBundle());
                finish();
            }
        },3000);
    }

    @Override
    public void setActionListeners() {


    }

    @Override
    public void manageAppBar(ActionBar actionBar, Toolbar toolbar,ActionBarDrawerToggle actionBarDrawerToggle) {

        actionBar.hide();
    }


    @Override
    public int getDrawerLayoutID() {
        return 0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
