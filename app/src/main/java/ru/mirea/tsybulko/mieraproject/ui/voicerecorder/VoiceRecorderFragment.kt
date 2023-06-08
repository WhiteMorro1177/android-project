package ru.mirea.tsybulko.mieraproject.ui.voicerecorder

import android.Manifest
import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import ru.mirea.tsybulko.mieraproject.R
import ru.mirea.tsybulko.mieraproject.databinding.FragmentVoiceRecorderBinding
import java.io.File
import java.io.IOException


class VoiceRecorderFragment : Fragment() {
    private lateinit var binding: FragmentVoiceRecorderBinding

    private var recorder: MediaRecorder = MediaRecorder()
    private var player: MediaPlayer = MediaPlayer()

    private lateinit var recordFilePath: String
    private var isRecording = false
    private var isPlaying = false

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootFragmentView = inflater.inflate(R.layout.fragment_voice_recorder, container, false)
        binding = FragmentVoiceRecorderBinding.bind(rootFragmentView)

        val requiredPermissions = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )

        checkAndRequestPermissions(requiredPermissions)

        val recordButton = binding.recordButton
        val playButton = binding.playButton.apply {
            isEnabled = false
        }

        recordFilePath = File(
            this.context!!.getExternalFilesDir(Environment.DIRECTORY_MUSIC),
            "/audiorecordtest.3gp"
        ).absolutePath

        // record button click
        recordButton.setOnClickListener {
            if (isRecording) {
                recordButton.text = "Start recording"
                playButton.isEnabled = true
                stopRecording()
            } else {
                recordButton.text = "Stop recording"
                playButton.isEnabled = false
                startRecording()
            }
            isRecording = !isRecording
        }

        // play button click
        playButton.setOnClickListener {
            if (!isPlaying) {
                playButton.text = "Stop playing"
                startPlaying()
            }
            else {
                playButton.text = "Start playing"
                playButton.isEnabled = true
                stopPlaying()
            }
            isPlaying = !isPlaying
        }

        return rootFragmentView
    }

    private fun startRecording() {
        recorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(recordFilePath)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        }
        try {
            recorder.prepare()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        recorder.start()
    }

    private fun stopRecording() {
        recorder.run {
            stop()
            release()
        }
    }

    private fun startPlaying() {
        try {
            player = MediaPlayer().apply {
                setDataSource(recordFilePath)
                prepare()
                start()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun stopPlaying() {
        player.run {
            release()
        }
    }

    private fun checkAndRequestPermissions(permissionsToCheck: Array<String>) {
        val permissionsToRequest: ArrayList<String> = ArrayList()
        for (permission in permissionsToCheck) {
            if (ContextCompat.checkSelfPermission(
                    this.context!!,
                    permission
                ) != PermissionChecker.PERMISSION_GRANTED
            ) {
                permissionsToRequest.add(permission)
            }
        }

        requestPermissions(permissionsToRequest.toTypedArray(), 200)
    }
}