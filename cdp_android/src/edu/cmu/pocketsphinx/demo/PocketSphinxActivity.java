/* ====================================================================
 * Copyright (c) 2014 Alpha Cephei Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY ALPHA CEPHEI INC. ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL CARNEGIE MELLON UNIVERSITY
 * NOR ITS EMPLOYEES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ====================================================================
 */

package edu.cmu.pocketsphinx.demo;

import static android.widget.Toast.makeText;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.R;
import com.example.activity.SAPServiceProvider;
import com.example.activity.SAPServiceProvider.FileAction;
import com.example.activity.SAPServiceProvider.LocalBinder;
import com.example.testffmpeg.NativeCall;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Config;
import edu.cmu.pocketsphinx.Decoder;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;


public class PocketSphinxActivity extends Activity implements
        RecognitionListener {
	
	static {
        System.loadLibrary("pocketsphinx_jni");
    }
    private static final String DEST_PATH  = "/storage/emulated/legacy/temp.aaa";
    private static final String DEST_DIRECTORY = "/storage/sdcard0/";
    
    public static boolean isUp = false;
    boolean check = false;
    private Context mCtxt;
    private ProgressBar mRecvProgressBar;

    private String mDirPath;
    private AlertDialog mAlert;
    private static final String TAG = "cdp";
    private String mFilePath;
    public int mTransId;
    private SAPServiceProvider mFTService;
    private static final String KWS_SEARCH = "wakeup";
    private static final String KEYPHRASE = "call";

    ContentResolver cr;
    private CustomSpeechRecognizer customRecognizer;
    private HashMap<String, Integer> captions;

    private ServiceConnection mFTConnection = new ServiceConnection()
    {
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(TAG, "FT service connection lost");
            mFTService = null;
        }

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder service) {
            Log.d(TAG, "FT service connected");
            mFTService = ((LocalBinder) service).getService();
            mFTService.registerFileAction(getFileAction());
        }
    };
    private FileAction getFileAction() {
        return new FileAction() {
            @Override
            public void onError(final String errorMsg,final int errorCode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mAlert != null && mAlert.isShowing()) {
                            mAlert.dismiss();
                        }
                        Toast.makeText(mCtxt, "Transfer cancelled "+errorMsg, Toast.LENGTH_SHORT).show();
                        mRecvProgressBar.setProgress(0);
                    }
                });
            }

            @Override
            public void onProgress(final long progress) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRecvProgressBar.setProgress((int) progress);
                    }
                });
            }

            @Override
            public void onTransferComplete(String path) {
            	NativeCall.audio_decode();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mRecvProgressBar.setProgress(0);
                        new AsyncTask<Void, Void, Exception>() {
                            @Override
                            protected Exception doInBackground(Void... params) {
                                try {
                                    Assets assets = new Assets(PocketSphinxActivity.this);
                                    File assetDir = assets.syncAssets();
                                    setupRecognizer(assetDir);
                                } catch (IOException e) {
                                    return e;
                                }
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Exception result) {
                                if (result != null) {
                                    ((TextView) findViewById(R.id.caption_text))
                                            .setText("Failed to init recognizer " + result);
                                } else {
                                	//String caption = getResources().getString(captions.get(KWS_SEARCH));
                                    TextView tv = ((TextView) findViewById(R.id.caption_text));
                                    tv.setText("waiting..");
                                    
                                    customRecognizer.startListening(KWS_SEARCH);
                                }
                            }
                        }.execute();
                        Toast.makeText(getBaseContext(), "receive Completed!", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onTransferRequested(int id, String path) {
            	Log.i("패스가뭐냐",path);
                mFilePath = path;
                mTransId = id;
                mFTService.receiveFile(mTransId, DEST_DIRECTORY + "test01.amr" , true);
            }
        };
    }

    private void showQuitDialog() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder alertbox = new AlertDialog.Builder(PocketSphinxActivity.this);
                alertbox = new AlertDialog.Builder(PocketSphinxActivity.this);
                alertbox.setMessage("Receiving file : [" + mFilePath + "] QUIT?");
                alertbox.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        try {
                            mFTService.cancelFileTransfer(mTransId);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(mCtxt, "IllegalArgumentException", Toast.LENGTH_SHORT).show();
                        }
                        mAlert.dismiss();
                        mRecvProgressBar.setProgress(0);
                    }
                });

                mAlert = alertbox.create();
                mAlert.show();
            }
        });
    }
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        // Prepare the data for UI
        
        captions = new HashMap<String, Integer>();
        captions.put(KWS_SEARCH, R.string.kws_caption);
        setContentView(R.layout.main);
        ((TextView) findViewById(R.id.caption_text))
                .setText("Preparing the recognizer");
        cr = getContentResolver();
        mCtxt = getApplicationContext();
        mRecvProgressBar = (ProgressBar) findViewById(R.id.RecvProgress);
        mRecvProgressBar.setMax(100);
        // Recognizer initialization is a time-consuming and it involves IO,
        // so we execute it in async task
        mCtxt.bindService(new Intent(getApplicationContext(), SAPServiceProvider.class), 
                this.mFTConnection, Context.BIND_AUTO_CREATE);
        isUp = true;
      
    }

    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        /*
    	String text = hypothesis.getHypstr();
        if (text.equals(KEYPHRASE))
            switchSearch(MENU_SEARCH);
        else if (text.equals(DIGITS_SEARCH))
            switchSearch(DIGITS_SEARCH);
        else if (text.equals(FORECAST_SEARCH))
            switchSearch(FORECAST_SEARCH);
        else
            ((TextView) findViewById(R.id.result_text)).setText(text);
            */
    }

    public void onDestroy() {
        super.onDestroy();
        isUp = false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        isUp = true;
    }
    
    @Override
    protected void onStop() {
        super.onStop();
        isUp = false;
    }
    
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isUp = false;
    }
    
	@Override
    protected void onPause() {
        super.onPause();
        isUp = false;
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        isUp = true;
    }
    
    @Override
    public void onResult(Hypothesis hypothesis) {
    	if(check)
    	{
    		check=false;
    		return;
    	}
    	customRecognizer.stop();
    	((TextView) findViewById(R.id.result_text)).setText("");
        
        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
            Log.i("뭐라고 인식되냐",text);
            if(text.equals("both") || text.equals("the"))
            	startActivity(new Intent("android.intent.action.DIAL", null));
            else // �ν��� ����� �� �Ǿ��� ��� �ٽ� �ϴ� �κ�
            	makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
        }
        check = true;
        MediaFile mf = new MediaFile(cr,new File(DEST_DIRECTORY+"test01.amr"));
        MediaFile mf2 = new MediaFile(cr,new File(DEST_DIRECTORY+"test01.raw"));
        try {
			mf.delete();
			mf2.delete();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @Override
    public void onBeginningOfSpeech() {
    }

    @Override
    public void onEndOfSpeech() {
        /*
    	if (DIGITS_SEARCH.equals(customRecognizer.getSearchName())
                || FORECAST_SEARCH.equals(customRecognizer.getSearchName()))
            switchSearch(KWS_SEARCH);
        */
    }
/*
    private void switchSearch(String searchName) {        
    	customRecognizer.stop();
        customRecognizer.startListening(searchName);
        
        String caption = getResources().getString(captions.get(searchName));
        TextView tv = ((TextView) findViewById(R.id.caption_text));
        tv.setText(caption);
    }
*/
    private void setupRecognizer(File assetsDir) {
    	
    	File modelsDir = new File(assetsDir, "models");
         
    	Config config = Decoder.defaultConfig();
    	config.setString("-hmm", new File(modelsDir, "hmm/hub4wsj_sc_8k").getPath());
    	config.setString("-dict", new File(modelsDir, "dict/cmu07a.dic").getPath());
    	config.setString("-rawlogdir", assetsDir.getPath());
    	config.setFloat("-kws_threshold", 1e-20f);
    	
    	customRecognizer = new CustomSpeechRecognizer(config);
    	customRecognizer.addListener(this);

        // Create keyword-activation search.
        customRecognizer.addKeyphraseSearch(KWS_SEARCH, KEYPHRASE);
        
        // Create language model search.
        File languageModel = new File(modelsDir, "lm/wsj0vp.5000.dmp");
        customRecognizer.addNgramSearch(KWS_SEARCH, languageModel);
       
/*        
        // Create grammar-based searches.
        File menuGrammar = new File(modelsDir, "grammar/menu.gram");
        customRecognizer.addGrammarSearch(MENU_SEARCH, menuGrammar);
        File digitsGrammar = new File(modelsDir, "grammar/digits.gram");
        customRecognizer.addGrammarSearch(DIGITS_SEARCH, digitsGrammar);
*/        
        
    }
}
