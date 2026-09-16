package com.example.sp2.alarms

import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.example.sp2.R
import com.example.sp2.data.local.DatabaseProvider
import com.example.sp2.data.local.toAlarm
import com.example.sp2.model.Alarm
import com.example.sp2.ui.theme.Sp2Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlarmRingActivity : ComponentActivity() {

    private var ringtone: Ringtone? = null
    private var vibrator: Vibrator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Shows this screen over the lock screen and wakes the display
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        }

        val alarmId = intent.getIntExtra(AlarmScheduler.EXTRA_ALARM_ID, -1)

        setContent {
            Sp2Theme {

                var alarm by remember { mutableStateOf<Alarm?>(null) }

                LaunchedEffect(alarmId) {

                    val entity = withContext(Dispatchers.IO) {
                        DatabaseProvider.getDatabase(applicationContext)
                            .alarmDao().getAlarmById(alarmId)
                    }

                    alarm = entity?.toAlarm()
                    startRingingAndVibrating(entity?.soundUri)
                }

                AlarmRingScreen(
                    alarm = alarm,
                    onDismiss = {
                        stopRingingAndVibrating()
                        NotificationHelper.dismiss(applicationContext, alarmId)
                        finish()
                    },
                    onSnooze = {
                        val current = alarm
                        stopRingingAndVibrating()
                        NotificationHelper.dismiss(applicationContext, alarmId)

                        if (current != null) {
                            AlarmScheduler.scheduleSnooze(
                                applicationContext,
                                current,
                                current.snoozeMinutes
                            )
                        }

                        finish()
                    }
                )
            }
        }
    }

    private fun startRingingAndVibrating(soundUriString: String?) {

        val soundUri: Uri = soundUriString?.let { Uri.parse(it) }
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)

        ringtone = RingtoneManager.getRingtone(applicationContext, soundUri)
        ringtone?.play()

        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            (getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager).defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(VIBRATOR_SERVICE) as Vibrator
        }

        val pattern = longArrayOf(0, 800, 800)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createWaveform(pattern, 0))
        } else {
            @Suppress("DEPRECATION")
            vibrator?.vibrate(pattern, 0)
        }
    }

    private fun stopRingingAndVibrating() {
        ringtone?.stop()
        vibrator?.cancel()
    }

    override fun onDestroy() {
        super.onDestroy()
        stopRingingAndVibrating()
    }
}

@Composable
private fun AlarmRingScreen(
    alarm: Alarm?,
    onDismiss: () -> Unit,
    onSnooze: () -> Unit
) {

    Surface(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Text(
                text = alarm?.time?.toString() ?: "--:--",
                style = MaterialTheme.typography.displayLarge
            )

            if (!alarm?.label.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = alarm?.label ?: "",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Button(
                onClick = onSnooze,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.alarm_snooze))
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.alarm_dismiss))
            }
        }
    }
}