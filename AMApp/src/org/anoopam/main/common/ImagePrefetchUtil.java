/*
 * Copyright (c) 2016. Anoopam Mission. This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License at "http://www.gnu.org/licenses/licenses.html" for more details.
 *
 */

package org.anoopam.main.common;

import android.widget.ImageView;

import org.anoopam.main.AMApplication;
import org.anoopam.ext.smart.framework.SmartApplication;

/**
 * Created by dadesai on 1/5/16.
 */
public class ImagePrefetchUtil {

    public static void prefetchImageFromCache(String url, int targetWidth) {
        ImageView tempImage = new ImageView(AMApplication.getInstance().getApplicationContext());
//        SmartApplication.REF_SMART_APPLICATION.getAQuery().
//                id(tempImage).
//                image(url, true, true, targetWidth, 0);

    }
}
