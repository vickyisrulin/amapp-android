package com.smart.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.smart.framework.SmartApplication;

/**
 * This Class Contains All Method Related To IjoomerTextView.
 *
 * @author tasol
 */
public class SmartTextView extends AppCompatTextView {


    private Context context;

    public SmartTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public SmartTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public SmartTextView(Context context) {
        super(context);
        this.context = context;
        init(context);
    }

    private void init(Context mContext) {

        try {
            if (SmartApplication.REF_SMART_APPLICATION.FONT != null) {
                if (getTypeface() != null) {
                    setTypeface(SmartApplication.REF_SMART_APPLICATION.FONT, getTypeface().getStyle());
                } else {
                    setTypeface(SmartApplication.REF_SMART_APPLICATION.FONT);
                }
            } else {
                Typeface tf = Typeface.createFromAsset(mContext.getAssets(), SmartApplication.REF_SMART_APPLICATION.FONT_NAME);
                if (getTypeface() != null) {
                    setTypeface(tf, getTypeface().getStyle());
                } else {
                    setTypeface(SmartApplication.REF_SMART_APPLICATION.FONT);
                }
                SmartApplication.REF_SMART_APPLICATION.FONT=tf;
            }
        } catch (Throwable e) {
        }
    }


}
