package com.example.network;

import java.io.*;
import java.net.*;

import org.apache.http.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.entity.*;
import org.apache.http.impl.client.*;
import org.apache.http.params.*;
import org.apache.http.util.*;
import org.json.*;

import android.os.*;
import android.util.Log;


public class HttpPostMethod extends Thread
{
	Handler mHandler;
	String url;
	String param;
	public HttpPostMethod(Handler mHandler, String url, String param)
	{
		this.mHandler=mHandler;
		this.url=url;
		this.param=param;
	}
	public void run()
	{
		Message msg = Message.obtain();
		HttpParams httpParams = new BasicHttpParams();
	    HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
	    HttpConnectionParams.setSoTimeout(httpParams, 5000);
		DefaultHttpClient httpclient = new DefaultHttpClient(httpParams);
		HttpPost httpost = new HttpPost("http://211.189.19.175:8080/cdp/"+url);
		
		try {
			StringEntity se  =new StringEntity( param , "UTF-8");
            se.setContentType("application/json");
            se.setContentEncoding( "UTF-8" );
			httpost.setEntity(se);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    httpost.setHeader("Accept", "application/json");
	    httpost.setHeader("Content-type", "application/json;charset=UTF-8");
	    
	    String result = null;
	    try {
	    	HttpResponse response = httpclient.execute(httpost);
			if(response.getStatusLine().getStatusCode()==HttpURLConnection.HTTP_OK){
				result = EntityUtils.toString(response.getEntity());
				JSONTokener jtk = new JSONTokener(result);
				JSONObject jo;
				try
				{
					jo = (JSONObject)jtk.nextValue();
					msg.what = jo.getInt("code");
					msg.obj = jo.getString("result");
					if(((String)msg.obj).equals("")){
						msg.obj = param;
					}
					
				} catch (JSONException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			else if(response.getStatusLine().getStatusCode()==HttpURLConnection.HTTP_NOT_FOUND){
						
			}
			else if(response.getStatusLine().getStatusCode()==HttpURLConnection.HTTP_UNAUTHORIZED){
					
			}
			else{
					
			}			
		}  catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    finally
	    {
	        mHandler.sendMessage(msg);
	    }
	}
}
