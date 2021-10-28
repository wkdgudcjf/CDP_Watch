/*global define, console, navigator, window, setTimeout*/
/*jslint regexp: true*/

/**
 * Audio model module
 */
var audio = ( function modelsAudio() {
        /*jshint maxstatements:42*/

        var e = myEvent,

            MAX_RECORDING_TIME = 10000,
            AUDIO_LENGTH_CHECK_INTERVAL = 10,

            AUDIO_DESTINATION_DIRECTORY = '/opt/usr/media/Sounds',
            
            audioControl = null,
            audioPath = '',
            audioLengthCheckInterval = null,
            audioRecordingStartTime = null,
            audioRecordingTime = 0,
            busy = false;

        /**
         * Executes when audio control is created from stream.
         * @param {audioControl} control
         */
        function onAudioControlCreated(control) {
            audioControl = control;
            e.fire('audio.ready');
        }

        /**
         * Executes on audio control creation error
         * @param {object} error
         */
        function onAudioControlError(error) {
            console.error(error);
            e.fire('audio.error', {error: error});
        }

        /**
         * Registers stream that audio controls.
         * @param {LocalMediaStream} mediaStream
         */
        function registerStream(mediaStream) {
            navigator.tizCamera.createCameraControl(
                mediaStream,
                onAudioControlCreated,
                onAudioControlError
            );
        }

        /**
         * Checks if audio length is greater then MAX_RECORDING_TIME.
         * If it does, recording will be stopped.
         */
        function checkAudioLength() {
            var currentTime = new Date();

            audioRecordingTime = currentTime - audioRecordingStartTime;
            if (audioRecordingTime > MAX_RECORDING_TIME) {
                stopRecording();
            }
        }

        /**
         * Starts tracing audio length.
         * When audio length reaches MAX_RECORDING_TIME, recording
         * will be stopped automatically.
         */
        function startTracingAudioLength() {
            audioRecordingStartTime = new Date();
            audioLengthCheckInterval = window.setInterval(
                checkAudioLength,
                AUDIO_LENGTH_CHECK_INTERVAL
            );
        }

        /**
         * Stops tracing audio length.
         */
        function stopTracingAudioLength() {
            window.clearInterval(audioLengthCheckInterval);
            audioLengthCheckInterval = null;
        }

        /**
         * Executes when recording starts successfully.
         */
        function onRecordingStartSuccess() {
            startTracingAudioLength();
            e.fire('audio.recording.start');
        }

        /**
         * Executes when error occurs during recording start.
         * @param {object} error
         */
        function onRecordingStartError(error) {
            busy = false;
            e.fire('audio.recording.error', {error: error});
        }

        /**
         * Executes when audio settings are applied.
         */
        function onAudioSettingsApplied() {
            audioControl.recorder.start(
                onRecordingStartSuccess,
                onRecordingStartError
            );
        }

        /**
         * Executes when error occurs during applying audio settings
         * @param {object} error
         */
        function onAudioSettingsError(error) {
            console.error('settings.error');
            busy = false;
            e.fire('audio.recording.error', {error: error});
        }

        /**
         * Returns recording format
         * @return {string}
         */
        function getRecordingFormat() {
            return 'amr';
        }

        /**
         * Creates filename for new audio.
         * @return {string}
         */
        function createAudioFileName() {
            var currentDate = new Date(),
                extension = getRecordingFormat(),
                fileName = '';

            fileName = /*dateHelper.format(currentDate, 'yyyymmdd_HHMMSS')*/"test01" +
                '.' + extension;

            return fileName;
        }

        /**
         * Starts audio recording.
         * When recording is started successfully, audio.recording.start event
         * is fired. If error occurs, audio.recording.error event is fired.
         * @return {boolean} If process starts true is returned,
         * false otherwise (audio other operation is in progress).
         */
        function startRecording() {
            var settings = {},
                fileName = '';

            if (busy) {
                return false;
            }

            busy = true;
            fileName = createAudioFileName();
            audioPath = AUDIO_DESTINATION_DIRECTORY + '/' + fileName;

            settings.fileName = fileName;
            settings.recordingFormat = getRecordingFormat();

            audioControl.recorder.applySettings(
                settings,
                onAudioSettingsApplied,
                onAudioSettingsError
            );

            return true;
        }

        /**
         * Executes when audio recording stops successfully.
         */
        function onAudioRecordingStopSuccess() {
            busy = false;
            e.fire('audio.recording.done', {path: audioPath});
            audioRecordingTime = 0;
        }

        /**
         * Executes when audio recording stop fails.
         * @param {object} error
         */
        function onAudioRecordingStopError(error) {
            busy = false;
            e.fire('audio.recording.error', {error: error});
            audioRecordingTime = 0;
        }

        /**
         * Stop audio recording.
         * When recording is stopped, audio.recording.done event is fired
         * with file path as a data.
         * If error occurs audio.recording error is fired.
         * Recording will stop also if MAX_RECORDING_TIME will be reached.
         */
        function stopRecording() {
            stopTracingAudioLength();
            audioControl.recorder.stop(onAudioRecordingStopSuccess,
                onAudioRecordingStopError
            );
        }

        /**
         * Returns current recording time in milliseconds.
         * @return {number}
         */
        function getRecordingTime() {
            return audioRecordingTime;
        }

        /**
         * Releases audio.
         */
        function release() {
            if (busy && isRecording()) {
                stopRecording();
            }
            busy = false;
            if (audioControl) {
                audioControl.release();
                audioControl = null;
                e.fire('audio.release');
            }
        }

        /**
         * Returns true if audio is ready to work,
         * false otherwise.
         * @return {boolean}
         */
        function isReady() {
            return audioControl !== null;
        }

        /**
         * Returns true if audio is recording,
         * false otherwise.
         * @return {boolean}
         */
        function isRecording() {
            return !!audioLengthCheckInterval;
        }

        return {
            MAX_RECORDING_TIME: MAX_RECORDING_TIME,

            registerStream: registerStream,
            release: release,
            isReady: isReady,
            isRecording: isRecording,

            startRecording: startRecording,
            stopRecording: stopRecording,
            getRecordingTime: getRecordingTime
        };
    })();
