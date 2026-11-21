package com.movtery.zalithlauncher.ui.fragment.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.movtery.anim.AnimPlayer
import com.movtery.anim.animations.Animations
import com.movtery.zalithlauncher.R
import com.movtery.zalithlauncher.databinding.SettingsFragmentExperimentalBinding
import com.movtery.zalithlauncher.setting.AllSettings
import com.movtery.zalithlauncher.ui.fragment.AboutFragment
import com.movtery.zalithlauncher.ui.fragment.ControlButtonFragment
import com.movtery.zalithlauncher.utils.ZHTools
import com.movtery.zalithlauncher.ui.fragment.settings.wrapper.BaseSettingsWrapper
import com.movtery.zalithlauncher.ui.fragment.settings.wrapper.SeekBarSettingsWrapper
import com.movtery.zalithlauncher.ui.fragment.settings.wrapper.SwitchSettingsWrapper

class ExperimentalSettingsFragment :
    AbstractSettingsFragment(R.layout.settings_fragment_experimental, SettingCategory.EXPERIMENTAL) {
    private lateinit var binding: SettingsFragmentExperimentalBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SettingsFragmentExperimentalBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val context = requireContext()

        SwitchSettingsWrapper(
            context,
            AllSettings.dumpShaders,
            binding.dumpShadersLayout,
            binding.dumpShaders
        )

        SwitchSettingsWrapper(
            context,
            AllSettings.bigCoreAffinity,
            binding.bigCoreAffinityLayout,
            binding.bigCoreAffinity
        )

        SeekBarSettingsWrapper(
            context,
            AllSettings.tcVibrateDuration,
            binding.tcVibrateDurationLayout,
            binding.tcVibrateDurationTitle,
            binding.tcVibrateDurationSummary,
            binding.tcVibrateDurationValue,
            binding.tcVibrateDuration,
            "ms"
        )

        // 1.0.4：关于启动器与控制布局入口移动到实验性设置下
        BaseSettingsWrapper(context, binding.aboutLauncherLayout) {
            ZHTools.swapFragmentWithAnim(this, AboutFragment::class.java, AboutFragment.TAG, null)
        }
        BaseSettingsWrapper(context, binding.controlLayoutLayout) {
            ZHTools.swapFragmentWithAnim(this, ControlButtonFragment::class.java, ControlButtonFragment.TAG, null)
        }
    }

    override fun slideIn(animPlayer: AnimPlayer) {
        animPlayer.apply(AnimPlayer.Entry(binding.root, Animations.BounceInDown))
    }
}