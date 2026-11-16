package com.movtery.cubernlauncher.ui.fragment.about

import android.content.Context
import com.movtery.cubernlauncher.R
import com.movtery.cubernlauncher.InfoDistributor

/**
 * 用户须知协议文本
 *
 * 说明启动器的来源、性质、收集的信息、用户权利与免责声明。
 */
object UserNoticeFragment {

    /**
     * 构建完整的用户须知文本
     */
    fun buildNoticeText(context: Context): String {
        val appName = InfoDistributor.APP_NAME
        return buildString {
            append(context.getString(R.string.user_notice_intro, appName))
            append("\n\n")

            append(context.getString(R.string.user_notice_origin_title))
            append("\n")
            append(context.getString(R.string.user_notice_origin_content, appName))
            append("\n\n")

            append(context.getString(R.string.user_notice_non_official_title))
            append("\n")
            append(context.getString(R.string.user_notice_non_official_content, appName))
            append("\n\n")

            append(context.getString(R.string.user_notice_network_title))
            append("\n")
            append(context.getString(R.string.user_notice_network_content))
            append("\n\n")

            append(context.getString(R.string.user_notice_storage_title))
            append("\n")
            append(context.getString(R.string.user_notice_storage_content))
            append("\n\n")

            append(context.getString(R.string.user_notice_license_title))
            append("\n")
            append(context.getString(R.string.user_notice_license_content, appName))
            append("\n\n")

            append(context.getString(R.string.user_notice_disclaimer_title))
            append("\n")
            append(context.getString(R.string.user_notice_disclaimer_content))
        }
    }
}
