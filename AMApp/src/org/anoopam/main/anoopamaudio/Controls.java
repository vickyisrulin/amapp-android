/*
 * Copyright (c) 2016. Anoopam Mission. This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License at "http://www.gnu.org/licenses/licenses.html" for more details.
 *
 */

package org.anoopam.main.anoopamaudio;

import android.content.Context;

import org.anoopam.main.R;


public class Controls {
	static String LOG_CLASS = "Controls";
	public static void playControl(Context context) {
		sendMessage(context.getResources().getString(R.string.play));
	}

	public static void pauseControl(Context context) {
		sendMessage(context.getResources().getString(R.string.pause));
	}
	public static void seekControl(Context context,long msec) {
		sendMessage("seek:"+ (msec));
	}

	public static void nextControl(Context context) {
		boolean isServiceRunning = UtilFunctions.isServiceRunning(AudioService.class.getName(), context);
		if (!isServiceRunning)
			return;
		if(PlayerConstants.SONGS_LIST.size() > 0 ){
			if(PlayerConstants.SONG_NUMBER < (PlayerConstants.SONGS_LIST.size()-1)){
				PlayerConstants.SONG_NUMBER++;
				PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
			}else{
				PlayerConstants.SONG_NUMBER = 0;
				PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
			}
		}
		PlayerConstants.SONG_PAUSED = false;
	}

	public static void previousControl(Context context) {
		boolean isServiceRunning = UtilFunctions.isServiceRunning(AudioService.class.getName(), context);
		if (!isServiceRunning)
			return;
		if(PlayerConstants.SONGS_LIST.size() > 0 ){
			if(PlayerConstants.SONG_NUMBER > 0){
				PlayerConstants.SONG_NUMBER--;
				PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
			}else{
				PlayerConstants.SONG_NUMBER = PlayerConstants.SONGS_LIST.size() - 1;
				PlayerConstants.SONG_CHANGE_HANDLER.sendMessage(PlayerConstants.SONG_CHANGE_HANDLER.obtainMessage());
			}
		}
		PlayerConstants.SONG_PAUSED = false;
	}
	
	private static void sendMessage(String message) {
		try{
			PlayerConstants.PLAY_PAUSE_HANDLER.sendMessage(PlayerConstants.PLAY_PAUSE_HANDLER.obtainMessage(0, message));
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
