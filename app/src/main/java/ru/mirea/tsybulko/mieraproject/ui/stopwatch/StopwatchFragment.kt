package ru.mirea.tsybulko.mieraproject.ui.stopwatch

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ru.mirea.tsybulko.mieraproject.R
import ru.mirea.tsybulko.mieraproject.databinding.FragmentStopwatchBinding

class StopwatchFragment : Fragment() {
    private lateinit var binder: FragmentStopwatchBinding
    private var isStopwatchActive = false

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootFragmentView = inflater.inflate(R.layout.fragment_stopwatch, container, false)
        binder = FragmentStopwatchBinding.inflate(layoutInflater)

        binder.startButton.setOnClickListener {
            if (!isStopwatchActive) {
                startStopwatch()
                isStopwatchActive = true
            }
        }

        binder.stopButton.setOnClickListener {
            if (isStopwatchActive) {
                pauseStopwatch()
                isStopwatchActive = false
            }
        }

        binder.resetButton.setOnClickListener {
            if (!isStopwatchActive) { resetStopwatch() }
        }

        return rootFragmentView
    }

    private fun startStopwatch() {
        Log.d("sw", "started")
        val stopwatchService = Intent(context, StopwatchService::class.java)
        stopwatchService.putExtra(StopwatchService.STOPWATCH_ACTION, StopwatchService.START)
        context?.startService(stopwatchService)
    }

    private fun pauseStopwatch() {
        Log.d("sw", "paused")
        val stopwatchService = Intent(context, StopwatchService::class.java)
        stopwatchService.putExtra(StopwatchService.STOPWATCH_ACTION, StopwatchService.PAUSE)
        context?.startService(stopwatchService)
    }

    private fun resetStopwatch() {
        Log.d("sw", "reset")
        val stopwatchService = Intent(context, StopwatchService::class.java)
        stopwatchService.putExtra(StopwatchService.STOPWATCH_ACTION, StopwatchService.RESET)
        context?.startService(stopwatchService)
    }
}