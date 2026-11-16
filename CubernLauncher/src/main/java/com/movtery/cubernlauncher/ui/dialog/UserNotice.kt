package com.movtery.cubernlauncher.ui.dialog

import android.content.Context
import android.view.LayoutInflater
import android.widget.TextView
import com.movtery.cubernlauncher.R
import com.movtery.cubernlauncher.setting.unit.BooleanSettingUnit
import com.movtery.cubernlauncher.ui.fragment.about.UserNoticeFragment

/**
 * 用户须知协议
 *
 * 首次启动时在解压运行时组件之前展示，用户同意后才会继续安装。
 */
object UserNotice {

    private val accepted = BooleanSettingUnit("user_notice_accepted", false)

    /**
     * 是否已经同意过用户须知
     */
    fun isAccepted(): Boolean = accepted.getValue()

    /**
     * 记录用户已同意
     */
    fun markAccepted() {
        accepted.put(true).save()
    }

    /**
     * 重置同意状态（用于恢复默认设置）
     */
    fun reset() {
        accepted.reset()
    }

    /**
     * 展示用户须知弹窗
     *
     * @param onAccepted 用户点击同意后的回调
     */
    fun show(context: Context, onAccepted: () -> Unit) {
        if (isAccepted()) {
            onAccepted()
            return
        }

        UserNoticeDialog.Builder(context)
            .setOnAccepted {
                markAccepted()
                onAccepted()
            }
            .show()
    }
}
