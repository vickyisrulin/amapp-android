package org.anoopam.main.anoopamaudio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

public class NotificationBroadcast extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_MEDIA_BUTTON)) {
            KeyEvent keyEvent = (KeyEvent) intent.getExtras().get(Intent.EXTRA_KEY_EVENT);
            if (keyEvent.getAction() != KeyEvent.ACTION_DOWN)
                return;

            switch (keyEvent.getKeyCode()) {
                case KeyEvent.KEYCODE_HEADSETHOOK:
                case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
                	if(!PlayerConstants.SONG_PAUSED){
    					Controls.pauseControl(context);
                	}else{
    					Controls.playControl(context);
                	}
                	break;
                case KeyEvent.KEYCODE_MEDIA_PLAY:
                	break;
                case KeyEvent.KEYCODE_MEDIA_PAUSE:
                	break;
                case KeyEvent.KEYCODE_MEDIA_STOP:
                	break;
                case KeyEvent.KEYCODE_MEDIA_NEXT:
                	Log.d("TAG", "TAG: KEYCODE_MEDIA_NEXT");
                	Controls.nextControl(context);
                	break;
                case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
                	Log.d("TAG", "TAG: KEYCODE_MEDIA_PREVIOUS");
                	Controls.previousControl(context);
                	break;
            }
		}  else{
            	if (intent.getAction().equals(AudioService.NOTIFY_PLAY)) {
    				Controls.playControl(context);
        		} else if (intent.getAction().equals(AudioService.NOTIFY_PAUSE)) {
    				Controls.pauseControl(context);
        		} else if (intent.getAction().equals(AudioService.NOTIFY_NEXT)) {
        			Controls.nextControl(context);
        		} else if (intent.getAction().equals(AudioService.NOTIFY_DELETE)) {
					Intent i = new Intent(context, AudioService.class);
					context.stopService(i);

					AudioListActivity.stopPlayer();

					Intent in = new Intent(context, AudioListActivity.class);
					in.putExtra(AudioListActivity.AUDIO_LIST, PlayerConstants.CATEGORY);
			        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			        context.startActivity(in);

        		}else if (intent.getAction().equals(AudioService.NOTIFY_PREVIOUS)) {
    				Controls.previousControl(context);
        		} else if (intent.getAction().equals(AudioService.NOTIFY_AUDIO_LIST)) {
					Intent in = new Intent(context, AudioListActivity.class);
					in.putExtra(AudioListActivity.AUDIO_LIST, PlayerConstants.CATEGORY);
					in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(in);
				}
		}
	}
	
	public String ComponentName() {
		return this.getClass().getName(); 
	}
}
