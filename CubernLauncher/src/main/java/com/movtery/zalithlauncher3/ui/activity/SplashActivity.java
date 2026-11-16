package com.movtery.CubernLauncher3.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.movtery.CubernLauncher3.R;

/**
 * 启动画面 Activity
 * 负责初始化检查和跳转到主界面
 */
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 1500; // 1.5秒启动画面

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 延迟跳转，显示启动画面
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, LauncherActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_DELAY);
    }
}
