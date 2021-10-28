package com.example.activity;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class AppManager {

	public static final boolean DEBUG_MODE = true;
	
	private static AppManager sInstance;
	private Activity activity;
	private Location myLocation;
	private static AppManager manager = new AppManager();
	private ImageLoaderConfiguration config;
	private DisplayImageOptions options;
	SAPServiceProvider service;
	String SENDER_ID="996274448898";
	

	public SAPServiceProvider getService() {
		return service;
	}

	public void setService(SAPServiceProvider sapServiceProvider) {
		// TODO Auto-generated method stub
		service = sapServiceProvider;
	}
	/**
	 * @return
	 */
	public boolean isPossibleInternet()
	{
		// 네트워크 연결 상태 확인하는 로직
		ConnectivityManager cm = (ConnectivityManager) getActivity()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		boolean isWifiConn = ni.isConnected();
		ni = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		boolean isMobileConn = ni.isConnected();

		if (!isWifiConn && !isMobileConn)
		{
			return false;
		}
		return true;
	}

	public static AppManager getsInstance() {
		return sInstance;
	}


	public static void setsInstance(AppManager sInstance) {
		AppManager.sInstance = sInstance;
	}

	public static AppManager getManager() {
		return manager;
	}


	public static void setManager(AppManager manager) {
		AppManager.manager = manager;
	}

	public ImageLoaderConfiguration getConfig() {
		return config;
	}


	public void setConfig(ImageLoaderConfiguration config) {
		this.config = config;
	}


	public DisplayImageOptions getOptions() {
		return options;
	}


	public void setOptions(DisplayImageOptions options) {
		this.options = options;
	}
	public Activity getActivity() {
		return activity;
	}
	public void setActivity(Activity activity) {
		this.activity = activity;
	}
	public Location getMyLocation() {
		return myLocation;
	}
	public void setMyLocation(Location myLocation) {
		this.myLocation = myLocation;
	}
	public static AppManager getInstance() {
		return manager;
	}



}
