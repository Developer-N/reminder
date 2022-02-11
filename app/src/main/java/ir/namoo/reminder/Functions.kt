package ir.namoo.reminder

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.getSystemService
import ir.namoo.reminder.db.Word
import ir.namoo.reminder.db.WordDB
import kotlinx.coroutines.runBlocking
import java.util.*

fun scheduleAll(context: Context) {
    val db = WordDB.getInstance(context.applicationContext)
    runBlocking {
        val list = db.WordDao().getAllWords()
        for (word in list)
            scheduleAlarm(context, word)
    }
}

fun scheduleAlarm(context: Context, word: String) {
    val db = WordDB.getInstance(context.applicationContext)
    runBlocking {
        val w = db.WordDao().getWordByWord(word)
        if (w != null)
            scheduleAlarm(context, w)
    }
}

fun scheduleAlarm(context: Context, word: Word) {
    //One Hour
    var date = Calendar.getInstance()
    date.timeInMillis = word.insertDate
    date.add(Calendar.HOUR_OF_DAY, 1)
    schedule(context, date, word.id * 5)

    //One Day
    date = Calendar.getInstance()
    date.timeInMillis = word.insertDate
    date.add(Calendar.DAY_OF_MONTH, 1)
    schedule(context, date, word.id * 5 + 1)

    //One week
    date = Calendar.getInstance()
    date.timeInMillis = word.insertDate
    date.add(Calendar.DAY_OF_MONTH, 7)
    schedule(context, date, word.id * 5 + 2)

    //One month
    date = Calendar.getInstance()
    date.timeInMillis = word.insertDate
    date.add(Calendar.MONTH, 1)
    schedule(context, date, word.id * 5 + 3)

    //three month
    date = Calendar.getInstance()
    date.timeInMillis = word.insertDate
    date.add(Calendar.MONTH, 3)
    schedule(context, date, word.id * 5 + 4)
}

private fun schedule(context: Context, date: Calendar, id: Int) {
    val alarmManager = context.getSystemService<AlarmManager>()
    if (alarmManager != null && !date.before(Calendar.getInstance())) {
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            Intent(context, RemindReceiver::class.java).apply {
                putExtra(REMIND_ID, id)
                action = BROADCAST_REMIND
            }, PendingIntent.FLAG_UPDATE_CURRENT or
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )
        alarmManager.setAlarmClock(
            AlarmManager.AlarmClockInfo(
                date.timeInMillis, pendingIntent
            ), pendingIntent
        )
    }
}

fun cancelSchedule(context: Context, id: Int) {
    val alarmManager = context.getSystemService<AlarmManager>()
    if (alarmManager != null) {
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            id,
            Intent(context, RemindReceiver::class.java).apply {
                putExtra(REMIND_ID, id)
                action = BROADCAST_REMIND
            }, PendingIntent.FLAG_UPDATE_CURRENT or
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )
        alarmManager.cancel(pendingIntent)
    }
}
