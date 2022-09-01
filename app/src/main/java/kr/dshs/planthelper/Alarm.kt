package kr.dshs.planthelper

import android.app.AlarmManager
import android.app.PendingIntent
import java.util.*

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