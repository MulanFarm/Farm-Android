package com.p2p.core;

import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
//import android.widget.RelativeLayout;
import android.widget.LinearLayout.LayoutParams;

import com.p2p.core.global.Constants;
import com.p2p.core.utils.MyUtils;

public abstract class BaseVideoActivity extends BaseCoreActivity {
	public static int mVideoFrameRate = 15;
	private final int MINX = 50;
	private final int MINY = 25;
	private final int USR_CMD_OPTION_PTZ_TURN_LEFT = 0;
	private final int USR_CMD_OPTION_PTZ_TURN_RIGHT = 1;
	private final int USR_CMD_OPTION_PTZ_TURN_UP = 2;
	private final int USR_CMD_OPTION_PTZ_TURN_DOWN = 3;
	public P2PView pView;
	boolean isBaseRegFilter = false;
	boolean isFullScreen = false;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		baseRegFilter();
	}

	public void baseRegFilter() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(Constants.P2P_WINDOW.Action.P2P_WINDOW_READY_TO_START);
		this.registerReceiver(baseReceiver, filter);
		isBaseRegFilter = true;
	}

	private BroadcastReceiver baseReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context arg0, Intent intent) {
			if (intent.getAction().equals(
					Constants.P2P_WINDOW.Action.P2P_WINDOW_READY_TO_START)) {
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

	public void initP2PView(int type) {
		pView.setCallBack();
		pView.setGestureDetector(new GestureDetector(this,
				new GestureListener(), null, true));
		pView.setDeviceType(type);
	}

	private class GestureListener extends
			GestureDetector.SimpleOnGestureListener {
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
			if (!isFullScreen) {
				isFullScreen = true;
				pView.fullScreen();
			} else {
				isFullScreen = false;
				pView.halfScreen();
			}
			return super.onDoubleTap(e);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			// int id = -1;
			// float distance = 0;
			// boolean ishorizontal = false;
			// if ((Math.abs(e2.getX()- e1.getX())) > (Math.abs(e2.getY()-
			// e1.getY()))) {
			// ishorizontal = true;
			// }
			//
			// if (ishorizontal) {
			// distance = e2.getX() - e1.getX();
			// if (Math.abs(distance) > MyUtils.dip2px(BaseVideoActivity.this,
			// MINX)) {
			// if (distance > 0) {
			// id = USR_CMD_OPTION_PTZ_TURN_RIGHT;
			// }
			// else {
			// id = USR_CMD_OPTION_PTZ_TURN_LEFT;
			// }
			// }
			// }
			// else {
			// distance = e2.getY() - e1.getY();
			// if (Math.abs(distance) > MyUtils.dip2px(BaseVideoActivity.this,
			// MINY)) {
			// if (distance > 0) {
			// id = USR_CMD_OPTION_PTZ_TURN_UP;
			// }
			// else {
			// id = USR_CMD_OPTION_PTZ_TURN_DOWN;
			// }
			// }
			// }
			//
			// if (id != -1) {
			// MediaPlayer.getInstance().native_p2p_control(id);
			// } else {
			// }

			return true;
		}
	}

	public void fillCameraData(byte[] data, int length, int PreviewWidth,
			int PreviewHeight, int isYUV) {
		MediaPlayer.getInstance()._FillVideoRawFrame(data, data.length,
				PreviewWidth, PreviewHeight, isYUV);
	}

	public void setMute(boolean bool) {
		try {
			MediaPlayer.getInstance()._SetMute(bool);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean closeLocalCamera() {
		if (MediaPlayer.iLocalVideoControl(1) == 1) {
			return true;
		} else {
			return false;
		}
	}

	public boolean openLocalCamera() {
		if (MediaPlayer.iLocalVideoControl(0) == 1) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (isBaseRegFilter) {
			this.unregisterReceiver(baseReceiver);
			isBaseRegFilter = false;
		}
	}

	protected abstract void onP2PViewSingleTap();

}
