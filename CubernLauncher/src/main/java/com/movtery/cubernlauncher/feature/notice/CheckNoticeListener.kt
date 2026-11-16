package com.movtery.cubernlauncher.feature.notice

fun interface CheckNoticeListener {
    fun onSuccessful(noticeInfo: NoticeInfo?)
}