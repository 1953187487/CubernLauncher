package com.movtery.cubernlauncher.ui.dialog

import android.content.Context
import com.movtery.cubernlauncher.R
import com.movtery.cubernlauncher.ui.fragment.about.UserNoticeFragment

/**
 * 用户须知协议弹窗
 *
 * 使用可选择文本展示完整协议内容，用户必须明确点击同意后才会继续。
 */
class UserNoticeDialog private constructor(
    context: Context,
    private val onAccepted: () -> Unit
) {

    class Builder(private val context: Context) {
        private var onAccepted: (() -> Unit)? = null

        fun setOnAccepted(listener: () -> Unit): Builder {
            this.onAccepted = listener
            return this
        }

        fun show() {
            val message = UserNoticeFragment.buildNoticeText(context)
            TipDialog.Builder(context)
                .setTitle(R.string.user_notice_title)
                .setMessage(message)
                .setSelectable(true)
                .setCancelable(false)
                .setShowCancel(false)
                .setConfirm(R.string.user_notice_agree)
                .setConfirmButtonCountdown(3000)
                .setConfirmClickListener {
                    onAccepted?.invoke()
                }
                .showDialog()
        }
    }
}
