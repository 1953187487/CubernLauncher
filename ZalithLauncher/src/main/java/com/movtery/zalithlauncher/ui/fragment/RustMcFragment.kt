package com.movtery.zalithlauncher.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.movtery.zalithlauncher.ui.compose.screens.RustMcScreen

/** 1.0.9：Rust 版 MC（Horizon Android）下载分类页 */
class RustMcFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setContent { RustMcScreen(onBack = { requireActivity().onBackPressed() }) }
    }
}
