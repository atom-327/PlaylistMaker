package SecondCounter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.VibratorManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.practicum.playlistmaker.main.ui.MainActivity
import com.practicum.playlistmaker.R
import java.util.Locale

class SecondCounter : AppCompatActivity() {
    companion object {
        private const val REFRESH_SECONDS_VALUE_MILLIS = 1_000L
    }

    private var isRunning = true
    private var mainThreadHandler: Handler? = null
    private var seconds = 0L

    private lateinit var toolbarSecondsCounterButton: Toolbar
    private lateinit var secondsInput: EditText
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var secondsValue: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second_counter)

        mainThreadHandler = Handler(Looper.getMainLooper())

        toolbarSecondsCounterButton = findViewById(R.id.toolbarSecondCounterButton)
        secondsInput = findViewById(R.id.secondsInput)
        startButton = findViewById(R.id.startButton)
        stopButton = findViewById(R.id.stopButton)
        secondsValue = findViewById(R.id.secondsValue)

        startButton.setOnClickListener {
            isRunning = true
            seconds = secondsInput.text.toString().toLong()
            it.setEnabled(false)
            hideSoftKeyboard(it)
            secondsValue.text = formatDuration(seconds)
            mainThreadHandler?.post(
                object : Runnable {
                    override fun run() {
                        if (seconds != 1L && isRunning) {
                            seconds -= 1
                            secondsValue.text = formatDuration(seconds)
                            mainThreadHandler?.postDelayed(
                                this,
                                REFRESH_SECONDS_VALUE_MILLIS,
                            )
                        } else {
                            secondsValue.text = "Done!"
                            startButton.setEnabled(true)
                            vibrateDevice(100)
                        }
                    }
                }
            )
        }

        stopButton.setOnClickListener {
            isRunning = false
            hideSoftKeyboard(it)
        }

        toolbarSecondsCounterButton.setNavigationOnClickListener {
            val returnIntent = Intent(this, MainActivity::class.java)
            startActivity(returnIntent)
            finish()
        }
    }

    private fun hideSoftKeyboard(view: View) {
        val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun formatDuration(durationInMillis: Long): String {
        val seconds = (durationInMillis) % 60
        val minutes = (durationInMillis / 60) % 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    @SuppressLint("NewApi")
    private fun vibrateDevice(duration: Long) {
        val vibrationManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        val vibrator = vibrationManager.defaultVibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    duration, VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else @Suppress("DEPRECATION") vibrator.vibrate(duration)
    }
}
