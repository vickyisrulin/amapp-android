package com.amapp.common;

import android.widget.ImageView;

import com.amapp.AMApplication;
import com.smart.framework.SmartActivity;
import com.smart.framework.SmartApplication;

/**
 * Created by dadesai on 1/5/16.
 */
public class ImagePrefetchUtil {

    public static void prefetchImageFromCache(String url, int targetWidth) {
        ImageView tempImage = new ImageView(AMApplication.getInstance().getApplicationContext());
        SmartApplication.REF_SMART_APPLICATION.getAQuery().
                id(tempImage).
                image(url, true, true, targetWidth, 0);

    }
}
