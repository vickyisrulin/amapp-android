/*
 * Copyright (c) 2016. Anoopam Mission. This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License at "http://www.gnu.org/licenses/licenses.html" for more details.
 *
 */

package org.anoopam.main.aboutapp;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;

import org.anoopam.ext.smart.customviews.SmartTextView;
import org.anoopam.main.common.AMConstants;
import org.anoopam.main.BuildConfig;
import org.anoopam.main.R;

/**
 * Created by npatel on 19th Jan 2016.
 */
public class AboutAppActivity extends FragmentActivity {

    SmartTextView versionTextView;
    SmartTextView copyRightView;
    SmartTextView licenseTextView;
    SmartTextView contactUsTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_aboutapp);
        versionTextView = (SmartTextView) findViewById(R.id.txtVersion);
        String versionName = "Version: " + BuildConfig.VERSION_NAME;
        versionTextView.setText(versionName);

        copyRightView = (SmartTextView) findViewById(R.id.txtCopyRightView);
        String copyRightMessage = "Copyright \u00a9 2016, Anoopam Mission" ;
        copyRightView.setText(copyRightMessage);

        licenseTextView = (SmartTextView) findViewById(R.id.txtLicenseView);
        String licenseLink = "<a href='"+AMConstants.URL_Licenses+"'>License Information</a>";
        licenseTextView.setText(Html.fromHtml(licenseLink));
        licenseTextView.setMovementMethod(LinkMovementMethod.getInstance());

        String contactUsText = "<a href='"+AMConstants.URL_ContactUs+"'>Contact us</a>";
        contactUsTextView = (SmartTextView) findViewById(R.id.txtContactUs);
        contactUsTextView.setText(Html.fromHtml(contactUsText));
        contactUsTextView.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
