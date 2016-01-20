package org.anoopam.main.aboutapp;

import android.content.ContentValues;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import org.anoopam.main.AMAppMasterActivity;
import org.anoopam.main.AMApplication;
import org.anoopam.main.R;
import org.anoopam.main.common.TouchImageView;
import com.androidquery.AQuery;
import org.anoopam.ext.smart.caching.SmartCaching;

import java.util.ArrayList;

/**
 * Created by npatel on 19th Jan 2016.
 */
public class AboutAppActivity extends FragmentActivity {

    private TouchImageView mAboutAppImage;
    private SmartCaching mSmartCaching;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_aboutapp);

    }
}
