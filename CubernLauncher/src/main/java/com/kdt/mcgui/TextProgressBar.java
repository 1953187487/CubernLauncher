package com.kdt.mcgui;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.core.content.res.ResourcesCompat;

import com.movtery.cubernlauncher.R;

/**
 * 带文本的下载进度条
 *
 * 动画经过重写：
 * - 进度变化通过 ValueAnimator 平滑追赶目标值，不会回退或跳变
 * - 进度条使用渐变填充，视觉层次更清晰
 * - 文本随进度平滑移动
 */
public class TextProgressBar extends ProgressBar {

    private static final long PROGRESS_ANIMATION_DURATION = 280L;

    private int mTextPadding = 0;
    private Paint mTextPaint;
    private Paint mProgressPaint;
    private String mText = "";

    private int mTargetProgress = 0;
    private int mDisplayedProgress = 0;
    @Nullable
    private ValueAnimator mProgressAnimator;

    public TextProgressBar(Context context) {
        super(context, null, android.R.attr.progressBarStyleHorizontal);
        init();
    }

    public TextProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs, android.R.attr.progressBarStyleHorizontal);
        init();
    }

    public TextProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, android.R.attr.progressBarStyleHorizontal);
        init();
    }

    public TextProgressBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, android.R.attr.progressBarStyleHorizontal, defStyleRes);
        init();
    }

    private void init() {
        setProgressDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.view_text_progressbar, null));
        setProgress(0);

        mTextPaint = new Paint();
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setFlags(Paint.FAKE_BOLD_TEXT_FLAG);
        mTextPaint.setAntiAlias(true);

        mProgressPaint = new Paint();
        mProgressPaint.setAntiAlias(true);
    }

    /**
     * 设置目标进度，动画平滑追赶
     */
    public void setProgressAnimated(int progress, boolean animated) {
        mTargetProgress = progress;
        if (!animated) {
            cancelAnimation();
            mDisplayedProgress = progress;
            setProgress(progress, false);
            invalidate();
            return;
        }
        if (progress == mDisplayedProgress) return;

        cancelAnimation();
        mProgressAnimator = ValueAnimator.ofInt(mDisplayedProgress, progress);
        mProgressAnimator.setDuration(PROGRESS_ANIMATION_DURATION);
        mProgressAnimator.setInterpolator(new DecelerateInterpolator());
        mProgressAnimator.addUpdateListener(animator -> {
            mDisplayedProgress = (Integer) animator.getAnimatedValue();
            setProgress(mDisplayedProgress, false);
            invalidate();
        });
        mProgressAnimator.start();
    }

    private void cancelAnimation() {
        if (mProgressAnimator != null) {
            mProgressAnimator.cancel();
            mProgressAnimator = null;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        cancelAnimation();
        super.onDetachedFromWindow();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        if (width <= 0 || height <= 0) return;

        // 渐变进度条
        float ratio = getMax() > 0 ? (float) getProgress() / getMax() : 0f;
        float progressWidth = width * ratio;
        if (progressWidth > 0) {
            mProgressPaint.setShader(new LinearGradient(
                    0, 0, progressWidth, 0,
                    new int[]{0xFF7C4DFF, 0xFF4FC3F7},
                    null, Shader.TileMode.CLAMP));
            canvas.drawRoundRect(0, 0, progressWidth, height, height / 2f, height / 2f, mProgressPaint);
        }

        mTextPaint.setTextSize((float) ((height - getPaddingBottom() - getPaddingTop()) * 0.55));
        mTextPaint.setColor(getResources().getColor(com.movtery.cubernlauncher.R.color.primary_text, getContext().getTheme()));
        int xPos = (int) Math.max(Math.min(progressWidth + mTextPadding,
                width - mTextPaint.measureText(mText) - mTextPadding), mTextPadding);
        int yPos = (int) ((height / 2) - ((mTextPaint.descent() + mTextPaint.ascent()) / 2));

        canvas.drawText(mText, xPos, yPos, mTextPaint);
    }

    public final void setText(@StringRes int resid) {
        setText(getContext().getResources().getText(resid).toString());
    }

    public final void setText(String text) {
        mText = text;
        invalidate();
    }

    public final void setTextPadding(int padding) {
        mTextPadding = padding;
    }
}
