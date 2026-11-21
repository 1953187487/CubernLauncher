package com.movtery.zalithlauncher.ui.fragment.about

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.movtery.zalithlauncher.ui.compose.screens.AboutLauncherScreen

/**
 * 关于启动器信息页。
 *
 * 1.0.4：删除旧贡献者 RecyclerView 列表，换成 Jetpack Compose 拟物沉浸风页面。
 */
class AboutInfoPageFragment() : Fragment() {
    private var parentPager2: ViewPager2? = null

    constructor(parentPager: ViewPager2) : this() {
        this.parentPager2 = parentPager
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent { AboutLauncherScreen(onBack = { parentPager2?.let { } }) }
        }
    }
}
