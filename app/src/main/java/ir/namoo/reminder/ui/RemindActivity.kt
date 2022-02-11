package ir.namoo.reminder.ui

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.AndroidEntryPoint
import ir.namoo.reminder.CHANNEL_ID
import ir.namoo.reminder.R
import ir.namoo.reminder.REMIND_ID
import ir.namoo.reminder.db.Word
import ir.namoo.reminder.ui.theme.ReminderTheme
import kotlin.random.Random


@AndroidEntryPoint
class RemindActivity : ComponentActivity() {
    private var isOk = false
    private var showWord: Word? = null
    private lateinit var notification: Uri
    private lateinit var r: Ringtone
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
        r = RingtoneManager.getRingtone(applicationContext, notification)
        setContent {
            ReminderTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val id = intent.getIntExtra(REMIND_ID, 0)
                    val viewModel = viewModels<WordViewModel>().value
                    viewModel.getWordByID(id)
                    val w = viewModel.word.observeAsState()
                    w.value?.let { word ->
                        showWord = word
                        r.play()
                        val activity = (LocalContext.current as? Activity)
                        Card(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp), verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    IconButton(onClick = {
                                        isOk = true
                                        activity?.finish()
                                    }) {
                                        Icon(
                                            Icons.Filled.Done,
                                            contentDescription = "Done",
                                            modifier = Modifier.size(100.dp),
                                            tint = MaterialTheme.colors.primary
                                        )
                                    }
                                    Spacer(modifier = Modifier.size(50.dp))
                                    Text(
                                        text = word.word,
                                        modifier = Modifier.fillMaxWidth(),
                                        fontSize = 24.sp,
                                        style = MaterialTheme.typography.h1,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        isOk = true
    }

    override fun onDestroy() {
        if (!isOk) {
            val builder = NotificationCompat.Builder(this, CHANNEL_ID).apply {
                setSmallIcon(R.drawable.ic_baseline_notifications_24)
                setContentTitle(showWord?.word ?: "-")
                priority = NotificationCompat.PRIORITY_DEFAULT
                setAutoCancel(true)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = getString(R.string.channel_name)
                val descriptionText = getString(R.string.channel_description)
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }
                val notificationManager: NotificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
            with(NotificationManagerCompat.from(this)) {
                notify(Random(Int.MAX_VALUE).nextInt(), builder.build())
            }
        }
        runCatching {
            r.stop()
        }
        super.onDestroy()
    }

}