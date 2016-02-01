package org.anoopam.main.aboutapp;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import org.anoopam.ext.smart.customviews.SmartTextView;
import org.anoopam.main.BuildConfig;
import org.anoopam.main.R;

/**
 * Created by npatel on 19th Jan 2016.
 */
public class AboutAppActivity extends FragmentActivity {

    SmartTextView versionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_aboutapp);
        versionTextView = (SmartTextView) findViewById(R.id.txtVersion);
        String versionName = "Version: " + BuildConfig.VERSION_NAME;
        versionTextView.setText(versionName);
    }
}
