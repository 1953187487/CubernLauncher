package com.movtery.zalithlauncher3;

import android.app.Application;
import android.content.Context;

/**
 * Zalith Launcher 3 主 Application 类
 * 负责初始化全局配置、运行时环境检测
 */
public class ZalithApplication extends Application {

    private static Context applicationContext;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationContext = getApplicationContext();
        
        // 初始化全局配置
        initGlobalConfig();
        
        // 检测基本运行环境
        checkRuntimeEnvironment();
    }

    private void initGlobalConfig() {
        // 读取配置，设置启动器名称、作者等信息
        // 此处可加载 config.yaml 或 preferences
    }

    private void checkRuntimeEnvironment() {
        // 检查 Android 版本、ABI 支持等
        // 为后续 Java 运行时管理做准备
    }

    public static Context getAppContext() {
        return applicationContext;
    }
}
