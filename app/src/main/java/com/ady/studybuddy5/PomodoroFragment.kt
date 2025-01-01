package com.ady.studybuddy5

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.TextView
import android.widget.Button
import android.widget.LinearLayout

class PomodoroFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL

            val timerText = TextView(requireContext()).apply {
                text = "Pomodoro Timer"
                textSize = 24f
            }

            val startButton = Button(requireContext()).apply {
                text = "Start Timer"
                setOnClickListener {
                    // Start Pomodoro timer logic here
                }
            }

            addView(timerText)
            addView(startButton)
        }

        return rootView
    }
}
