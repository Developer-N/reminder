package ir.namoo.reminder

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ir.namoo.reminder.ui.RemindActivity

class RemindReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED -> scheduleAll(context)
            BROADCAST_REMIND -> {
                val id = intent.getIntExtra(REMIND_ID, -1)
                if (id == -1) return
                context.startActivity(Intent(context, RemindActivity::class.java).apply {
                    putExtra(REMIND_ID, id)
                })
            }
        }
    }
}