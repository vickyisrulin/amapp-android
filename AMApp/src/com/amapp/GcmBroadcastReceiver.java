package com.amapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;



/**
 * This Class Contains All Method Related To GCMReceiver.
 * 
 * @author tasol
 * 
 */
public class GcmBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		ComponentName comp = new ComponentName(context.getPackageName(),
				com.amapp.GcmIntentService.class.getName());
		context.startService((intent.setComponent(comp)));
		setResultCode(Activity.RESULT_OK);
	}

}
