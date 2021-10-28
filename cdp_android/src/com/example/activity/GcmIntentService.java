package com.example.activity;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.example.activity.SAPServiceProvider;
import com.example.activity.SAPServiceProvider.LocalBinder;

public class GcmIntentService extends IntentService
{
	private static final String TAG = GcmIntentService.class.getSimpleName();
    public static final int NOTIFICATION_ID = 1;
  //  private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
  //  private SAPServiceProvider myService;
    public GcmIntentService() 
    {
        super("GcmIntentService");
//        bindService(new Intent(getApplicationContext(), SAPServiceProvider.class),
//                this.myConnection, Context.BIND_AUTO_CREATE);
    }
//    private ServiceConnection myConnection = new ServiceConnection()
//    {
//        @Override
//        public void onServiceDisconnected(ComponentName arg0) {
//            Log.d("���� �ٿ�� ���", "FT service connection lost");
//            myService = null;
//        }
//
//        @Override
//        public void onServiceConnected(ComponentName arg0, IBinder service)
//        {
//            Log.d("���� �ٿ��", "FT service connected");
//            myService = ((LocalBinder) service).getService();  
//        }
//    };

    @Override
    protected void onHandleIntent(Intent intent)
    {
    	Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        String messageType = gcm.getMessageType(intent);
        if (!extras.isEmpty())
        {
            if (GoogleCloudMessaging.
                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) 
            {

            }
            else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_DELETED.equals(messageType))
            {

            }
            else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType))
            {
              	String type = extras.getString("type");
            	if(type.equals("location")){
      		
            		if(AppManager.getInstance().getMyLocation()!=null)
            		{
            			String lat = extras.getString("lat");
                    	String lng = extras.getString("long");
//                    	location.setLatitude(Double.parseDouble(lat));
//                    	location.setLongitude(Double.parseDouble(lng));
//                		int k = (int) location.bearingTo(AppManager.getInstance().getMyLocation());
                		int k2 = (int) angleFromCoordinate(Double.parseDouble(lat),Double.parseDouble(lng),AppManager.getInstance().getMyLocation().getLatitude(),AppManager.getInstance().getMyLocation().getLongitude());
                		int k3 = (int) angleFromCoordinate2(Double.parseDouble(lat),Double.parseDouble(lng),AppManager.getInstance().getMyLocation().getLatitude(),AppManager.getInstance().getMyLocation().getLongitude());
                		if(k2<0)
                		{
                			k2+=360;
                		}
                		if(k3<0)
                		{
                			k3+=360;
                		}
                		if(AppManager.getInstance().getService() != null)
                			AppManager.getInstance().getService().sendString("location",k2);
                		Log.i("gcm왓다", AppManager.getInstance().getMyLocation().getLatitude()+"/내/꺼/"+AppManager.getInstance().getMyLocation().getLongitude());
                    	Log.i("gcm왓다", ""+lat+"/원조상대방/"+lng);
                		Log.i("gcm와서 각도쟀다.","각도2: "+k2 +",각도3 : "+k3);
                    	//                	
//                    	Message msg = Message.obtain();
//                    	
//                    	//받으면 내꺼랑 계산해서 기어로 보낸다.
//                		
//                		follovService.locationReceived(lat, lng);
                    	
            		}
            	} 
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
    private double angleFromCoordinate(double lat1, double long1, double lat2,
	        double long2) {

	    double dLon = (long2 - long1);

	    double y = Math.sin(dLon) * Math.cos(lat2);
	    double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1)
	            * Math.cos(lat2) * Math.cos(dLon);

	    double brng = Math.atan2(y, x);
	    brng = Math.toDegrees(brng);
	    return brng;
	}
	private double angleFromCoordinate2(double lat1, double long1, double lat2,
	        double long2) {
		 // 현재 위치 : 위도나 경도는 지구 중심을 기반으로 하는 각도이기 때문에 라디안 각도로 변환한다.
	    double Cur_Lat_radian = lat1 * (3.141592 / 180);
	    double Cur_Lon_radian = long1 * (3.141592 / 180);
	
	    // 목표 위치 : 위도나 경도는 지구 중심을 기반으로 하는 각도이기 때문에 라디안 각도로 변환한다.
	    double Dest_Lat_radian = lat2 * (3.141592 / 180);
	    double Dest_Lon_radian = long2 * (3.141592 / 180);
	    // radian distance
	    double radian_distance = 0;
	    radian_distance = Math.acos(Math.sin(Cur_Lat_radian) * Math.sin(Dest_Lat_radian) + Math.cos(Cur_Lat_radian) * Math.cos(Dest_Lat_radian) * Math.cos(Cur_Lon_radian - Dest_Lon_radian));
	
	    // 목적지 이동 방향을 구한다.(현재 좌표에서 다음 좌표로 이동하기 위해서는 방향을 설정해야 한다. 라디안값이다.
	    double radian_bearing = Math.acos((Math.sin(Dest_Lat_radian) - Math.sin(Cur_Lat_radian) * Math.cos(radian_distance)) / (Math.cos(Cur_Lat_radian) * Math.sin(radian_distance)));        // acos의 인수로 주어지는 x는 360분법의 각도가 아닌 radian(호도)값이다.       
	    double true_bearing = 0;
	    if (Math.sin(Dest_Lon_radian - Cur_Lon_radian) < 0)
	    {
	    	true_bearing = radian_bearing * (180 / 3.141592);
	    	true_bearing = 360 - true_bearing;
	    }
	    else
	    {
	    	true_bearing = radian_bearing * (180 / 3.141592);
	    }
	    return true_bearing;
	}
    private void setNotification(Context context, String title, String message) {
		NotificationManager notificationManager = null;
		Notification notification = null;
		try {
			notificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);
			notification = new Notification(R.drawable.ic_launcher,
					title, System.currentTimeMillis());
			notification.flags |= Notification.FLAG_AUTO_CANCEL;
			notification.vibrate = new long[] {400,1500};
			notification.number++;
		
			Intent intent = new Intent(context, FirstActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent pi = PendingIntent.getActivity(context, 0, intent, 0);
			notification.setLatestEventInfo(context, message, title, pi);
			notificationManager.notify(0, notification);
		} catch (Exception e) {
			Log.d("kk", "[setNotification] Exception : " + e.getMessage());
		}
	}
}
