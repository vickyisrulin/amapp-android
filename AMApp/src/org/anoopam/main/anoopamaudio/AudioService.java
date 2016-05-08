/*
 * Copyright (c) 2016. Anoopam Mission. This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or any later version.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License at "http://www.gnu.org/licenses/licenses.html" for more details.
 *
 */

package org.anoopam.main.anoopamaudio;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.RemoteControlClient;
import android.media.RemoteControlClient.MetadataEditor;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.RemoteViews;

import org.anoopam.ext.smart.framework.SharedPreferenceConstants;
import org.anoopam.ext.smart.framework.SmartApplication;
import org.anoopam.ext.smart.framework.SmartUtils;
import org.anoopam.main.R;
import org.anoopam.main.common.AMConstants;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class AudioService extends Service implements AudioManager.OnAudioFocusChangeListener,SharedPreferenceConstants {
	String LOG_CLASS = "SongService";
	public MediaPlayer mp;
	int NOTIFICATION_ID = 1111;
	public static final String NOTIFY_PREVIOUS = "org.anoopam.main.previous";
	public static final String NOTIFY_DELETE = "org.anoopam.main.delete";
	public static final String NOTIFY_PAUSE = "org.anoopam.main.pause";
	public static final String NOTIFY_PLAY = "org.anoopam.main.play";
	public static final String NOTIFY_NEXT = "org.anoopam.main.next";
	public static final String NOTIFY_AUDIO_LIST = "org.anoopam.main.audiolist";

	private ComponentName remoteComponentName;
	private RemoteControlClient remoteControlClient;
	AudioManager audioManager;
	Bitmap mDummyAlbumArt;
	private static Timer timer;
	private static boolean currentVersionSupportBigNotification = false;
	private static boolean currentVersionSupportLockScreenControls = false;

	public static AudioService getInstance() {
		return audioService;
	}

	private static AudioService audioService;

	private DownloadManager downloadManager;
	private DownloadManagerPro downloadManagerPro;

	private boolean wasPlaying=false;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		mp = new MediaPlayer();
		audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

		currentVersionSupportBigNotification = UtilFunctions.currentVersionSupportBigNotification();
		currentVersionSupportLockScreenControls = UtilFunctions.currentVersionSupportLockScreenControls();
		timer = new Timer();
		mp.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				Controls.nextControl(getApplicationContext());
			}
		});
		audioService = this;
		super.onCreate();
	}

	/**
	 * Send message from timer
	 * @author jonty.ankit
	 */
	private class MainTask extends TimerTask{
		public void run(){
			handler.sendEmptyMessage(0);
		}
	}

	private final Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg){
			if(mp != null){
				int progress = (mp.getCurrentPosition()*100) / mp.getDuration();
				Integer i[] = new Integer[3];
				i[0] = mp.getCurrentPosition();
				i[1] = mp.getDuration();
				i[2] = progress;
				try{
					PlayerConstants.PROGRESSBAR_HANDLER.sendMessage(PlayerConstants.PROGRESSBAR_HANDLER.obtainMessage(0, i));
				}catch(Exception e){}
			}
		}
	};

	@SuppressLint("NewApi")
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		downloadManager = (DownloadManager)getSystemService(DOWNLOAD_SERVICE);
		downloadManagerPro = new DownloadManagerPro(downloadManager);

		try {
			if(PlayerConstants.SONGS_LIST.size() <= 0){
				PlayerConstants.SONGS_LIST = UtilFunctions.listOfSongs(getApplicationContext());
			}
			final ContentValues data = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER);
			if(currentVersionSupportLockScreenControls){
				RegisterRemoteClient();
			}

			String songPath = data.getAsString("audioURL");



			final File destination = new File(SmartUtils.getAudioStorage(PlayerConstants.CATEGORY.getAsString("catName"))+ File.separator + URLUtil.guessFileName(songPath, null, null));

			if(destination.exists()){
				SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(AMConstants.KEY_CURRENT_AUDIO,songPath);
				SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(AMConstants.KEY_CURRENT_AUDIO_NAME,data.getAsString("audioTitle"));
				SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(AMConstants.KEY_CURRENT_CAT_NAME, PlayerConstants.CATEGORY.getAsString("catName"));
				AudioListActivity.currentAudio= songPath;
				playSong(destination.getAbsolutePath(), data);
				newNotification();

			}

			PlayerConstants.SONG_CHANGE_HANDLER = new Handler(new Callback() {
				@Override
				public boolean handleMessage(Message msg) {
					final ContentValues data = PlayerConstants.SONGS_LIST.get(PlayerConstants.SONG_NUMBER);
					String songPath = data.getAsString("audioURL");



					final File destination = new File(SmartUtils.getAudioStorage(PlayerConstants.CATEGORY.getAsString("catName"))+ File.separator + URLUtil.guessFileName(songPath, null, null));

					if(destination.exists()){

						int[] bytesAndStatus = downloadManagerPro.getBytesAndStatus(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getLong(songPath,0L));

						if(!AudioListActivity.isDownloading((Integer) bytesAndStatus[2])){
							SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(AMConstants.KEY_CURRENT_AUDIO,songPath);
							SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(AMConstants.KEY_CURRENT_AUDIO_NAME,data.getAsString("audioTitle"));
							SmartApplication.REF_SMART_APPLICATION.writeSharedPreferences(AMConstants.KEY_CURRENT_CAT_NAME, PlayerConstants.CATEGORY.getAsString("catName"));
							AudioListActivity.currentAudio= songPath;
							try{
								playSong(destination.getAbsolutePath(), data);
								newNotification();
								AudioListActivity.changeUI();
							}catch(Exception e){
								e.printStackTrace();
							}
						}else{
							Controls.nextControl(getApplicationContext());
						}


					}else{
						Controls.nextControl(getApplicationContext());
					}

					return false;
				}
			});

			PlayerConstants.PLAY_PAUSE_HANDLER = new Handler(new Callback() {
				@Override
				public boolean handleMessage(Message msg) {
					String message = (String)msg.obj;
					if(mp == null)
						return false;
					if(message.equalsIgnoreCase(getResources().getString(R.string.play))){
						PlayerConstants.SONG_PAUSED = false;
						if(currentVersionSupportLockScreenControls){
							remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
						}
						mp.start();
					}else if(message.equalsIgnoreCase(getResources().getString(R.string.pause))){
						PlayerConstants.SONG_PAUSED = true;
						if(currentVersionSupportLockScreenControls){
							remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PAUSED);
						}
						mp.pause();

					}else if(message.startsWith("seek")){
						mp.seekTo(Integer.parseInt(message.split(":")[1]));
					}


					newNotification();
					try{
						AudioListActivity.changeButton();
						AudioListActivity.updateUI();
//						AudioPlayerActivity.changeButton();
					}catch(Exception e){}
					Log.d("TAG", "TAG Pressed: " + message);
					return false;
				}
			});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return START_STICKY;
	}

	/**
	 * Notification
	 * Custom Bignotification is available from API 16
	 */
	@SuppressLint("NewApi")
	private void newNotification() {


		RemoteViews simpleContentView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.custom_notification);
		RemoteViews expandedView = new RemoteViews(getApplicationContext().getPackageName(), R.layout.big_notification);

		Notification notification = new NotificationCompat.Builder(getApplicationContext())
				.setSmallIcon(R.drawable.ic_music)
				.setContentTitle(SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(AMConstants.KEY_CURRENT_AUDIO_NAME, "")).build();

		setListeners(simpleContentView);
		setListeners(expandedView);
		
		notification.contentView = simpleContentView;
		if(currentVersionSupportBigNotification){
			notification.bigContentView = expandedView;
		}
		
		try{
			notification.contentView.setImageViewResource(R.id.imageViewAlbumArt, R.drawable.logo);
			if(currentVersionSupportBigNotification){
				notification.bigContentView.setImageViewResource(R.id.imageViewAlbumArt, R.drawable.logo);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		if(PlayerConstants.SONG_PAUSED){
			notification.contentView.setViewVisibility(R.id.btnPause, View.GONE);
			notification.contentView.setViewVisibility(R.id.btnPlay, View.VISIBLE);

			if(currentVersionSupportBigNotification){
				notification.bigContentView.setViewVisibility(R.id.btnPause, View.GONE);
				notification.bigContentView.setViewVisibility(R.id.btnPlay, View.VISIBLE);
			}
		}else{
			notification.contentView.setViewVisibility(R.id.btnPause, View.VISIBLE);
			notification.contentView.setViewVisibility(R.id.btnPlay, View.GONE);

			if(currentVersionSupportBigNotification){
				notification.bigContentView.setViewVisibility(R.id.btnPause, View.VISIBLE);
				notification.bigContentView.setViewVisibility(R.id.btnPlay, View.GONE);
			}
		}


		notification.contentView.setTextViewText(R.id.textCustomNotificationAlbumName, SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(AMConstants.KEY_CURRENT_CAT_NAME, ""));
		notification.contentView.setTextViewText(R.id.textCustomNotificationSongName, SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(AMConstants.KEY_CURRENT_AUDIO_NAME, ""));

		if(currentVersionSupportBigNotification){
			notification.bigContentView.setTextViewText(R.id.textBitNotificationAlbumName, SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(AMConstants.KEY_CURRENT_CAT_NAME, ""));
			notification.bigContentView.setTextViewText(R.id.textBigNotificationSongName, SmartApplication.REF_SMART_APPLICATION.readSharedPreferences().getString(AMConstants.KEY_CURRENT_AUDIO_NAME, ""));
		}

		notification.flags |= Notification.FLAG_ONGOING_EVENT;
		startForeground(NOTIFICATION_ID, notification);
	}
	
	/**
	 * Notification click listeners
	 * @param view
	 */
	public void setListeners(RemoteViews view) {
		Intent previous = new Intent(NOTIFY_PREVIOUS);
		Intent delete = new Intent(NOTIFY_DELETE);
		Intent pause = new Intent(NOTIFY_PAUSE);
		Intent next = new Intent(NOTIFY_NEXT);
		Intent play = new Intent(NOTIFY_PLAY);
		Intent audioList = new Intent(NOTIFY_AUDIO_LIST);

		PendingIntent pPrevious = PendingIntent.getBroadcast(getApplicationContext(), 0, previous, PendingIntent.FLAG_UPDATE_CURRENT);
		view.setOnClickPendingIntent(R.id.btnPrevious, pPrevious);

		PendingIntent pDelete = PendingIntent.getBroadcast(getApplicationContext(), 0, delete, PendingIntent.FLAG_UPDATE_CURRENT);
		view.setOnClickPendingIntent(R.id.btnDelete, pDelete);
		
		PendingIntent pPause = PendingIntent.getBroadcast(getApplicationContext(), 0, pause, PendingIntent.FLAG_UPDATE_CURRENT);
		view.setOnClickPendingIntent(R.id.btnPause, pPause);
		
		PendingIntent pNext = PendingIntent.getBroadcast(getApplicationContext(), 0, next, PendingIntent.FLAG_UPDATE_CURRENT);
		view.setOnClickPendingIntent(R.id.btnNext, pNext);
		
		PendingIntent pPlay = PendingIntent.getBroadcast(getApplicationContext(), 0, play, PendingIntent.FLAG_UPDATE_CURRENT);
		view.setOnClickPendingIntent(R.id.btnPlay, pPlay);

		PendingIntent pAudioList = PendingIntent.getBroadcast(getApplicationContext(), 0, audioList, PendingIntent.FLAG_UPDATE_CURRENT);
		view.setOnClickPendingIntent(R.id.imageViewAlbumArt, pAudioList);

	}
	
	@Override
	public void onDestroy() {
		if(mp != null){
			mp.stop();
			mp = null;
		}
		super.onDestroy();
	}

	/**
	 * Play song, Update Lockscreen fields
	 * @param songPath
	 * @param data
	 */
	@SuppressLint("NewApi")
	private void playSong(String songPath, ContentValues data) {
		try {
			if(currentVersionSupportLockScreenControls){
				UpdateMetadata(data);
				remoteControlClient.setPlaybackState(RemoteControlClient.PLAYSTATE_PLAYING);
			}
			mp.reset();
			mp.setDataSource(songPath);
			mp.prepare();
			mp.start();
			timer.scheduleAtFixedRate(new MainTask(), 100, 100);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@SuppressLint("NewApi")
	private void RegisterRemoteClient(){
		remoteComponentName = new ComponentName(getApplicationContext(), new NotificationBroadcast().ComponentName());
		try {
			if(remoteControlClient == null) {
				audioManager.registerMediaButtonEventReceiver(remoteComponentName);
				Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
				mediaButtonIntent.setComponent(remoteComponentName);
				PendingIntent mediaPendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0);
				remoteControlClient = new RemoteControlClient(mediaPendingIntent);
				audioManager.registerRemoteControlClient(remoteControlClient);
			}
			remoteControlClient.setTransportControlFlags(
					RemoteControlClient.FLAG_KEY_MEDIA_PLAY |
							RemoteControlClient.FLAG_KEY_MEDIA_PAUSE |
							RemoteControlClient.FLAG_KEY_MEDIA_PLAY_PAUSE |
							RemoteControlClient.FLAG_KEY_MEDIA_STOP |
							RemoteControlClient.FLAG_KEY_MEDIA_PREVIOUS |
							RemoteControlClient.FLAG_KEY_MEDIA_NEXT);
		}catch(Exception ex) {
		}
	}

	@SuppressLint("NewApi")
	private void UpdateMetadata(ContentValues data){
		if (remoteControlClient == null){
			return;
		}

		try{
			MetadataEditor metadataEditor = remoteControlClient.editMetadata(true);
			metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_ALBUM, data.getAsString(PlayerConstants.CATEGORY.getAsString("catName")));
			metadataEditor.putString(MediaMetadataRetriever.METADATA_KEY_TITLE, data.getAsString("audioTitle"));
			mDummyAlbumArt = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
			metadataEditor.putBitmap(MetadataEditor.BITMAP_KEY_ARTWORK, mDummyAlbumArt);
			metadataEditor.apply();
			audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
		}catch (Throwable e){
			e.printStackTrace();
		}
	}

	@Override
	public void onAudioFocusChange(int focusChange) {


		switch (focusChange) {
			case AudioManager.AUDIOFOCUS_LOSS:
			case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
			case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
				try{
					wasPlaying = mp.isPlaying();
					Controls.pauseControl(this);
				}catch (Throwable e){
					e.printStackTrace();
				}
				break;
			case AudioManager.AUDIOFOCUS_GAIN:
			case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:
			case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK:

				if(wasPlaying){
					wasPlaying=false;
					Controls.playControl(this);
				}
				break;
		}

	}
}