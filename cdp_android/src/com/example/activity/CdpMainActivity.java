package com.example.activity;

import java.io.IOException;
import java.net.SocketTimeoutException;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.Toast;

import com.example.R;
import com.example.network.HttpPostMethod;
import com.example.service.CdpLocationService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import edu.cmu.pocketsphinx.demo.PocketSphinxActivity;

public class CdpMainActivity extends TabActivity 
{
	private TabHost mTabHost = null;
	private TabWidget mTabWidget = null;
	private GoogleCloudMessaging gcm;
	public static final String EXTRA_MESSAGE = "message";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private String regId;
	final int ONE = 0;
    final int TWO = 1;
    final int THREE = 2;
    final int FOUR = 3;
    final int FIVE = 4;
    final int SIX = 5;
    final int SEVEN = 6;
    final int EIGHT = 7;
    final int NINE = 8;
    final int TEN = 9;
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabbar_activity);
		gcmInit();
		// Tab
		mTabHost = (TabHost)findViewById(android.R.id.tabhost);
		mTabWidget = this.getTabWidget();
		
		TabSpec tabSpec = null;
		Intent intent = null;
		
		// Home Tab (first tab)
		intent = new Intent(this, InfoActivity.class);
		
		tabSpec = mTabHost.newTabSpec("info");
		tabSpec.setIndicator("", getResources().getDrawable(R.drawable.tab01));
		tabSpec.setContent(intent);
		mTabHost.addTab(tabSpec);
		
		// Upload Tab (second tab)		
		intent = new Intent(this, MeActivity.class);
		
		tabSpec = mTabHost.newTabSpec("me");
		tabSpec.setIndicator("", getResources().getDrawable(R.drawable.tab02));
		tabSpec.setContent(intent);
		mTabHost.addTab(tabSpec);
		
		// Song Tab (third tab)
		intent = new Intent(this, CommunityActivity.class);
		
		tabSpec = mTabHost.newTabSpec("community");
		tabSpec.setIndicator("", getResources().getDrawable(R.drawable.tab04));
		tabSpec.setContent(intent);
		mTabHost.addTab(tabSpec);
		
		// Short cut Tab (forth tab)
		intent = new Intent(this, FriendActivity.class);
		
		tabSpec = mTabHost.newTabSpec("friends");
		tabSpec.setIndicator("", getResources().getDrawable(R.drawable.tab03));
		tabSpec.setContent(intent);
		mTabHost.addTab(tabSpec);
		// 배경색 없애기.
		for (int i = 0; i < 4; i++) {
			mTabHost.getTabWidget().getChildAt(i).setBackgroundColor(0x00ffffff);
		}
	}
	 private boolean servicesConnected() {
	       // Check that Google Play services is available
	       int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

	       // If Google Play services is available
	       if (ConnectionResult.SUCCESS == resultCode) {
	    	   // In debug mode, log the status
	           //Log.d(");

	    	   // Continue
	    	   return true;
	    	   // Google Play services was not available for some reason
	       } else {
	    	   // Display an error dialog
	           return false;
	       }
	    }
		public void gcmInit() {
			if (servicesConnected()) {
				gcm = GoogleCloudMessaging.getInstance(this);
		        regId = getRegistrationId(this);
		 
		        if (regId.equals("")) {
		        	Log.i("reg", "regid가 없어서 새로 등록합니다");
		            registerInBackground();
		        }
		        Log.i("reg", " regid : " + regId);
		    } 
		    else
		    {
		    	//PRINT_LOG(LOG_TAG, "구글 플레이 서비스가 없습니다.");
		    }
		}
		
		 private void registerInBackground() {
		      new AsyncTask<Void, Integer, String>() {
		         @Override
		         protected String doInBackground(Void... params) {
		            String msg = "";
		            try {
		               Context context = getApplicationContext();
		               if (gcm == null) {
		                  gcm = GoogleCloudMessaging.getInstance(context);
		               }
		               regId = gcm.register(AppManager.getInstance().SENDER_ID);
		               msg = "Device registered, registration ID=" + regId;
		               Log.i("reg", "등록된 regid : " + regId);
		              // PRINT_LOG(LOG_TAG, "등록된 regid : " + regid);

		               sendRegistrationIdToBackend();

		               storeRegistrationId(context, regId);
		            } catch (SocketTimeoutException ste) {
					} catch (IOException ex) {
		               msg = "Error :" + ex.getMessage();
		            }
		            return msg;
		         }
		         protected void onProgressUpdate(Integer... progress) {

		         }


		         @Override
		         protected void onPostExecute(String msg) {
		            // mDisplay.append(msg + "\n");
		           // PRINT_LOG(LOG_TAG, "register REG Id 끝");
		         }


		         // 나의 서버로 regId를 전송하는 메소드
		         private void sendRegistrationIdToBackend() {
		        	 HttpPostMethod post = new HttpPostMethod(handler, "join.do", regId);
		        	 post.start();
		         }
		         private void storeRegistrationId(Context context, String regId) {
			            final SharedPreferences prefs = getGCMPreferences(context);
			            int appVersion = getAppVersion(context);
			           // Log.i(LOG_TAG, "Saving regId on app version " + appVersion);
			            SharedPreferences.Editor editor = prefs.edit();
			            editor.putString(PROPERTY_REG_ID, regId);
			            editor.putInt(PROPERTY_APP_VERSION, appVersion);
			            editor.commit();
			         }
		      }.execute(null, null, null);

		   }
		
		 Handler handler = new Handler() 
		{
			public void handleMessage(android.os.Message msg)
			{
				switch(msg.what)
				{
					case 1:
						SharedPreferences pref = getSharedPreferences("cdp", 0);
						SharedPreferences.Editor edit = pref.edit();
						edit.putString("number", (String) msg.obj);
					    edit.commit();//저장 시작
						break;
				}
			}
		};
		 private static int getAppVersion(Context context) {
		      try {
		         PackageInfo packageInfo = context.getPackageManager()
		               .getPackageInfo(context.getPackageName(), 0);
		         return packageInfo.versionCode;
		      } catch (NameNotFoundException e) {
		         // should never happen
		         throw new RuntimeException("Could not get package name: " + e);
		      }
		   }
		 
		 private SharedPreferences getGCMPreferences(Context context) {
		      // This sample app persists the registration ID in shared preferences,
		      // but
		      // how you store the regID in your app is up to you.
		      return getSharedPreferences(MainActivity.class.getSimpleName(),
		            Context.MODE_PRIVATE);
		   }
		
		 private String getRegistrationId(Context context) {
		      final SharedPreferences prefs = getGCMPreferences(context);
		      String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		      if (registrationId.equals("")) {
		         //Log.i(LOG_TAG, "Registration not found.");
		         return "";
		      }
		      // Check if app was updated; if so, it must clear the registration ID
		      // since the existing regID is not guaranteed to work with the new
		      // app version.
		      int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
		            Integer.MIN_VALUE);
		      int currentVersion = getAppVersion(context);
		      if (registeredVersion != currentVersion) {
		        // Log.i(LOG_TAG, "App version changed.");
		         return "";
		      }
		      return registrationId;
		   }
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Java Code로 옵션메뉴 추가 하기
        menu.add(0, ONE, Menu.NONE, "음성검색").setIcon(android.R.drawable.ic_menu_rotate);
        menu.add(0, TWO, Menu.NONE, "서비스시작").setIcon(android.R.drawable.ic_menu_add);
        menu.add(0, THREE, Menu.NONE, "서비스종료").setIcon(android.R.drawable.ic_menu_agenda);
        return true;
    }
	
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }
 
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
 
        switch (item.getItemId()) {
        case ONE:
        	Intent intent = null;
    		intent = new Intent(this, PocketSphinxActivity.class);
    		startActivity(intent);
            break;
 
        case TWO:
        	startService(new Intent(this, CdpLocationService.class));
        	break;
 
        case THREE:
        	stopService(new Intent(this, CdpLocationService.class));
            break;
 
        default:
            break;
        }
 
        return super.onOptionsItemSelected(item);
    }
 
}