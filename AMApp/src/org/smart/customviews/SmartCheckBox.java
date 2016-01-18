package org.smart.customviews;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatCheckBox;
import android.util.AttributeSet;

import org.smart.framework.SmartApplication;

/**
 * This Class Contains All Method Related To IjoomerCheckBox.
 *
 * @author tasol
 *
 */
public class SmartCheckBox extends AppCompatCheckBox{

	public SmartCheckBox(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	public SmartCheckBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SmartCheckBox(Context context) {
		super(context);
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
