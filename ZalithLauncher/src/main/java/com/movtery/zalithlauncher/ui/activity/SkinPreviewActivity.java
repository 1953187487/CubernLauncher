package com.movtery.zalithlauncher.ui.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.movtery.zalithlauncher.R;
import com.movtery.zalithlauncher.utils.path.PathManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * 真正的 3D 皮肤预览 Activity（1.0.7）。
 * 使用 skinview3d（Three.js）渲染 Minecraft 皮肤，支持 idle/walk/run/fly/wave/dance 动作与自动旋转。
 */
public class SkinPreviewActivity extends BaseActivity {
    private WebView webView;
    private String skinPath;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skin_preview);

        String name = getIntent().getStringExtra("skin_name");
        skinPath = getIntent().getStringExtra("skin_path");
        if (name == null || name.isEmpty()) name = "角色皮肤";
        ((android.widget.TextView) findViewById(R.id.skin_title)).setText(name);

        findViewById(R.id.skin_back).setOnClickListener(v -> finish());

        webView = findViewById(R.id.skin_webview);
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setDomStorageEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.setWebViewClient(new WebViewClient());
        webView.addJavascriptInterface(this, "Android");

        prepareSkinFile();
        webView.loadUrl("file:///android_asset/skin_preview.html");

        setupActionButtons();
    }

    /** 将皮肤文件复制到 WebView 可访问的 files 目录（file:// 同源限制） */
    private void prepareSkinFile() {
        File out = new File(getFilesDir(), "skin.png");
        try {
            InputStream in = null;
            if (skinPath != null && new File(skinPath).exists()) {
                in = new java.io.FileInputStream(skinPath);
            } else {
                String uuid = getIntent().getStringExtra("skin_uuid");
                if (uuid != null) {
                    File src = new File(PathManager.DIR_USER_SKIN, uuid + ".png");
                    if (src.exists()) in = new java.io.FileInputStream(src);
                }
            }
            if (in == null) in = getAssets().open("skin_default.png");
            FileOutputStream fos = new FileOutputStream(out);
            byte[] buf = new byte[8192]; int n;
            while ((n = in.read(buf)) > 0) fos.write(buf, 0, n);
            fos.close(); in.close();
        } catch (Exception e) { /* 使用默认皮肤 */ }
    }

    private void setupActionButtons() {
        findViewById(R.id.action_idle).setOnClickListener(v -> callJs("idle"));
        findViewById(R.id.action_walk).setOnClickListener(v -> callJs("walk"));
        findViewById(R.id.action_run).setOnClickListener(v -> callJs("run"));
        findViewById(R.id.action_wave).setOnClickListener(v -> callJs("wave"));
        findViewById(R.id.action_dance).setOnClickListener(v -> callJs("dance"));
        findViewById(R.id.action_rotate).setOnClickListener(v -> evalJs("setAutoRotate(true)"));
    }

    @JavascriptInterface
    public String getSkinUrl() {
        return "file://" + getFilesDir().getAbsolutePath() + "/skin.png";
    }

    private void callJs(String anim) { evalJs("setAnimation('" + anim + "')"); }
    private void evalJs(String js) {
        webView.evaluateJavascript("javascript:" + js, null);
    }
}
