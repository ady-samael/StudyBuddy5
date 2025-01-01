package com.ady.studybuddy5

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class PomodoroActivity : AppCompatActivity() {

    private var isTimerRunning = false
    private var isTimerPaused = false
    private var timeLeftInMillis: Long = 0
    private var workTimeInMillis: Long = 0
    private var breakTimeInMillis: Long = 0
    private var numberOfCycles: Int = 1
    private var currentCycle: Int = 1
    private var inBreakPhase = false

    private lateinit var timerText: TextView
    private lateinit var phaseText: TextView // Phase display
    private lateinit var cycleCounterText: TextView // Cycle counter display
    private lateinit var startButton: Button
    private lateinit var pauseButton: Button
    private lateinit var resumeButton: Button
    private lateinit var stopButton: Button

    private val timerUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val timeLeft = intent?.getLongExtra(TimerService.EXTRA_TIME_LEFT, 0L) ?: 0L
            timeLeftInMillis = timeLeft
            updateTimerDisplay(timeLeftInMillis)

            if (timeLeftInMillis == 0L && !isTimerPaused) {
                isTimerRunning = false
                showTimerFinishedDialog()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // UI Setup
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
        }

        timerText = TextView(this).apply {
            text = "00:00"
            textSize = 32f
        }

        // Phase indicator
        phaseText = TextView(this).apply {
            text = "Phase: Work" // Initial phase
            textSize = 24f
        }

        // Cycle counter (added)
        cycleCounterText = TextView(this).apply {
            text = "Cycle: $currentCycle of $numberOfCycles"
            textSize = 18f
        }

        val workTimeInput = EditText(this).apply {
            hint = "Work Time (minutes)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }

        val breakTimeInput = EditText(this).apply {
            hint = "Break Time (minutes)"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }

        val cyclesInput = EditText(this).apply {
            hint = "Number of Cycles"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }

        startButton = Button(this).apply {
            text = "Start"
            setOnClickListener {
                if (!isTimerRunning && !isTimerPaused) {
                    val workTimeInMinutes = workTimeInput.text.toString().toLongOrNull() ?: 25
                    val breakTimeInMinutes = breakTimeInput.text.toString().toLongOrNull() ?: 5
                    val cycles = cyclesInput.text.toString().toIntOrNull() ?: 1

                    workTimeInMillis = workTimeInMinutes * 60 * 1000
                    breakTimeInMillis = breakTimeInMinutes * 60 * 1000
                    numberOfCycles = cycles
                    currentCycle = 1
                    timeLeftInMillis = workTimeInMillis
                    inBreakPhase = false

                    updatePhaseDisplay()
                    updateCycleCounter()
                    startTimer(timeLeftInMillis)
                }
            }
        }

        pauseButton = Button(this).apply {
            text = "Pause"
            setOnClickListener {
                if (isTimerRunning) {
                    pauseTimer()
                }
            }
        }

        resumeButton = Button(this).apply {
            text = "Resume"
            setOnClickListener {
                if (isTimerPaused) {
                    resumeTimer()
                }
            }
        }

        stopButton = Button(this).apply {
            text = "Stop"
            setOnClickListener {
                if (isTimerRunning || isTimerPaused) {
                    stopTimer()
                }
            }
        }

        layout.addView(timerText)
        layout.addView(phaseText) // Add phaseText to the layout
        layout.addView(cycleCounterText) // Add cycleCounterText to the layout
        layout.addView(workTimeInput)
        layout.addView(breakTimeInput)
        layout.addView(cyclesInput)
        layout.addView(startButton)
        layout.addView(pauseButton)
        layout.addView(resumeButton)
        layout.addView(stopButton)

        setContentView(layout)

        val filter = IntentFilter(TimerService.TIMER_UPDATE_ACTION)
        registerReceiver(timerUpdateReceiver, filter)

        updateButtonStates()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(timerUpdateReceiver)
    }

    private fun startTimer(timeInMillis: Long) {
        val intent = Intent(this, TimerService::class.java).apply {
            putExtra(TimerService.EXTRA_TIME_LEFT, timeInMillis)
        }
        ContextCompat.startForegroundService(this, intent)
        isTimerRunning = true
        isTimerPaused = false
        updateButtonStates()
    }

    private fun pauseTimer() {
        val intent = Intent(this, TimerService::class.java).apply {
            action = TimerService.ACTION_PAUSE
        }
        startService(intent)
        isTimerRunning = false
        isTimerPaused = true
        updateButtonStates()
    }

    private fun resumeTimer() {
        val intent = Intent(this, TimerService::class.java).apply {
            putExtra(TimerService.EXTRA_TIME_LEFT, timeLeftInMillis)
        }
        ContextCompat.startForegroundService(this, intent)
        isTimerRunning = true
        isTimerPaused = false
        updateButtonStates()
    }

    private fun stopTimer() {
        val intent = Intent(this, TimerService::class.java).apply {
            action = TimerService.ACTION_STOP
        }
        startService(intent)
        isTimerRunning = false
        isTimerPaused = false
        timeLeftInMillis = 0
        currentCycle = 1
        updateTimerDisplay(0)
        updatePhaseDisplay() // Reset phase to work
        updateCycleCounter() // Reset cycle counter
        updateButtonStates()
    }

    private fun showTimerFinishedDialog() {
        AlertDialog.Builder(this)
            .setTitle("Timer Finished!")
            .setMessage(
                if (inBreakPhase)
                    "Break time is over. Ready to work?"
                else
                    "Work time is over. Take a break?"
            )
            .setPositiveButton("OK") { _, _ ->
                if (inBreakPhase) {
                    // Only increment cycle after the break phase
                    if (currentCycle < numberOfCycles) {
                        currentCycle++
                        inBreakPhase = false
                        timeLeftInMillis = workTimeInMillis
                        updatePhaseDisplay()
                        updateCycleCounter()
                        startTimer(timeLeftInMillis)
                    } else {
                        // If last cycle is completed, show completion dialog and stop
                        stopTimer()
                        showProcessCompleteDialog()
                    }
                } else {
                    // Move to the break phase
                    inBreakPhase = true
                    timeLeftInMillis = breakTimeInMillis
                    updatePhaseDisplay()
                    startTimer(timeLeftInMillis)
                }
            }
            .setCancelable(false)
            .show()
    }


    private fun showProcessCompleteDialog() {
        AlertDialog.Builder(this)
            .setTitle("Pomodoro Process Complete")
            .setMessage("Congratulations! You've completed your Pomodoro cycles.")
            .setPositiveButton("OK") { _, _ -> stopTimer() }
            .setCancelable(false)
            .show()
    }

    private fun updateTimerDisplay(timeInMillis: Long) {
        val minutes = (timeInMillis / 60000).toInt()
        val seconds = ((timeInMillis % 60000) / 1000).toInt()
        timerText.text = String.format("%02d:%02d", minutes, seconds)
    }

    private fun updatePhaseDisplay() {
        phaseText.text = if (inBreakPhase) "Phase: Break" else "Phase: Work"
    }

    private fun updateCycleCounter() {
        cycleCounterText.text = "Cycle: $currentCycle of $numberOfCycles"
    }

    private fun updateButtonStates() {
        startButton.isEnabled = !isTimerRunning && !isTimerPaused
        pauseButton.isEnabled = isTimerRunning
        resumeButton.isEnabled = isTimerPaused
        stopButton.isEnabled = isTimerRunning || isTimerPaused
    }
}
