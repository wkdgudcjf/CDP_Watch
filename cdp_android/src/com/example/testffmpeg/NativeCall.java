package com.example.testffmpeg;


public class NativeCall {  
	  
	static {
	    System.loadLibrary("basicplayer");
	}

	public static native void audio_decode(); 
	//public static native void audio_encode();
}  