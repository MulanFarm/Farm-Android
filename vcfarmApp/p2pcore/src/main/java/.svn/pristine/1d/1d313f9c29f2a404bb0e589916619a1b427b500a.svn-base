package com.p2p.shake;

import java.net.InetAddress;

import android.os.Handler;

public class ShakeManager {
	public static final int HANDLE_ID_SEARCH_END = 0x11;
	public static final int HANDLE_ID_RECEIVE_DEVICE_INFO = 0x12;
	public static final int HANDLE_ID_APDEVICE_END = 0x13;
	public static final int HANDLE_ID_ONEAPDEVICE = 0x14;
	
	private static ShakeManager manager = null;
	private ShakeThread shakeThread;
	private ShakeManager(){};
	private long searchTime = 10000;
	private InetAddress host;
	public synchronized static ShakeManager getInstance(){
		if(null==manager){
			synchronized(ShakeManager.class){
				manager = new ShakeManager();
			}
		}
		return manager;
	}
	
	public Handler handler;
	
	public void setHandler(Handler handler){
		this.handler = handler;
	}
	
	public void setSearchTime(long time){
		this.searchTime = time;
	}
	
	public void setInetAddress(InetAddress host){
		this.host = host;
	}
	
	public boolean shaking(){
		if(null==shakeThread){
			shakeThread = new ShakeThread(handler);
			shakeThread.setHandler(handler);
			shakeThread.setSearchTime(searchTime);
			shakeThread.setInetAddress(host);
			shakeThread.start();
			return true;
		}else{
			return false;
		}
	}
	
	public void stopShaking(){
		if(null!=shakeThread){
			shakeThread.killThread();
			shakeThread = null;
		}
	}
	
	public boolean isShaking(){
		if(null!=shakeThread){
			return true;
		}else{
			return false;
		}
	}
	
}
