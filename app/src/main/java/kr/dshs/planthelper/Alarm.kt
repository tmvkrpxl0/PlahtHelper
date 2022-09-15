package kr.dshs.planthelper

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.util.*
import kotlin.random.Random

/**
 * 24시간 포맷 사용함
 */
fun setAlarm(alarmManager: AlarmManager, hour: Int, minute: Int, day: Int, pendingIntent: PendingIntent) {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = System.currentTimeMillis()

        set(Calendar.DAY_OF_WEEK, day)
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    alarmManager.setInexactRepeating(AlarmManager.RTC, calendar.timeInMillis, AlarmManager.INTERVAL_DAY, pendingIntent)
}

const val alarmChannel = "planthelper_by_tmvkrpxl0"

class AlarmBroadcast: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val builder = NotificationCompat.Builder(context, alarmChannel)
            .setContentTitle("Alarm test")
            .setContentText("Contents")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val manager = NotificationManagerCompat.from(context)
        manager.notify(Random.nextInt(), builder.build())
    }

}