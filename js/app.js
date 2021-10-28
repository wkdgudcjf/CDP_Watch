( function () {
	window.addEventListener( 'tizenhwkey', function( ev ) {
		if( ev.keyName == "back" ) {
			var page = document.getElementsByClassName( 'ui-page-active' )[0],
				pageid = page ? page.id : "";
			if( pageid === "basic" ) {
				tizen.application.getCurrentApplication().exit();
			} else {
				window.history.back();
			}
		}
	});
 window.addEventListener("devicemotion", function(e) 
		   {
		      circleXY(Math.round(e.rotationRate.beta), page,0);
		   }, true);
	// set time on page basic
	getTime();
	setInterval(getTime,10000);	
	
	// set spot on page exercise	
	
	
	circleXY(res, page);
	
	

		
	var basic, exer, map, friend;
	

	basic = document.getElementById("basic");
	exer = document.getElementById("exer");
	map = document.getElementById("map");
	friend = document.getElementById("friend");
	
	var dx;
	document.addEventListener("touchstart", function(e) {
		dx = e.touches.item(0).screenX;
	}, false);
	map.addEventListener("touchstart", function(e) {
		if(dangerCtrl==0){
			var xx = e.touches.item(0).screenX;
			var yy = e.touches.item(0).screenY;
			
			if(xx < 50 && yy > 270) {
				tau.changePage("#voice");
			}
		}
	}, false);
	
	
	basic.addEventListener("touchmove", function(e) {
	      if(dx >= 280 || dx < 40) {
	         var pTarget = e.touches.item(0);
	         if(pTarget.screenX-dx > 100) {
	            tau.changePage("#friend");
		         page = "c5";
		         circleXY(res, page,1);
	         }else if(dx - pTarget.screenX > 100) {
	            tau.changePage("#exer");
		         page = "c2";
		         if(spotFinish==0){
		        	 spot_t = setInterval(getSpot, 1000);
		        	 spotFinish=1;
		         }
		         circleXY(res, page,1);
	         }
	       }
	   }, false);

	   exer.addEventListener("touchmove", function(e) {
	      if(dx >= 280 || dx < 40) {
	         var pTarget = e.touches.item(0);
	         if(pTarget.screenX-dx > 100) {
	            tau.changePage("#basic");
	            page = "c1";
	            circleXY(res, page,1);
	         }else if(dx - pTarget.screenX > 100) {
	            tau.changePage("#map");
	            page = "c3";
	            circleXY(res, page,1);
	         }
	         
	      }
	      
	   }, false);
	   
	   map.addEventListener("touchmove", function(e) {
	      if(dx >= 280 || dx < 40) {
	         var pTarget = e.touches.item(0);
	         if(pTarget.screenX-dx > 100) {
	            tau.changePage("#exer");
		         page = "c2";
		         circleXY(res, page,1);
	         }else if(dx - pTarget.screenX > 100) {
	            tau.changePage("#friend");
		         page = "c5";
		 		circleXY(res, page,1);
	         }
	      }
	   }, false);
	   
	   friend.addEventListener("touchmove", function(e) {
	      if(dx >= 280 || dx < 40) {
	         var pTarget = e.touches.item(0);
	         if(pTarget.screenX-dx > 100) {
	            tau.changePage("#map");
		         page = "c3";
	 			circleXY(res, page,1);
	         }else if(dx - pTarget.screenX > 100) {
	            tau.changePage("#basic");
		         page = "c1";
	 			circleXY(res, page,1);
	         }
	      }
	   }, false);
	   
	   friend.addEventListener("swipelist.right", function() {
			tau.changePage("#friend_message");
			page = "c6";
	 		circleXY(res, page,1);			
		}, false);
	
	//var page = document.getElementById( "friend" ),
	var	listElement = friend.getElementsByClassName( "ui-swipelist-list", "ul" )[0],
		swipeList;
	friend.addEventListener( "pageshow", function() {
	// make SwipeList object
		var options = {
			left: true,
			right: true
		}
		swipeList = new tau.SwipeList( listElement, options );
		
	});
	friend.addEventListener( "pagehide", function() {
	// release object
		swipeList.destroy();
	});
	
	connect();
	var t=setTimeout("fetch('call')",1500);
	
	var ERROR_FILE_WRITE = 'FILE_WRITE_ERR',
    NO_FREE_SPACE_MSG = 'No free space.',
    CANNOT_ACCESS_AUDIO_MSG = 'Cannot access audio stream. ' +
    'Please close all applications that use the audio stream and ' +
    'open the application again.',

    page2 = null,
    recordBtn = null,
    recordBtn2 = null,
    recordBtnIcon = null,
    recordProgress = null,
    recordProgressVal = null,
    exitAlertMessage = null,
    exitAlertOk = null,

    stream = null,

    RECORDING_INTERVAL_STEP = 100,

    recordingInterval = null,
    recording = false,
    recordingTime = 0,
    exitInProgress = false,
    recordingLock = false;

/**
 * Toggles between recording/no recording state.
 * @param {boolean} forceValue
 */
function toggleRecording(forceValue) {
    if (forceValue !== undefined) {
        recording = !!forceValue;
    } else {
        recording = !recording;
    }
}








/**
 * Starts audio recording.
 */
function startRecording() {
    recordingLock = true;
    audio.startRecording();
}

/**
 * Stops audio recording.
 */
function stopRecording() {
    recordingLock = true;
    audio.stopRecording();
}

/**
 * Starts or stops audio recording.
 */
function setRecording() {
    if (recording) {
        startRecording();
    } else {
        stopRecording();
    }
}

/**
 * Handles click event on record button.
 */
function onRecordBtnClick() {
    if (recordingLock) {
        return;
    }
    console.log("button click");
    tau.changePage("#voice_record");
    toggleRecording();
    setRecording();
}
function onRecordBtn2Click() {
    if (recordingLock) {
        return;
    }
    console.log("button click");
    tau.back();
    toggleRecording();
    setRecording();
}


/**
 * Handles page show event.
 */
function onPageShow() {
    recordingLock = false;
    toggleRecording(false);
}


/**
 * Registers event listeners.
 */
function bindEvents() {
    page2.addEventListener('pageshow', onPageShow);
    recordBtn.addEventListener('touchstart', onRecordBtnClick);
    recordBtn2.addEventListener('touchstart', onRecordBtn2Click);
}


/**
 * Handles models.stream.ready event.
 * @param {event} ev
 */
function onStreamReady(ev) {
    stream = ev.detail.stream;
    audio.registerStream(stream);
}

/**
 * Handles models.stream.cannot.access.audio event.
 */
function onStreamCannotAccessAudio() {
    console.log("cannot access audio");
}

/**
 * Inits stream.
 */
function initStream() {
    audioStream.getStream();
}

/**
 * Handles audio.ready event.
 */
function onAudioReady() {
    console.log('onAudioReady()');
}

/**
 * Handles audio.error event.
 */
function onAudioError() {
    console.log('onAudioError()');
}

/**
 * Handles audio.recording.start event.
 */
function onRecordingStart() {
    toggleRecording(true);
    recordingLock = false;
}

/**
 * Handles audio.recording.done event.
 * @param {event} ev
 */
function onRecordingDone(ev) {
    toggleRecording(false);
    sendFile(FILE_PATH);
}

/**
 * Handles audio.recording.error event.
 * @param {CustomEvent} ev
 */
function onRecordingError(ev) {
    var error = ev.detail.error;

    if (error === ERROR_FILE_WRITE) {
        console.error(NO_FREE_SPACE_MSG);
    } else {
        console.error('Error: ' + error);
    }
    toggleRecording(false);
}

/**
 * Handles application exit event.
 */
function onApplicationExit() {
    exitInProgress = true;
    if (audio.isReady()) {
        audio.release();
        stream.stop();
    }
}

/**
 * Function called when application visibility state changes
 * (document.visibilityState changed to 'visible' or 'hidden').
 */


/**
 * Inits module.
 */

page2 = document.getElementById('voice');
recordBtn = document.getElementById(
    'main-navigation-bar'
);
recordBtn2= document.getElementById(
	'main-navigation-bar2'
);
recordBtnIcon = document.getElementById(
     'main-navigation-bar-button'
);

myEvent.listeners({
    'application.exit': onApplicationExit,

    'models.stream.ready': onStreamReady,
    'models.stream.cannot.access.audio': onStreamCannotAccessAudio,

    'audio.ready': onAudioReady,
    'audio.error': onAudioError,

    'audio.recording.start': onRecordingStart,
    'audio.recording.done': onRecordingDone,
    'audio.recording.error': onRecordingError,
});
bindEvents();
initStream();


} () 
);


