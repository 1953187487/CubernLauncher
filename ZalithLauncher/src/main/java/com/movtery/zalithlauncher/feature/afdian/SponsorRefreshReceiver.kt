package com.movtery.zalithlauncher.feature.afdian;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.launch;

/** 每日 0 点刷新赞助者列表的接收器（1.0.7） */
class SponsorRefreshReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_BOOT_COMPLETED == intent.action || ACTION_REFRESH == intent.action) {
            scheduleDaily(context)
            refreshNow(context)
        }
    }

    companion object {
        const val ACTION_REFRESH = "com.movtery.zalithlauncher.REFRESH_SPONSORS"

        /** 刷新一次并持久化 */
        fun refreshNow(context: Context) {
            CoroutineScope(Dispatchers.IO).launch {
                AfdianApi.querySponsors().onSuccess { AfdianApi.saveSponsors(context, it) }
            }
        }

        /** 设定每天 0 点（24:00）闹钟 */
        fun scheduleDaily(context: Context) {
            val alarm = context.getSystemService(Context.ALARM_SERVICE) as android.app.AlarmManager
            val pi = android.app.PendingIntent.getBroadcast(
                context, 1001,
                Intent(context, SponsorRefreshReceiver::class.java).setAction(ACTION_REFRESH),
                android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
            )
            val cal = java.util.Calendar.getInstance().apply {
                set(java.util.Calendar.HOUR_OF_DAY, 0)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                add(java.util.Calendar.DAY_OF_YEAR, 1)
            }
            try { alarm.setExactAndAllowWhileIdle(android.app.AlarmManager.RTC_WAKEUP, cal.timeInMillis, pi) }
            catch (e: Exception) { alarm.set(android.app.AlarmManager.RTC_WAKEUP, cal.timeInMillis, pi) }
        }
    }
}
