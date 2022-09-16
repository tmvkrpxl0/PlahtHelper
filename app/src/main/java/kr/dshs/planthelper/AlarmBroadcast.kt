package kr.dshs.planthelper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/**
 * 24시간 포맷 사용함
 */
const val notificationId = 1
const val channelId = "planthelper_by_tmvkrpxl0"

class AlarmBroadcast: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val builder = NotificationCompat.Builder(context, channelId)
            .setContentTitle("Alarm test")
            .setContentText("Contents")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val manager = NotificationManagerCompat.from(context)
        manager.notify(notificationId, builder.build())
    }

}