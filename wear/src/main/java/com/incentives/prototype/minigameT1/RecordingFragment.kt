package com.incentives.prototype.minigameT1

import android.Manifest
import android.app.Activity
import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.incentives.prototype.EnumStateType
import com.incentives.prototype.MainViewModel
import com.incentives.prototype.R
import com.incentives.prototype.SendMessage
import com.incentives.prototype.SharedViewModel
import com.incentives.prototype.databinding.FragmentRecordingBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File
import java.util.Locale
import java.util.concurrent.TimeUnit

class RecordingFragment : Fragment() {

    private val RECORD_AUDIO_REQUEST_CODE = 1234
    lateinit var minigameT1Activity:MinigameT1
    private fun initRecordingView() {
        toggleRecordingButton = view?.findViewById(R.id.mic_button)
        tapToSpeakText = view?.findViewById(R.id.tap_to_speak_text)
        backButton = view?.findViewById(R.id.back_button)
        timeText = view?.findViewById(R.id.time_text)
        thinking = view?.findViewById(R.id.overlay_image)

        toggleRecordingButton?.setOnClickListener {
            if (viewModel.isRecording) {
                toggleRecordingButton?.imageAlpha = 255 // 100% opacity
                //tapToSpeakText?.text = "Talk"
                stopRecording()
                handler.removeCallbacks(runnable)
                elapsedTime = 0L
            } else {
                startTime = SystemClock.uptimeMillis()
                handler.postDelayed(runnable, 0)
                toggleRecordingButton?.imageAlpha = 128 // 50% opacity
                //tapToSpeakText?.text = "Talking"
                startRecording()
            }
        }

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)
        setupDoneButton()

        //viewModel.textState.observe(this.viewLifecycleOwner, doneButtonVisibilityObserver)
        viewModel.stateLiveData.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                1 -> {
                    timeText?.visibility = View.VISIBLE
                    thinking?.visibility = View.GONE
                    tapToSpeakText?.text = "Talking"
                    backButton?.visibility = View.INVISIBLE
                }
                2 -> {
                    timeText?.visibility = View.GONE
                    thinking?.visibility = View.VISIBLE
                    tapToSpeakText?.text = "Thinking"
                    backButton?.visibility = View.INVISIBLE
                }
                3 -> {
                    val i= 0
                }
                4 -> {
                    val currentNavHost = minigameT1Activity.supportFragmentManager.primaryNavigationFragment as NavHostFragment
                    if(currentNavHost != null)
                        currentNavHost.navController.navigate(R.id.pagerHint)
                }
                else -> {
                    timeText?.visibility = View.GONE
                    thinking?.visibility = View.GONE
                    tapToSpeakText?.text = "Talk"
                    backButton?.visibility = View.VISIBLE
                }
            }
        })
    }
    private fun requestRecordAudioPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), RECORD_AUDIO_REQUEST_CODE
        )
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if ((requestCode == RECORD_AUDIO_REQUEST_CODE) && grantResults.isNotEmpty()) {
            val granted = grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED

            if (granted) {
                initRecordingView()
            } else {
                Toast.makeText(requireActivity(), "Permissions to record audio from microphone must be granted.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val viewModel by viewModels<MainViewModel>({ requireActivity() })
    private lateinit var sharedViewModel: SharedViewModel

    private val doneButtonVisibilityObserver: Observer<String> = Observer { newText ->

        if (newText.isNotEmpty() && viewModel.isRecording && newText == "Listening...") {
            timeText?.visibility = View.VISIBLE
            thinking?.visibility = View.GONE
            tapToSpeakText?.text = "Talking"
            backButton?.visibility = View.INVISIBLE
        }
        else if (newText.isNotEmpty() && !viewModel.isRecording && newText == "Generating...") {
            thinking?.visibility = View.VISIBLE
            tapToSpeakText?.text = "Thinking"
            backButton?.visibility = View.INVISIBLE
        }
            else
                if (newText.isNotEmpty() && !viewModel.isRecording && newText != "Generating..." && newText != "Listening...") {
                    timeText?.visibility = View.GONE
                    thinking?.visibility = View.GONE
                    tapToSpeakText?.text = "Talk"
                    backButton?.visibility = View.VISIBLE
            }
    }

    val runnable = object: Runnable {
        override fun run() {
            elapsedTime = (SystemClock.uptimeMillis() - startTime)/1000
            val minutes = TimeUnit.SECONDS.toMinutes(elapsedTime)
            val seconds = TimeUnit.SECONDS.toSeconds(elapsedTime) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.SECONDS.toMinutes(elapsedTime) )
            timeText?.text = String.format("%02d:%02d", minutes, seconds)
            handler.postDelayed(this, 1000)
        }
    }

    private var timeText: TextView? = null
    private var thinking: ImageView? = null
    private var toggleRecordingButton: ImageButton? = null
    private var tapToSpeakText: TextView? = null
    private var backButton: ImageButton? = null

    val handler = Handler(Looper.getMainLooper())
    var elapsedTime = 0L
    var startTime = 0L

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        minigameT1Activity = activity as MinigameT1
        return inflater.inflate(R.layout.fragment_recording, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        MyTimer.ChangeCurrentState(EnumStateType.RECORDER.ordinal)
        viewModel.initStateLiveData()
        view.keepScreenOn = true
        val hasAudioPermission = ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        val hasStoragePermission = ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED

        if (hasAudioPermission && hasStoragePermission) {
            initRecordingView()
        } else {
            requestRecordAudioPermission()
        }
    }

    private fun setupDoneButton() {
        backButton?.setOnClickListener {
            findNavController().navigate(R.id.generateMenu)
        }
    }

    private fun startRecording() {
        viewModel.toggleRecording(requireActivity().externalCacheDir, {})
    }

    private fun stopRecording() {
        if (viewModel.isRecording) {
            viewModel.toggleRecording(requireActivity().externalCacheDir) {
                findNavController().popBackStack()
            }
        }
    }

    companion object {
        private const val SPEECH_RECOGNITION_REQUEST_CODE = 123
    }

    override fun onDestroyView() {
        super.onDestroyView()
        view?.keepScreenOn = false
        toggleRecordingButton = null
    }
}