package com.example.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.example.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class FirstActivity extends Activity implements OnClickListener {
	ProgressBarThread progressBar = new ProgressBarThread(FirstActivity.this);
	Handler handler = new Handler() 
	{
		public void handleMessage(android.os.Message msg)
		{
			switch(msg.what)
			{
//				case FollovCode.HTTP_ERROR:
//					FollovDialog.showDialog("접속 에러 입니다.", JoinActivity.this);
//					break;
//				case FollovCode.JOIN_ERROR:
//					FollovDialog.showDialog("회원가입 에러입니다.", JoinActivity.this);
//					break;
//				case FollovCode.EMAIl_CHECK_COMPLETION:
//					gcmInit();
//					break;
//				case FollovCode.GCM_NETWORK_ERROR:
//					FollovDialog.showDialog((String)msg.obj, JoinActivity.this);
//					break;
				case 1:
					progressBar.stopProgressBar();
					Intent i = new Intent(FirstActivity.this, MainActivity.class);//메인 액티비티
					startActivity(i);
					finish();
				case 2: //방 액티비티로 넘겨야한다.
					progressBar.stopProgressBar();
					Intent i2 = new Intent(FirstActivity.this, RoomActivity.class);//메인 액티비티
					startActivity(i2);
					finish();
//				case FollovCode.EMAIl_CHECK_DUPLCATED:
//					FollovDialog.showDialog("이메일 중복입니다.", JoinActivity.this);
//					break;
			}
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		progressBar.start();				
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		
		Log.e("network","network init : "+cm.getNetworkPreference());
		
		
		setContentView(R.layout.first);
		ImageLoaderConfiguration config = AppManager.getInstance().getConfig();
		config = new ImageLoaderConfiguration.Builder(getApplicationContext())
        .threadPoolSize(3) // default
        .threadPriority(Thread.NORM_PRIORITY - 1) // default
        .tasksProcessingOrder(QueueProcessingType.FIFO) // default
        .denyCacheImageMultipleSizesInMemory()
        .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
        .writeDebugLogs()
        .build();
		ImageLoader.getInstance().init(config);
		
		DisplayImageOptions options = AppManager.getInstance().getOptions();
		options = new DisplayImageOptions.Builder()
        .showImageOnLoading(R.drawable.loading) // resource or drawable
        .showImageForEmptyUri(R.drawable.empty) // resource or drawable
        .showImageOnFail(R.drawable.fail) // resource or drawable
        .cacheInMemory(true)
		.cacheOnDisk(true)
		.considerExifParams(true)
		.bitmapConfig(Bitmap.Config.RGB_565)
		.build();
		AppManager.getInstance().setActivity(this);
		if(!AppManager.getInstance().isPossibleInternet())
		{
			FollovDialog.showDialog("인터넷 접속 상태를 확인해 주세요.", FirstActivity.this);
			System.exit(0);
		}
		else
		{
			if(FollovPref.getBoolean("login",FirstActivity.this))
			{
				if(FollovPref.getBoolean("party",FirstActivity.this))
				{
					//이름,이메일
					//방 액티비티로(방에서 방번호와 그사람들 가져오기.)
				}
				else
				{
					//이름,이메일만 받아오기
					//기어한테 세팅.
				}
			}
			else
			{
				Handler handler = new Handler() {
					public void handleMessage(android.os.Message msg) {
							progressBar.stopProgressBar();
							Intent i = new Intent(FirstActivity.this, JoinActivity.class);//메인 액티비티
							startActivity(i);
							finish();
					};
				};
				handler.sendEmptyMessageDelayed(0, 1000);
			}
		}
	}
	@Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		System.exit(0);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}
