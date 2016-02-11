/*
 * Copyright (c) 2016. Anoopam Mission. This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License at "http://www.gnu.org/licenses/licenses.html" for more details.
 *
 */

package org.anoopam.main.common;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

import org.anoopam.main.R;

public class SeekBarBackgroundDrawable extends Drawable {

    private Paint mPaintLine = new Paint();
    private Paint mPaintBackground = new Paint();
    private float dy;
    private float mPaddingLeft;
    private float mPaddingRight;


    public SeekBarBackgroundDrawable(Context ctx, int lineColor, int backgroundColor, final float paddingLeft, final float paddingRight) {
        mPaddingLeft = paddingLeft;
        mPaddingRight = paddingRight;
        mPaintLine.setColor(lineColor);

        mPaintBackground.setColor(backgroundColor);
        dy = ctx.getResources()
                .getDimension(R.dimen.one_dp);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRect(getBounds().left,
                getBounds().top,
                getBounds().right,
                getBounds().bottom,
                mPaintBackground);

        canvas.drawRect(getBounds().left+mPaddingLeft,
                getBounds().centerY() - dy / 2,
                getBounds().right-mPaddingRight,
                getBounds().centerY() + dy / 2,
                mPaintLine);
    }


    @Override
    public void setAlpha(final int alpha) {

    }


    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }

}