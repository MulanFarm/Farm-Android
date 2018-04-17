package com.p2p.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.MotionEvent;

import com.p2p.core.global.Constants;

import java.io.IOException;

public abstract class BasePlayBackActivity extends BaseCoreActivity{
	private static int mVideoFrameRate = 15;
	public P2PView pView;
	boolean isBaseRegFilter = false;
	boolean isFullScreen = false;
	
	private static final int PAUSE = 2;
	private static final int START = 3;
	private static final int JUMP = 4;
	private static final int NEXT = 5;
	private static final int PREVIOUS = 6;
	private static final int NAMEPLAY = 7;
	
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		baseRegFilter();
	}
	
	public void baseRegFilter(){
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.P2P_WINDOW.Action.P2P_WINDOW_READY_TO_START);
		this.registerReceiver(baseReceiver, filter);
		isBaseRegFilter = true;
	}
	
	private BroadcastReceiver baseReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context arg0, Intent intent) {
			if(intent.getAction().equals(Constants.P2P_WINDOW.Action.P2P_WINDOW_READY_TO_START)){
				final MediaPlayer mPlayer = MediaPlayer.getInstance();
				new Thread(new Runnable() {
					@Override
					public void run() {
						MediaPlayer.nativeInit(mPlayer);
						try {
							mPlayer.setDisplay(pView);
							
						} catch (IOException e) {
							e.printStackTrace();
						}
						mPlayer.start(mVideoFrameRate);
					}
				}).start();
			}
		}
	};
	
	public void initP2PView(int type){
		pView.setCallBack();
		pView.setGestureDetector(new GestureDetector(this, new GestureListener(), null, true));
		pView.setDeviceType(type);
	}
	
	private class GestureListener extends GestureDetector.SimpleOnGestureListener {
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			onP2PViewSingleTap();
			return super.onSingleTapConfirmed(e);
		}


		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return super.onSingleTapUp(e);
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			if(!isFullScreen){
				isFullScreen = true;
				pView.fullScreen();
			}else{
				isFullScreen = false;
				pView.halfScreen();
			}
			return super.onDoubleTap(e);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			
			return super.onFling(e1, e2, velocityX, velocityY);
		}
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		if(isBaseRegFilter){
			this.unregisterReceiver(baseReceiver);
			isBaseRegFilter = false;
		}
	}
	
	public void pausePlayBack(){
		MediaPlayer.iRecFilePlayingControl(PAUSE,0,"test".getBytes());
	}
	
	public void startPlayBack(){
		MediaPlayer.iRecFilePlayingControl(START,0,"test".getBytes());
	}
	
	public boolean previous(String filename){
		if(MediaPlayer.iRecFilePlayingControl(NAMEPLAY,0,filename.getBytes())==0){
			return false;
		}else{
			return true;
		}
	}
	
	public boolean next(String filename){
		if(MediaPlayer.iRecFilePlayingControl(NAMEPLAY,0,filename.getBytes())==0){
			return false;
		}else{
			return true;
		}
	}
	
	public void jump(int value){
		MediaPlayer.iRecFilePlayingControl(JUMP,value,"test".getBytes());
	}
	
	protected abstract void onP2PViewSingleTap();
}	
