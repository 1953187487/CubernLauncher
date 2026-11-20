package com.movtery.zalithlauncher.feature.notice

import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object UserAgreementStore {
    private const val PREF = "cubern_user_agreement"
    private const val KEY_ACCEPTED = "accepted"
    private const val KEY_VERSION = "version"
    private const val KEY_TIME = "time"
    const val CURRENT_VERSION = "1.0.3"
    const val APP_NAME = "Cubern Launcher"

    fun isAccepted(context: Context): Boolean {
        val sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        return sp.getBoolean(KEY_ACCEPTED, false) && sp.getString(KEY_VERSION, "") == CURRENT_VERSION
    }

    fun accept(context: Context) {
        val sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
        val now = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        sp.edit()
            .putBoolean(KEY_ACCEPTED, true)
            .putString(KEY_VERSION, CURRENT_VERSION)
            .putString(KEY_TIME, now)
            .apply()
    }

    fun acceptedTime(context: Context): String? =
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).getString(KEY_TIME, null)
}
