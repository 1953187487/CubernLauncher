package com.movtery.cubernlauncher.ui.fragment.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.movtery.anim.AnimPlayer
import com.movtery.anim.animations.Animations
import com.movtery.cubernlauncher.R
import com.movtery.cubernlauncher.databinding.FragmentAboutBinding
import com.movtery.cubernlauncher.ui.fragment.about.AboutInfoPageFragment
import com.movtery.cubernlauncher.ui.fragment.about.AboutSponsorPageFragment
import com.movtery.cubernlauncher.ui.fragment.FragmentWithAnim
import com.movtery.cubernlauncher.utils.ZHTools
import com.movtery.cubernlauncher.utils.path.UrlManager
import com.movtery.cubernlauncher.ui.dialog.TipDialog
import com.movtery.cubernlauncher.utils.stringutils.StringUtils
import androidx.viewpager2.widget.ViewPager2

/**
 * 「关于本启动器」设置分类
 *
 * 复用原关于页面的信息页与赞助页，并补充引用的开源项目致谢。
 */
class AboutSettingsFragment : FragmentWithAnim(R.layout.fragment_about) {
    companion object {
        const val TAG: String = "AboutSettingsFragment"
    }

    private lateinit var binding: FragmentAboutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initViewPager()

        binding.apply {
            appInfo.text = StringUtils.insertNewline(
                StringUtils.insertSpace(getString(R.string.about_version_name), ZHTools.getVersionName()),
                StringUtils.insertSpace(getString(R.string.about_version_code), ZHTools.getVersionCode()),
                StringUtils.insertSpace(getString(R.string.about_last_update_time), ZHTools.getLastUpdateTime(requireContext())),
                StringUtils.insertSpace(getString(R.string.about_version_status), ZHTools.getVersionStatus(requireContext()))
            )
            appInfo.setOnClickListener {
                StringUtils.copyText("text", appInfo.text.toString(), requireContext())
            }
            returnButton.setOnClickListener { ZHTools.onBackPressed(requireActivity()) }
            supportDevelopment.setOnClickListener {
                TipDialog.Builder(requireActivity())
                    .setTitle(R.string.request_sponsorship_title)
                    .setMessage(R.string.request_sponsorship_message)
                    .setConfirm(R.string.about_button_support_development)
                    .setConfirmClickListener { ZHTools.openLink(requireActivity(), UrlManager.URL_SUPPORT) }
                    .showDialog()
            }
        }
    }

    private fun initViewPager() {
        binding.infoViewPager.apply {
            adapter = ViewPagerAdapter(requireActivity(), this)
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            offscreenPageLimit = 1
        }
    }

    override fun slideIn(animPlayer: AnimPlayer) {
        animPlayer.apply(AnimPlayer.Entry(binding.infoViewPager, Animations.BounceInDown))
            .apply(AnimPlayer.Entry(binding.operateLayout, Animations.BounceInLeft))
    }

    override fun slideOut(animPlayer: AnimPlayer) {
        animPlayer.apply(AnimPlayer.Entry(binding.infoViewPager, Animations.FadeOutUp))
            .apply(AnimPlayer.Entry(binding.operateLayout, Animations.FadeOutRight))
    }

    private class ViewPagerAdapter(
        fragmentActivity: androidx.fragment.app.FragmentActivity,
        private val viewPager: ViewPager2
    ) : androidx.viewpager2.adapter.FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> AboutInfoPageFragment(viewPager)
                else -> AboutSponsorPageFragment()
            }
        }
    }
}
