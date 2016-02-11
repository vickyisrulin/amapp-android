/*
 * Copyright (c) 2016. Anoopam Mission. This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License at "http://www.gnu.org/licenses/licenses.html" for more details.
 *
 */

package org.anoopam.main.common;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

public class ExtendedViewPager extends ViewPager {

	public ExtendedViewPager(Context context) {
		super(context);
	}
	
	public ExtendedViewPager(Context context, AttributeSet attrs) {
	    super(context, attrs);
	}
	
	@Override
	protected boolean canScroll(View v, boolean checkV, int dx, int x, int y) {
	    if (v instanceof TouchImageView) {
	    	//
	    	// canScrollHorizontally is not supported for Api < 14. To get around this issue,
	    	// ViewPager is extended and canScrollHorizontallyFroyo, a wrapper around
	    	// canScrollHorizontally supporting Api >= 8, is called.
	    	//
	        return ((TouchImageView) v).canScrollHorizontallyFroyo(-dx);
	        
	    } else {
	        return super.canScroll(v, checkV, dx, x, y);
	    }
	}



}