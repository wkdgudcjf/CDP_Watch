package com.example.service;

import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.example.activity.AppManager;
import com.example.network.HttpPostMethod;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class CdpLocationService extends Service implements LocationListener,SensorEventListener,
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener
{
	private LocationClient mLocationClient;
	private LocationRequest mLocationRequest;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer, mField;
	private float[] mGravity;
	private float[] mMagnetic;
	private Location myLocation;
	private boolean check=true;
	boolean check2=true; 
	Timer timeTimer;
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		super.onStartCommand(intent, flags, startId);
		Log.i("서비스시작", "시작됨");
		if(startId != 1)
			return START_STICKY;
		initLocationClient();
		mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        mSensorManager.registerListener(this, mAccelerometer, 1000);
        mSensorManager.registerListener(this, mField, 1000);
		mLocationClient.connect();
		timeTimer = new Timer();
	    timeTimer.schedule(timeTimerTask, 50, 2000);
		return START_STICKY;
	}
	 TimerTask timeTimerTask = new TimerTask(){
	        public void run(){
	        	check2=true;
	        } //end run
	    }; //end TimerTask

	public void initLocationClient() {

		mLocationRequest = LocationRequest.create();
		
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);		
		mLocationRequest.setFastestInterval(50);
		mLocationRequest.setInterval(30);
		/*
		 * Create a new location client, using the enclosing class to handle
		 * callbacks.
		 */
		mLocationClient = new LocationClient(this, this, this);
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onLocationChanged(Location location) 
	{
		// TODO Auto-generated method stub
		showToast("위치 : "+ location.getLatitude() +" / "+location.getLongitude());
//		AppManager.getInstance().setMyLocation(location);
//		SharedPreferences pref = getSharedPreferences("cdp", 0);
//		String value = pref.getString("number", "");
//		JSONObject join = new JSONObject();
//		try
//		{
//			join.put("num", value);
//			join.put("long", location.getLongitude());
//			join.put("lat", location.getLatitude());
//		} 
//		catch (JSONException e1)
//		{
//			e1.printStackTrace();
//		}			
		//HttpPostMethod post = new HttpPostMethod(handler, "location.do", join.toString());
    	// post.start();
	}
	 Handler handler = new Handler() 
		{
			public void handleMessage(android.os.Message msg)
			{
				switch(msg.what)
				{
					case 1:
						Log.i("제대로보냄","제대로보냈다.");
					break;
				}
			}
		};
	
	private void showToast(String msg) {
		Toast.makeText(getApplicationContext(), msg,
				Toast.LENGTH_SHORT).show();
	}
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		locationRequest(false);
	//	startSendLocationToLoverTimer(FollovSetUp.UPDATE_INTERVAL_IN_MILLISECONDS_NOT_DATING);
	}
	private void locationRequest(boolean isFastRequest){
		if(isFastRequest)
			mLocationClient.requestLocationUpdates(mLocationRequest, CdpLocationService.this);
		else
			mLocationClient.requestLocationUpdates(mLocationRequest, CdpLocationService.this);
		
		//mLocationClient.removeLocationUpdates(FollovIntentService.this);
	}
	
	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.i("cdp", "끝");
		mLocationClient.removeLocationUpdates(this);
		mSensorManager.unregisterListener(this);
		timeTimerTask.cancel();  
	    timeTimer.cancel(); 
	}
	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onSensorChanged(SensorEvent event) {
		switch(event.sensor.getType()) {
        case Sensor.TYPE_ACCELEROMETER:
            mGravity = event.values.clone();
            break;
        case Sensor.TYPE_MAGNETIC_FIELD:
            mMagnetic = event.values.clone();
            break;
        default:
            return;
        }
        
        if(mGravity != null && mMagnetic != null) {
            updateDirection();
        }
	}
	 private void updateDirection() {
		 if(check2)
		 {
	        float[] temp = new float[9];
	        float[] R = new float[9];
	        //Load rotation matrix into R
	        SensorManager.getRotationMatrix(temp, null, mGravity, mMagnetic);
	        //Remap to camera's point-of-view
	        SensorManager.remapCoordinateSystem(temp, SensorManager.AXIS_X, SensorManager.AXIS_Z, R);
	        //Return the orientation values
	        float[] values = new float[3];
	        SensorManager.getOrientation(R, values);
	        //Convert to degrees
	        for (int i=0; i < values.length; i++) {
	            Double degrees = (values[i] * 180) / Math.PI;
	            values[i] = degrees.floatValue();
	        }
	       // Log.i("방향", getDirectionFromDegrees(values[0]));
	       showToast("방향 : "+ getDirectionFromDegrees(values[0])+"각도  : "+values[0]);
	        if(values[0]<0)
	        {
	        	values[0]+=360;
	        }
	        if(AppManager.getInstance().getService() != null)
    			AppManager.getInstance().getService().sendString("direction",(int)values[0]);
	        check2=false;
	        //Display the compass direction
	        //Display the raw values
//	        valueView.setText(String.format("Azimuth: %1$1.2f, Pitch: %2$1.2f, Roll: %3$1.2f",
//	                values[0], values[1], values[2]));
	    }
	 }
	 private String getDirectionFromDegrees(float degrees) {
	        if(degrees >= -22.5 && degrees < 22.5) { return "N"; }
	        if(degrees >= 22.5 && degrees < 67.5) { return "NE"; }
	        if(degrees >= 67.5 && degrees < 112.5) { return "E"; }
	        if(degrees >= 112.5 && degrees < 157.5) { return "SE"; }
	        if(degrees >= 157.5 || degrees < -157.5) { return "S"; }
	        if(degrees >= -157.5 && degrees < -112.5) { return "SW"; }
	        if(degrees >= -112.5 && degrees < -67.5) { return "W"; }
	        if(degrees >= -67.5 && degrees < -22.5) { return "NW"; }

	        return null;
	    }
}
