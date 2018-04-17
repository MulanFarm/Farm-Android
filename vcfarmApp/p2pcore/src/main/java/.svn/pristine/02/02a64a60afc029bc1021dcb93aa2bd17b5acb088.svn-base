package com.p2p.core.update;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.p2p.core.utils.MyUtils;



public class UpdateManager {
//	private static final String UPDATE_URL = "http://www.gwelltimes.com/upg/android/";
	private static final String UPDATE_URL = "http://upg1.cloudlinks.cn/upg/android/";
	//private static final String WEISUO = "http://www.gwelltimes.com/upg/30/00/";
//	private static final String UPDATE_URL = "http://c-telphone.com/upg/android/";
//	private static final String UPDATE_URL = "http://gxyaj.cn/upg/android/";
	public static final int HANDLE_MSG_DOWNING = 0X11;
	public static final int HANDLE_MSG_DOWN_SUCCESS = 0X12;
	public static final int HANDLE_MSG_DOWN_FAULT = 0X13;
	private boolean isDowning = false;
	private String version_server;
	private static UpdateManager manager = null;

	private int download_state;
	

	private UpdateManager(){};
	
	public synchronized static UpdateManager getInstance(){
		if(null==manager){
			synchronized(UpdateManager.class){
				manager = new UpdateManager();
			}
		}
		return manager;
	}
	
	public boolean getIsDowning(){
		return isDowning;
	}
	
	public void cancelDown(){
		isDowning = false;
	}
	
	public boolean checkUpdate(String AppId){
		boolean haveNewVersion = false;
		if(AppId==null||AppId.length()<=0){
			AppId="0517401";
		}
		try{
			//获取当前版本信息
			String version = MyUtils.getVersion();
			String[] version_parse = version.split("\\.");
			String url = UPDATE_URL+version_parse[0]+"/"+version_parse[1]+"/latestversion.asp?AppId="+AppId+"&version="+version;
			//解析服务器版本
			version_server = version;
			URL update_url = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) update_url.openConnection();
			BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
			byte[] buffer = new byte[128];
			int n;
			while((n=bis.read(buffer, 0, buffer.length))!=-1){
				version_server = new String(buffer);
			}
			bis.close();
			connection.disconnect();
			//当前版本与服务器版本比较
			String[] vaersion_server_parse = version_server.split("\\.");
			int version_count = Integer.parseInt((version_parse[2]+version_parse[3]).trim());
			int version_count_server = Integer.parseInt((vaersion_server_parse[2]+vaersion_server_parse[3]).trim());
			if(version_count<version_count_server){
				haveNewVersion = true;
			}else{
				haveNewVersion = false;
			}
		}catch(Exception e){
			haveNewVersion = false;
		}
		return haveNewVersion;
	}
//	public String getHelpUrl(){
//		try {
//			//获取当前版本信息
//			String version = MyUtils.getVersion();
//			String[] version_parse = version.split("\\.");
//			String url = UPDATE_URL+version_parse[0]+"/"+version_parse[1]+"/help.txt";
//			URL update_url = new URL(url);
//			HttpURLConnection connection = (HttpURLConnection) update_url.openConnection();
//			InputStreamReader isReader=new InputStreamReader(connection.getInputStream(),"utf-8");
//			BufferedReader bReader=new BufferedReader(isReader);
//			String line;
//			StringBuffer sb = new StringBuffer("");  
//			try {  
//		        while ((line = bReader.readLine()) != null) {  
//		            sb.append(line);  
//		        }  
//		    } catch (IOException e) {  
//		        e.printStackTrace();  
//		    } 
//			isReader.close();
//			bReader.close();
//			connection.disconnect();
//		    return sb.toString();  
//			
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			return "";
//		}
//	}
	public String getHelpUrl(){
		try {
			//获取当前版本信息
			String version = MyUtils.getVersion();
			String[] version_parse = version.split("\\.");
			String url = UPDATE_URL+version_parse[0]+"/"+version_parse[1]+"/help.asp";
			URL update_url = new URL(url);
			HttpURLConnection connection = (HttpURLConnection) update_url.openConnection();
			InputStreamReader isReader=new InputStreamReader(connection.getInputStream(),"utf-8");
			BufferedReader bReader=new BufferedReader(isReader);
			String line;
			StringBuffer sb = new StringBuffer("");  
			try {  
		        while ((line = bReader.readLine()) != null) {  
		            sb.append(line);  
		        }  
		    } catch (IOException e) {  
		        e.printStackTrace();  
		    } 
			isReader.close();
			bReader.close();
			connection.disconnect();
		    return sb.toString();  
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return "";
		}
	}
	
	public String getUpdateDescription(){
		String description = "";
		BufferedInputStream bis = null;
		try{
		String[] version_parse = MyUtils.getVersion().split("\\.");
		String url = UPDATE_URL+version_parse[0]+"/"+version_parse[1]+"/des_html.asp";
		URL update_url = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) update_url.openConnection();
		bis = new BufferedInputStream(connection.getInputStream());
		StringBuffer desBuffer = new StringBuffer();
		byte[] buffer = new byte[1024];
		int n;
		while((n=bis.read(buffer, 0, buffer.length))!=-1){
			desBuffer.append(new String(buffer,"utf-8"));
		}
		bis.close();
		connection.disconnect();
		description = desBuffer.toString();
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			try {
				if(bis!=null){bis.close();}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		description = description.trim();
		return description;
	}
	
	public String getUpdateDescription_en(){
		String description = "";
		BufferedInputStream bis = null;
		try{
		String[] version_parse = MyUtils.getVersion().split("\\.");
		String url = UPDATE_URL+version_parse[0]+"/"+version_parse[1]+"/des_html_en.asp";
		URL update_url = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) update_url.openConnection();
		bis = new BufferedInputStream(connection.getInputStream());
		StringBuffer desBuffer = new StringBuffer();
		byte[] buffer = new byte[1024];
		int n;
		while((n=bis.read(buffer, 0, buffer.length))!=-1){
			desBuffer.append(new String(buffer,"utf-8"));
		}
		bis.close();
		connection.disconnect();
		description = desBuffer.toString();
		}catch (Exception e){
			e.printStackTrace();
		}finally{
			try {
				if(bis!=null){bis.close();}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		description = description.trim();
		return description;
	}
	
	
	public void downloadApk(Handler handler,String filePath,String fileName){
		boolean isSuccess = true;
		int progress = 0;
		try {
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
				String savePath = Environment.getExternalStorageDirectory()+"/"+filePath;
				File dirfile = new File(savePath);
				if(!dirfile.exists()){
					dirfile.mkdirs();
				}
				
				File apkfile = new File(savePath+"/"+fileName);
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(apkfile));
				String[] version_server_parse = version_server.split("\\.");
				String[] version_parse = MyUtils.getVersion().split("\\.");
				URL down_url = new URL(UPDATE_URL+"/"+version_parse[0]+"/"+version_parse[1]+"/"+version_server.trim()+".apk");
				HttpURLConnection connection = (HttpURLConnection) down_url.openConnection();
				BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
				int fileLength = connection.getContentLength();
				int downLength = 0;
				int n;
				byte[] buffer = new byte[1024];
				isDowning = true;
				while((n=bis.read(buffer, 0, buffer.length))!=-1){
					if(!isDowning){
						isSuccess = false;
						break;
					}
					bos.write(buffer, 0, n);
					downLength +=n;
					progress = (int) (((float) downLength / fileLength) * 100);
					Message msg = new Message();
					msg.what = HANDLE_MSG_DOWNING;
					msg.arg1 = progress;
					handler.sendMessage(msg);
				}
				bis.close();
				bos.close();
				isDowning = false;
				connection.disconnect();
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			isDowning = false;
			isSuccess = false;
			e.printStackTrace();
		} 
		Message msg = new Message();
		msg.arg1 = progress;
		if(isSuccess){
			msg.what = HANDLE_MSG_DOWN_SUCCESS;
		}else{
			msg.what = HANDLE_MSG_DOWN_FAULT;
		}
		handler.sendMessage(msg);
	}
}
