package com.incentives.prototype

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.media.MediaRecorder
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.*
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.audio.TranscriptionRequest
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.launch
import okio.source
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.util.Optional
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay


private const val TAG = "MainViewModel"

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val _textState = MutableLiveData("")
    val textState: LiveData<String> get() = _textState

    private val _stateLiveData = MutableLiveData(0)
    val stateLiveData: LiveData<Int> get() = _stateLiveData

    private var _isRecording = false
    val isRecording: Boolean get() = _isRecording // Add readOnly property for external access

    private val _isRecordingLiveData = MutableLiveData(false) // Rename isRecordingLiveData field
    //val isRecordingLiveData: LiveData<Boolean> get() = _isRecordingLiveData // Add readOnly property for external access

    private val openAI = OpenAI(BuildConfig.OPENAI_API_KEY)

    private lateinit var fileLocation: String
    private lateinit var recorder: MediaRecorder

    fun initStateLiveData() {
        _stateLiveData.value = 0
    }
    fun cleanCache() {
        getApplication<Application>().cacheDir.deleteRecursively()
    }

    @OptIn(BetaOpenAI::class)
    fun toggleRecording(externalCacheDir: File?, callback: (String) -> Unit) {
        _isRecording = !_isRecording // Swap position of this assignment

        if (!isRecording) { // Swap position of this condition
            // Initialize fileLocation beforehand.
            fileLocation = getApplication<Application>().cacheDir.path + "/recording.mp3"
        }

        if (isRecording) {
            fileLocation = getApplication<Application>().cacheDir.path + "/recording.mp3"
            recorder = MediaRecorder().apply {
                setOutputFile(fileLocation)
                setAudioSource(MediaRecorder.AudioSource.DEFAULT)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)

                Log.d("MediaRecorder Log", "File location: $fileLocation")
                Log.d("MediaRecorder Log", "Audio source: MIC")
                Log.d("MediaRecorder Log", "Output format: MPEG_4")
                Log.d("MediaRecorder Log", "Audio encoder: AAC")
            }

            recorder?.apply {
                setOnErrorListener { mr, what, extra ->
                    Log.e("MediaRecorder", "Error occurred: $what - $extra")
                }
                setOnInfoListener { mr, what, extra ->
                    Log.i("MediaRecorder", "Info: $what - $extra")
                }
            }

            try {
                recorder?.prepare()
                viewModelScope.launch {
                    delay(500) // Add delay here
                    recorder?.start()
                    _isRecordingLiveData.value = true
                    _textState.value = "Listening..."
                    _stateLiveData.value = 1
                    Log.d(TAG, "toggleRecording: Recording started")
                }
            } catch (e: IOException) {
                _textState.value = "error in recorder prepare"
                _stateLiveData.value = 3
                Log.d("error", "error")
            }
        }
        else {
            if (::recorder.isInitialized) {
                recorder?.stop()
            }
            _isRecordingLiveData.value = false

            val audioFile = File(fileLocation)
            audioFile.createNewFile()
            val request = TranscriptionRequest(
                audio = FileSource(fileLocation, source = audioFile.source()),
                model = ModelId("whisper-1")
            )
            viewModelScope.launch {
                _textState.value = "Generating..."
                _stateLiveData.value = 2
                try {
                    val transcription = openAI.transcription(request)
                    _textState.value = transcription.text
                    _stateLiveData.value = 4
                } catch (e: Exception) {
                    _textState.value = "Sorry, we couldn't connect to the OpenAI servers right now. Please try again later!"
                    _stateLiveData.value = 3
                }
                cleanCache()
                //File(fileLocation).delete()
            }
        }
    }

    private suspend fun resumeRecordWithDelay() {
        delay(100)
        recorder?.start()
        _isRecordingLiveData.value = true
        _textState.value = "Listening..."
        _stateLiveData.value = 1
        Log.d(TAG, "toggleRecording: Recording started")
    }

    private fun createOutputUri(): Uri {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "recording_${System.currentTimeMillis()}.mp3")
            put(MediaStore.MediaColumns.MIME_TYPE, "audio/mpeg")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MUSIC)
        }

        val contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        return getApplication<Application>().contentResolver.insert(contentUri, contentValues)
            ?: throw IOException("Failed to create new MediaStore record.")
    }

    @OptIn(BetaOpenAI::class)
    fun processUsingWhisper(audioFile: File, callback: (String) -> Unit) {
        val request = TranscriptionRequest(
            audio = FileSource(audioFile.path, source = audioFile.source()),
            model = ModelId("whisper-1")
        )

        _textState.value = "Generating..."
        _stateLiveData.value = 2
        viewModelScope.launch {
            try {
                val transcription = openAI.transcription(request)
                _textState.value = transcription.text
                _stateLiveData.value = 4
                callback(transcription.text) // Add this line to execute callback
            } catch (e: Exception) {
                _textState.value = "Error encountered! Unable to connect to the OpenAI servers right now. Please try again later!"
                _stateLiveData.value = 3
            }

            cleanCache()
        }
    }

    override fun onCleared() {
        super.onCleared()
        if(this::recorder.isInitialized) {
            recorder.release()
        }
    }

}