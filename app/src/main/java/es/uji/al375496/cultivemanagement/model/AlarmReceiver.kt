package es.uji.al375496.cultivemanagement.model

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import es.uji.al375496.cultivemanagement.*


class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val title = intent.getStringExtra(TITLE)
        val text = intent.getStringExtra(TEXT)
        val id = intent.getIntExtra(ID, 0)
        val builder = NotificationCompat.Builder(context, REMINDERS)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setStyle(NotificationCompat.BigTextStyle()
                        .bigText(text))
        with(NotificationManagerCompat.from(context)) {
            notify(id, builder.build())
        }
    }
}