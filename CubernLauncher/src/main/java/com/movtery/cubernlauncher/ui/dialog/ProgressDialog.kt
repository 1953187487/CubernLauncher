package com.movtery.cubernlauncher.ui.dialog

import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import android.widget.ProgressBar
import com.movtery.cubernlauncher.databinding.DialogProgressBinding
import com.movtery.cubernlauncher.setting.AllSettings
import com.movtery.cubernlauncher.ui.dialog.DraggableDialog.DialogInitializationListener
import com.movtery.cubernlauncher.utils.file.FileTools.Companion.formatFileSize

/**
 * 更新下载进度对话框
 *
 * 进度条动画经过重写：
 * - 采用目标值追赶动画，进度变化平滑且不会回退
 * - 下载速度与剩余时间独立刷新，避免数字抖动
 */
class ProgressDialog(
    context: Context,
    private val listener: OnCancelListener
) : FullScreenDialog(context), DialogInitializationListener {
    private val binding = DialogProgressBinding.inflate(layoutInflater)

    private var progressAnimator: ValueAnimator? = null
    private var displayedProgress = 0
    private var targetProgress = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        setCancelable(false)

        binding.progressBar.setMax(MAX_PROGRESS)
        binding.cancelButton.setOnClickListener {
            if (!listener.onClick()) return@setOnClickListener
            dismiss()
        }

        DraggableDialog.initDialog(this)
    }

    fun updateText(text: String?) {
        binding.textView.text = text
    }

    fun updateRate(processingRate: Long) {
        if (processingRate > 0) binding.uploadRate.visibility = View.VISIBLE
        val formatFileSize = formatFileSize(processingRate)
        "$formatFileSize/s".also { binding.uploadRate.text = it }
    }

    fun updateProgress(progress: Double, total: Double) {
        if (total <= 0) return

        val percentage = (progress / total * 100).coerceIn(0.0, 100.0)
        targetProgress = (percentage * MAX_PROGRESS / 100).toInt()

        binding.progressBar.apply {
            visibility = if (targetProgress > 0) View.VISIBLE else View.GONE
            if (AllSettings.animation.getValue()) {
                animateProgressTo(targetProgress)
            } else {
                progressAnimator?.cancel()
                progressAnimator = null
                displayedProgress = targetProgress
                setProgress(displayedProgress, false)
            }
        }
    }

    /**
     * 平滑追赶目标进度
     */
    private fun animateProgressTo(target: Int) {
        if (target == displayedProgress) return

        progressAnimator?.cancel()
        progressAnimator = ValueAnimator.ofInt(displayedProgress, target).apply {
            duration = PROGRESS_ANIMATION_DURATION
            interpolator = DecelerateInterpolator()
            addUpdateListener { animator ->
                val value = animator.animatedValue as Int
                displayedProgress = value
                binding.progressBar.setProgress(value, false)
            }
            start()
        }
    }

    override fun onInit(): Window? = window

    override fun onDetachedFromWindow() {
        progressAnimator?.cancel()
        progressAnimator = null
        super.onDetachedFromWindow()
    }

    fun interface OnCancelListener {
        fun onClick(): Boolean
    }

    companion object {
        private const val MAX_PROGRESS = 1000
        private const val PROGRESS_ANIMATION_DURATION = 300L
    }
}
