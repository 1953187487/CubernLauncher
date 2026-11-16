package com.movtery.cubernlauncher.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.movtery.cubernlauncher.R
import com.movtery.cubernlauncher.databinding.ActivityLauncherCubernBinding
import com.movtery.cubernlauncher.event.single.LaunchGameEvent
import com.movtery.cubernlauncher.event.single.SwapToLoginEvent
import com.movtery.cubernlauncher.feature.accounts.AccountsManager
import com.movtery.cubernlauncher.feature.accounts.LocalAccountUtils
import com.movtery.cubernlauncher.feature.version.Version
import com.movtery.cubernlauncher.feature.version.VersionsManager
import com.movtery.cubernlauncher.InfoDistributor
import com.movtery.cubernlauncher.launch.LaunchGame.Companion.preLaunch
import com.movtery.cubernlauncher.plugins.renderer.RendererPluginManager
import com.movtery.cubernlauncher.setting.AllSettings
import com.movtery.cubernlauncher.ui.compose.CubernTheme
import com.movtery.cubernlauncher.ui.compose.LauncherHomeScreen
import com.movtery.cubernlauncher.ui.compose.SettingsScreen
import com.movtery.cubernlauncher.ui.compose.TaowaScreen
import com.movtery.cubernlauncher.utils.StoragePermissionsUtils
import com.movtery.cubernlauncher.utils.ZHTools
import com.movtery.cubernlauncher.ui.fragment.VersionSelectorFragment
import net.kdt.pojavlaunch.Tools
import net.kdt.pojavlaunch.contracts.OpenDocumentWithExtension
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Cubern Launcher 的 MIUIX 主界面
 *
 * - 右上角提供打开主目录与陶瓦联机入口
 * - 设置界面整合关于启动器与管理控制布局
 * - 原启动器的功能页面通过 Fragment 容器继续承载
 */
class LauncherActivity : FragmentActivity() {

    private lateinit var binding: ActivityLauncherCubernBinding

    private var pendingNotificationCallback: (() -> Unit)? = null

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            pendingNotificationCallback?.invoke()
        }
        pendingNotificationCallback = null
    }

    /**
     * 启动 JAR 安装器（模组安装包）
     */
    fun launchModInstaller() {
        modInstallerLauncher.launch(null)
    }

    private val modInstallerLauncher = registerForActivityResult(
        OpenDocumentWithExtension("jar")
    ) { uris: List<Uri>? ->
        if (uris != null && uris.isNotEmpty()) {
            Tools.launchModInstaller(this, uris[0])
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLauncherCubernBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.composeRoot.setContent {
            CubernTheme {
                var showSettings by remember { mutableStateOf(false) }
                var showTaowa by remember { mutableStateOf(false) }

                when {
                    showTaowa -> TaowaScreen(onBack = { showTaowa = false })
                    showSettings -> SettingsScreen(
                        onBack = { showSettings = false },
                        onOpenAbout = { openAboutPage() }
                    )
                    else -> LauncherHomeScreen(
                        activity = this,
                        onOpenSettings = { showSettings = true },
                        onOpenTaowa = { showTaowa = true },
                        onOpenVersionSelector = { openVersionSelector() }
                    )
                }
            }
        }

        supportFragmentManager.registerFragmentLifecycleCallbacks(
            object : FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentResumed(fm: FragmentManager, f: androidx.fragment.app.Fragment) {
                    updateComposeVisibility()
                }
            },
            false
        )

        EventBus.getDefault().register(this)
    }

    /**
     * 当功能页面处于前台时隐藏 MIUIX 首页
     */
    private fun updateComposeVisibility() {
        val hasFragment = supportFragmentManager.findFragmentById(R.id.container_fragment) != null
        binding.composeRoot.visibility = if (hasFragment) View.GONE else View.VISIBLE
        binding.containerFragment.visibility = if (hasFragment) View.VISIBLE else View.GONE
    }

    private fun openVersionSelector() {
        val fragment = supportFragmentManager.findFragmentById(R.id.container_fragment)
        if (fragment != null) {
            ZHTools.swapFragmentWithAnim(
                fragment,
                VersionSelectorFragment::class.java,
                VersionSelectorFragment.TAG,
                null
            )
        }
    }

    /**
     * 打开完整的关于启动器页面
     */
    private fun openAboutPage() {
        val fragment = supportFragmentManager.findFragmentById(R.id.container_fragment)
        if (fragment != null) {
            ZHTools.swapFragmentWithAnim(
                fragment,
                com.movtery.cubernlauncher.ui.fragment.AboutFragment::class.java,
                com.movtery.cubernlauncher.ui.fragment.AboutFragment.TAG,
                null
            )
        }
    }

    /**
     * 请求通知栏权限
     */
    fun askForNotificationPermission(callback: () -> Unit) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            callback()
            return
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            callback()
            return
        }
        pendingNotificationCallback = callback
        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun event(event: LaunchGameEvent) {
        val version = VersionsManager.getCurrentVersion()
        if (version == null) {
            Toast.makeText(this, R.string.error_no_version, Toast.LENGTH_LONG).show()
            return
        }

        if (AccountsManager.allAccounts.isEmpty()) {
            Toast.makeText(this, R.string.account_no_saved_accounts, Toast.LENGTH_LONG).show()
            EventBus.getDefault().post(SwapToLoginEvent())
            return
        }

        val rendererPlugin = RendererPluginManager.getConfigurablePluginOrNull(version.getRenderer())
        if (rendererPlugin != null) {
            StoragePermissionsUtils.checkPermissions(
                this,
                R.string.generic_warning,
                getString(
                    R.string.permissions_storage_for_renderer_config,
                    rendererPlugin.displayName,
                    InfoDistributor.APP_NAME
                ),
                object : StoragePermissionsUtils.PermissionGranted {
                    override fun granted() {
                        launchGame(version)
                    }

                    override fun cancelled() {
                        launchGame(version)
                    }
                }
            )
            return
        }

        launchGame(version)
    }

    private fun launchGame(version: Version) {
        LocalAccountUtils.checkUsageAllowed(object : LocalAccountUtils.CheckResultListener {
            override fun onUsageAllowed() {
                preLaunch(this@LauncherActivity, version)
            }

            override fun onUsageDenied() {
                if (!AllSettings.localAccountReminders.getValue()) {
                    preLaunch(this@LauncherActivity, version)
                } else {
                    LocalAccountUtils.openDialog(
                        this@LauncherActivity,
                        { checked ->
                            LocalAccountUtils.saveReminders(checked)
                            preLaunch(this@LauncherActivity, version)
                        },
                        getString(R.string.account_no_microsoft_account) +
                            getString(R.string.account_purchase_minecraft_account_tip),
                        R.string.account_continue_to_launch_the_game
                    )
                }
            }
        })
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStackImmediate()
            updateComposeVisibility()
            return
        }
        @Suppress("DEPRECATION")
        super.onBackPressed()
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }
}
