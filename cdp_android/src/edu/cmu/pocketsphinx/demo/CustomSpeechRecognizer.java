package edu.cmu.pocketsphinx.demo;

import static java.lang.String.format;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Collection;
import java.util.HashSet;

import edu.cmu.pocketsphinx.*;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder.AudioEncoder;
import android.media.MediaRecorder.AudioSource;
import android.net.rtp.AudioStream;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Files;
import android.util.Log;

/**
 * Main class to access recognizer functions. After configuration this class
 * starts a listener thread which records the data and recognizes it using
 * Pocketsphinx engine. Recognition events are passed to a client using
 * {@link RecognitionListener}
 * 
 */
public class CustomSpeechRecognizer {

    protected static final String TAG = CustomSpeechRecognizer.class.getSimpleName();

    private static final int BUFFER_SIZE = 1024;

    private final Decoder decoder;

    private Thread recognizerThread;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Collection<RecognitionListener> listeners = new HashSet<RecognitionListener>();

    private final int sampleRate;

    public CustomSpeechRecognizer(Config config) {
        sampleRate = (int) config.getFloat("-samprate");
        if (config.getFloat("-samprate") != sampleRate)
            throw new IllegalArgumentException("sampling rate must be integer");
        decoder = new Decoder(config);
    }

    /**
     * Adds listener.
     */
    public void addListener(RecognitionListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    /**
     * Removes listener.
     */
    public void removeListener(RecognitionListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Starts recognition. Does nothing if recognition is active.
     * 
     * @return true if recognition was actually started
     */
    public boolean startListening(String searchName) {
        if (null != recognizerThread)
            return false;

        Log.i(TAG, format("Start recognition \"%s\"", searchName));
        decoder.setSearch(searchName);
        recognizerThread = new RecognizerThread();
        recognizerThread.start();
        return true;
    }

    private boolean stopRecognizerThread() {
        if (null == recognizerThread)
            return false;

        try {
            recognizerThread.interrupt();
            recognizerThread.join();
        } catch (InterruptedException e) {
            // Restore the interrupted status.
            Thread.currentThread().interrupt();
        }

        recognizerThread = null;
        return true;
    }

    /**
     * Stops recognition. All listeners should receive final result if there is
     * any. Does nothing if recognition is not active.
     * 
     * @return true if recognition was actually stopped
     */
    public boolean stop() {
        boolean result = stopRecognizerThread();
        if (result) {
            Log.i(TAG, "Stop recognition");
            final Hypothesis hypothesis = decoder.hyp();
            mainHandler.post(new ResultEvent(hypothesis));
        }
        return result;
    }

    /**
     * Cancels recognition. Listeners do not receive final result. Does nothing
     * if recognition is not active.
     * 
     * @return true if recognition was actually canceled
     */
    public boolean cancel() {
        boolean result = stopRecognizerThread();
        if (result) {
            Log.i(TAG, "Cancel recognition");
        }

        return result;
    }

    /**
     * Gets name of the currently active search.
     * 
     * @return active search name or null if no search was started
     */
    public String getSearchName() {
        return decoder.getSearch();
    }

    public void addFsgSearch(String searchName, FsgModel fsgModel) {
        decoder.setFsg(searchName, fsgModel);
    }

    /**
     * Adds searches based on JSpeech grammar.
     * 
     * @param name
     *            search name
     * @param file
     *            JSGF file
     */
    public void addGrammarSearch(String name, File file) {
        Log.i(TAG, format("Load JSGF %s", file));
        decoder.setJsgfFile(name, file.getPath());
    }

    /**
     * Adds search based on N-gram language model.
     * 
     * @param name
     *            search name
     * @param file
     *            N-gram model file
     */
    public void addNgramSearch(String name, File file) {
        Log.i(TAG, format("Load N-gram model %s", file));
        decoder.setLmFile(name, file.getPath());
    }

    /**
     * Adds search based on a single phrase.
     * 
     * @param name
     *            search name
     * @param phrase
     *            search phrase
     */
    public void addKeyphraseSearch(String name, String phrase) {
        decoder.setKeyphrase(name, phrase);
    }

    /**
     * Adds search based on a keyphrase file.
     * 
     * @param name
     *            search name
     * @param phrase
     *            search phrase
     */
    public void addKeywordSearch(String name, File file) {
        decoder.setKws(name, file.getPath());
    }

    private final class RecognizerThread extends Thread {
        @Override
        public void run() {
        
            decoder.startUtt(null);
            short[] buffer = new short[BUFFER_SIZE];
            byte[] buf = new byte[BUFFER_SIZE*2];
            float[] buff = new float[BUFFER_SIZE/2];
            
            FileInputStream in=null;
           
        	try {
				in = new FileInputStream("/storage/sdcard0/test01.raw");
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			}
        	
        	int nread = 0;
        	// remove dump data
        	try {
        		in.read(buf, 0, 3);
				//System.out.println();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	while(nread != -1) {
            	try {
					nread = in.read(buf, 0, buf.length);
					System.out.println(nread);
				} catch (IOException e) {
					e.printStackTrace();
				}
            	
                if (nread > 0) {
                	ByteBuffer.wrap(buf).order(ByteOrder.BIG_ENDIAN).asFloatBuffer().get(buff);
                	for(int i=0;i<nread/2;i++) {
     
                		//System.out.println(buff[i]);
                		float f = buff[i/2]*32768; //32768
                		if( f > 32767 ) f = 32767;
                		if( f < -32768 ) f = -32768;
                		/*
                		buffer[i*2] = (short)f;
                		buffer[i*2+1] = 0;
                		*/
                		buffer[i] = (short)f;
                	}
                	//ByteBuffer.wrap(buf).order(ByteOrder.BIG_ENDIAN).asShortBuffer().get(buffer);
                	decoder.processRaw(buffer, nread/2, false, false);
                	
                }
                
        	}  
            final Hypothesis hypothesis = decoder.hyp();
            mainHandler.post(new ResultEvent(hypothesis));
            
            decoder.endUtt();

            // Remove all pending notifications.
            mainHandler.removeCallbacksAndMessages(null);
        }
    }

    private abstract class RecognitionEvent implements Runnable {
        public void run() {
            RecognitionListener[] emptyArray = new RecognitionListener[0];
            for (RecognitionListener listener : listeners.toArray(emptyArray))
                execute(listener);
        }

        protected abstract void execute(RecognitionListener listener);
    }

    
    private class ResultEvent extends RecognitionEvent {
        protected final Hypothesis hypothesis;

        ResultEvent(Hypothesis hypothesis) {
            this.hypothesis = hypothesis;
        }

        @Override
        protected void execute(RecognitionListener listener) {
               listener.onResult(hypothesis);
        }
    }
}