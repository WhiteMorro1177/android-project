package ru.mirea.tsybulko.mieraproject.ui.stopwatch

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.mirea.tsybulko.mieraproject.R
import ru.mirea.tsybulko.mieraproject.databinding.FragmentStopwatchBinding
import kotlin.math.roundToInt

class StopwatchFragment : Fragment() {
    private lateinit var binding: FragmentStopwatchBinding
    private var isStopwatchActive = false
    private lateinit var serviceIntent: Intent
    private var time = 0.0

    private lateinit var fragment: Context

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootFragmentView = inflater.inflate(R.layout.fragment_stopwatch, container, false)
        binding = FragmentStopwatchBinding.bind(rootFragmentView)

        fragment = this.context!!

        binding.startStopButton.setOnClickListener { startStopTimer() }
        binding.resetButton.setOnClickListener { resetTimer() }

        serviceIntent = Intent(this.context, StopwatchService::class.java)
        fragment.registerReceiver(updateTime, IntentFilter(StopwatchService.TIMER_UPDATED))
        return rootFragmentView
    }

    private fun resetTimer() {
        stopTimer()
        time = 0.0
        binding.timeTV.text = getTimeStringFromDouble(time)
    }

    private fun startStopTimer() = if (isStopwatchActive) stopTimer() else startTimer()

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    private fun startTimer() {
        serviceIntent.putExtra(StopwatchService.TIME_EXTRA, time)
        fragment.startService(serviceIntent)
        binding.startStopButton.text = "Stop"
        binding.startStopButton.icon = fragment.getDrawable(R.drawable.baseline_pause_24)
        isStopwatchActive = true
    }

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    private fun stopTimer() {
        fragment.stopService(serviceIntent)
        binding.startStopButton.text = "Start"
        binding.startStopButton.icon = fragment.getDrawable(R.drawable.baseline_play_arrow_24)
        isStopwatchActive = false
    }

    private val updateTime: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            time = intent.getDoubleExtra(StopwatchService.TIME_EXTRA, 0.0)
            binding.timeTV.text = getTimeStringFromDouble(time)
        }
    }

    private fun getTimeStringFromDouble(time: Double): String {
        val resultInt = time.roundToInt()
        val hours = resultInt % 86400 / 3600
        val minutes = resultInt % 86400 % 3600 / 60
        val seconds = resultInt % 86400 % 3600 % 60

        return makeTimeString(hours, minutes, seconds)
    }

    private fun makeTimeString(hour: Int, min: Int, sec: Int): String =
        String.format("%02d:%02d:%02d", hour, min, sec)
}