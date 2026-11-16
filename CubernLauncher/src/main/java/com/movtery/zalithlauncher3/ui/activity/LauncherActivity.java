package com.movtery.zalithlauncher3.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.movtery.zalithlauncher3.R;

/**
 * Launcher 主界面 Activity
 * 展示启动器主功能入口
 * 右上角添加淘瓦联机功能按钮
 */
public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        // 初始化 UI 组件
        initUI();
    }

    private void initUI() {
        // 右上角淘瓦联机按钮
        Button taowaMultiplayerBtn = findViewById(R.id.btn_taowa_multiplayer);
        taowaMultiplayerBtn.setOnClickListener(v -> {
            // 打开淘瓦联机功能
            openTaowaMultiplayer();
        });

        // 设置按钮（合并了关于启动器和管理控制）
        Button settingsBtn = findViewById(R.id.btn_settings);
        settingsBtn.setOnClickListener(v -> {
            // 打开设置页面
            openSettings();
        });
    }

    private void openTaowaMultiplayer() {
        // TODO: 实现淘瓦联机功能
        // 参考 Zalith Launcher 2 的实现
    }

    private void openSettings() {
        // TODO: 打开设置页面（包含关于启动器和管理控制）
    }
}
