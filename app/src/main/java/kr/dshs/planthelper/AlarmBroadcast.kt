package kr.dshs.planthelper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/**
 * 24시간 포맷 사용함
 */
const val channelId = "planthelper_by_tmvkrpxl0"

class AlarmBroadcast: BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val index = intent.getIntExtra(profileIndexKey, -1)
        assert(index != -1) { "알람에 아이디가 없습니다!" }

        val plantProfile = plantProfiles[index]
        val builder = NotificationCompat.Builder(context, channelId)
            .setContentTitle("물 줄 시간입니다!")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentText("${plantProfile.getAnyName()} 에 물 줄 시간이 되었습니다!}")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val manager = NotificationManagerCompat.from(context)
        manager.notify(plantProfile.id, builder.build())
    }
}