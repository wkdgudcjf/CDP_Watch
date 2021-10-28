package com.example.activity;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.accessory.SA;
import com.samsung.android.sdk.accessory.SAAgent;
import com.samsung.android.sdk.accessory.SAPeerAgent;
import com.samsung.android.sdk.accessory.SASocket;
import com.samsung.android.sdk.accessoryfiletransfer.SAFileTransfer;
import com.samsung.android.sdk.accessoryfiletransfer.SAFileTransfer.EventListener;
import com.samsung.android.sdk.accessoryfiletransfer.SAft;

import edu.cmu.pocketsphinx.demo.PocketSphinxActivity;


public class SAPServiceProvider extends SAAgent {
   public static final String TAG = "cdp";
   
   public Boolean isAuthentication = false;
   public Context mContext = null;
   public static final int MSG_PUSHFILE_ACCEPTED = 1;
   public static final int MSG_PUSHFILE_NOT_ACCEPTED = 2;
   public static final int SERVICE_CONNECTION_RESULT_OK = 0;

   public static final int WALK_CHANNEL_ID = 227;

   HashMap<Integer, WalkProviderConnection> mConnectionsMap = null;
   private FileAction mFileAction = null;
   private SAFileTransfer mSAFileTransfer = null;
   private WalkProviderConnection mConnection = null;
   private EventListener mCallback;
   private final IBinder mBinder = new LocalBinder();
   private int id;
   public class LocalBinder extends Binder {
      public SAPServiceProvider getService() {
         return SAPServiceProvider.this;
      }
   }

   public SAPServiceProvider() {
      super(TAG, WalkProviderConnection.class);
   }

   public class WalkProviderConnection extends SASocket 
   {
      private int mConnectionId;

      public WalkProviderConnection() {
         super(WalkProviderConnection.class.getName());
      }

      @Override
      public void onError(int channelId, String errorString, int error) {
         Log.e(TAG, "Connection is not alive ERROR: " + errorString + "  "
               + error);
      }
      @Override
      public void onReceive(int channelId, byte[] data)
      {
         final WalkProviderConnection uHandler = mConnectionsMap.get(Integer
                  .parseInt(String.valueOf(mConnectionId)));
         id = mConnectionId;
            if(uHandler == null){
               Log.e(TAG,"Error, can not get HelloAccessoryProviderConnection handler");
               return;
            }
         Log.d(TAG, "onReceive");
         String strToUpdateUI = new String(data);
         if(strToUpdateUI.equals("call"))
         {
             Log.i("일단왔어", "처음에왓음");
         }
      }
      @Override
      protected void onServiceConnectionLost(int errorCode) {
         Log.e(TAG, "onServiceConectionLost  for peer = " + mConnectionId
               + "error code =" + errorCode);

         if (mConnectionsMap != null) {
            mConnectionsMap.remove(mConnectionId);
         }
      }
   }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate of smart view Provider Service");
        mContext = getApplicationContext();
        SA mAccessory = new SA();
        mCallback = new EventListener() {                
            @Override
            public void onProgressChanged(int transId, int progress) {
                Log.d(TAG, "onTransferProgress : " + progress + " transId : " + transId);

                if (mFileAction != null) {
                    mFileAction.onProgress(progress);
                }
            }
                
            @Override
            public void onTransferCompleted(int transId, String fileName, int errorCode) {
                Log.d(TAG, "onTransferComplete,  tr id : " + transId +  " file name : " + fileName + " error code : " + errorCode);
                if (errorCode == 0) {
                    mFileAction.onTransferComplete(fileName);
                } else {
                    mFileAction.onError("Error", errorCode);
                }
            }                

            @Override
            public void onTransferRequested(int id, String fileName) {
                Log.d(TAG, "onTransferRequested,  tr id : " + id +  " file name : " + fileName);
                if (PocketSphinxActivity.isUp)
                    mFileAction.onTransferRequested(id, fileName);
                else
                    mContext.startActivity(new Intent().setClass(mContext, PocketSphinxActivity.class)
                                                       .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                                       .setAction("incomingFT")
                                                       .putExtra("tx", id)
                                                       .putExtra("fileName", fileName));
            }

        };
        SAft SAftPkg = new SAft();
        try {
           mAccessory.initialize(this);
           SAftPkg.initialize(this);
        } catch (SsdkUnsupportedException e) {
           // Error Handling
        } catch (Exception e1) {
            Log.e(TAG, "Cannot initialize Accessory package.");
            e1.printStackTrace();
         /*
          * Your application can not use Accessory package of Samsung
          * Mobile SDK. You application should work smoothly without using
          * this SDK, or you may want to notify user and close your app
          * gracefully (release resources, stop Service threads, close UI
          * thread, etc.)
          */
            stopSelf();
        }
        mSAFileTransfer = new SAFileTransfer(SAPServiceProvider.this, mCallback);
    }       

   @Override
   protected void onFindPeerAgentResponse(SAPeerAgent arg0, int arg1) {
      // TODO Auto-generated method stub
      Log.d(TAG, "onFindPeerAgentResponse  arg1 =" + arg1);
   }
   @Override
   public void onDestroy() {
       super.onDestroy();
       Log.i(TAG, "SAP Service Stopped.");
   }
   @Override
   protected void onServiceConnectionResponse(SASocket uSocket,
         int error) {
      if (error == CONNECTION_SUCCESS) {
    	  WalkProviderConnection myConnection = (WalkProviderConnection) uSocket;
         if (uSocket != null)
         {
        	 mConnection = myConnection;
            if (mConnectionsMap == null) {
               mConnectionsMap = new HashMap<Integer, WalkProviderConnection>();
            }

            mConnection.mConnectionId = (int) (System.currentTimeMillis() & 255);

            Log.d(TAG, "onServiceConnection connectionID = "
                  + mConnection.mConnectionId);

            mConnectionsMap.put(mConnection.mConnectionId, mConnection);
            AppManager.getInstance().setService(this);
            Log.e(TAG, "Connection Success");
         } else {
            Log.e(TAG, "SASocket object is null");
         }
      } else if (error == CONNECTION_ALREADY_EXIST) 
      {
         Log.e(TAG, "onServiceConnectionResponse, CONNECTION_ALREADY_EXIST");
      } else {
         Log.e(TAG, "onServiceConnectionResponse result error =" + error);
      }
   }
   public void registerFileAction(FileAction action)
   {
       this.mFileAction = action;
   }
   public void cancelFileTransfer(int transId) {
       if (mSAFileTransfer != null) { 
           mSAFileTransfer.cancel(transId);
       }
   }
   public void receiveFile(int transId, String path, boolean bAccept) {
       Log.d(TAG, "receiving file : transId: " + transId + "bAccept : " + bAccept +"path : "+ path);
       if (mSAFileTransfer != null) {
           if (bAccept)
           {
               mSAFileTransfer.receive(transId, path);
           } 
           else
           {
               mSAFileTransfer.reject(transId);
           }
       }
   }
   public void sendFile(int transId, String path, boolean bAccept) {
       Log.d(TAG, "receiving file : transId: " + transId + "bAccept : " + bAccept);
       if (mSAFileTransfer != null) {
           if (bAccept)
           {
              // mSAFileTransfer.send(transId, path);
           } 
           else
           {
               mSAFileTransfer.reject(transId);
           }
       }
   }
   @Override
   public IBinder onBind(Intent arg0) {
      return mBinder;
   }

   public void sendString(String string,int data) {
      if(mConnectionsMap==null)
      {
         return;
      }
      final WalkProviderConnection uHandler = mConnectionsMap.get(Integer
               .parseInt(String.valueOf(id)));
      if(uHandler == null){
         Log.e(TAG,"Error, can not get HelloAccessoryProviderConnection handler");
         return;
      }
      try {
         JSONObject message = new JSONObject();
         message.put("code", string);
         message.put("data", data);
            uHandler.send(WALK_CHANNEL_ID, message.toString().getBytes());
      } catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      catch (JSONException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   public interface FileAction {
       void onError(String errorMsg, int errorCode);
       void onProgress(long progress);
       void onTransferComplete(String path);
       void onTransferRequested(int id, String path);
   }
}

