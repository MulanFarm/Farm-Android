package com.p2p.core;

import java.util.HashMap;
import java.util.Set;

import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.p2p.core.global.Constants;
import com.p2p.core.utils.HomeWatcher;
import com.p2p.core.utils.OnHomePressedListener;

public abstract class BaseCoreActivity extends FragmentActivity implements
		OnHomePressedListener {
	private boolean isGoExit = false;
	protected static HashMap<Integer, Integer> activity_stack = new HashMap<Integer, Integer>();
	public HomeWatcher mHomeWatcher;

	private void onStackChange() {
		Set<Integer> keys = activity_stack.keySet();
		int start = 0;
		int stop = 0;
		for (Integer key : keys) {
			int status = activity_stack.get(key);
			if (status == Constants.ActivityStatus.STATUS_START) {
				start++;
			} else if (status == Constants.ActivityStatus.STATUS_STOP) {
				stop++;
			}
		}

		if (activity_stack.size() > 0 && start == 0) {
			if (!isGoExit) {
				onGoBack();
			}
		} else if (activity_stack.size() > 0 && start > 0) {
			onGoFront();
		}
		Log.e("my", "stack size:" + activity_stack.size() + "    start:"
				+ start + "  stop:" + stop);
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if (mHomeWatcher != null) {
			mHomeWatcher.stopWatch();
			mHomeWatcher = null;
		}
		activity_stack.put(getActivityInfo(),
				Constants.ActivityStatus.STATUS_STOP);
		onStackChange();
	}

	// 应用程序是否在前台运行
	protected boolean isOnFront() {
		Set<Integer> keys = activity_stack.keySet();
		int start = 0;
		int stop = 0;
		for (Integer key : keys) {
			int status = activity_stack.get(key);
			if (status == Constants.ActivityStatus.STATUS_START) {
				start++;
			} else if (status == Constants.ActivityStatus.STATUS_STOP) {
				stop++;
			}
		}

		if (activity_stack.size() > 0 && start == 0) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		mHomeWatcher = new HomeWatcher(this);
		mHomeWatcher.setOnHomePressedListener(this);
		mHomeWatcher.startWatch();
		activity_stack.put(getActivityInfo(),
				Constants.ActivityStatus.STATUS_START);
		onStackChange();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		activity_stack.remove(getActivityInfo());
		onStackChange();
	}

	// 每个继承的activity必须返回不同的值
	public abstract int getActivityInfo();

	protected void isGoExit(boolean isGoExit) {
		this.isGoExit = isGoExit;
	}

	// Home键按下时回调
	@Override
	public void onHomePressed() {
		// TODO Auto-generated method stub

	}

	// Home键长按下时回调
	@Override
	public void onHomeLongPressed() {
		// TODO Auto-generated method stub

	}
	// 进入后台
	protected abstract void onGoBack();

	// 进入前台
	protected abstract void onGoFront();

	// 退出应用
	protected abstract void onExit();
}
