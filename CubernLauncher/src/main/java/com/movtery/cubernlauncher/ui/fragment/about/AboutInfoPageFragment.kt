package com.movtery.cubernlauncher.ui.fragment.about

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.movtery.cubernlauncher.InfoCenter
import com.movtery.cubernlauncher.InfoDistributor
import com.movtery.cubernlauncher.R
import com.movtery.cubernlauncher.databinding.FragmentAboutInfoPageBinding
import com.movtery.cubernlauncher.feature.afdian.AfdianApi
import com.movtery.cubernlauncher.feature.update.UpdateUtils
import com.movtery.cubernlauncher.ui.dialog.TipDialog
import com.movtery.cubernlauncher.ui.subassembly.about.AboutItemBean
import com.movtery.cubernlauncher.ui.subassembly.about.AboutItemBean.AboutItemButtonBean
import com.movtery.cubernlauncher.ui.subassembly.about.AboutRecyclerAdapter
import com.movtery.cubernlauncher.utils.ZHTools
import com.movtery.cubernlauncher.utils.path.UrlManager

class AboutInfoPageFragment() : Fragment(R.layout.fragment_about_info_page) {
    private lateinit var binding: FragmentAboutInfoPageBinding
    private val mAboutData: MutableList<AboutItemBean> = ArrayList()
    private var parentPager2: ViewPager2? = null

    constructor(parentPager: ViewPager2): this() {
        this.parentPager2 = parentPager
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAboutInfoPageBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        loadAboutData(requireContext().resources)

        val context = requireActivity()

        binding.apply {
            dec1.text = InfoCenter.replaceName(context, R.string.about_dec1)
            dec2.text = InfoCenter.replaceName(context, R.string.about_dec2)
            dec3.text = InfoCenter.replaceName(context, R.string.about_dec3)

            githubButton.setOnClickListener { ZHTools.openLink(requireActivity(), UrlManager.URL_HOME) }
            licenseButton.setOnClickListener { ZHTools.openLink(requireActivity(), "https://www.gnu.org/licenses/gpl-3.0.html") }
            checkUpdateButton.setOnClickListener {
                UpdateUtils.checkDownloadedPackage(requireContext(), force = true, ignore = false)
            }

            val aboutAdapter = AboutRecyclerAdapter(this@AboutInfoPageFragment.mAboutData)
            aboutRecycler.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = aboutAdapter
            }
            sponsor.setOnClickListener { _ ->
                parentPager2?.currentItem = 1
            }

            if (ZHTools.isChinese(requireActivity())) {
                qqGroupButton.visibility = View.VISIBLE
                qqGroupButton.setOnClickListener {
                    TipDialog.Builder(context)
                        .setTitle("QQ")
                        .setMessage("欢迎加入 ${InfoDistributor.APP_NAME} 官方 QQ 交流群（群号：${InfoCenter.QQ_GROUP}）！由于群人数有限，加入群聊前需要赞助 5元 或以上金额，请点击右侧“赞助开发”按钮访问爱发电。")
                        .setSelectable(true)
                        .setConfirm(R.string.generic_confirm)
                        .setShowCancel(false)
                        .showDialog()
                }
            } else {
                qqGroupButton.visibility = View.GONE
            }

            discordButton.setOnClickListener { ZHTools.openLink(requireActivity(), "https://discord.gg/yDDkTHp4cJ") }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun loadAboutData(resources: Resources) {
        mAboutData.clear()

        // 1.0.5：关于页重写为 Cubern Launcher 自主内容（开发基础 / 作者 / 贡献者鸣谢 / 检查更新）
        mAboutData.add(AboutItemBean(
            resources.getDrawable(R.drawable.ic_pojav_full, requireContext().theme),
            "Cubern Launcher",
            "基于 Zalith Launcher 1 深度二改的 Minecraft Java 版 Android 启动器。启动内核：PojavLauncher；界面：Jetpack Compose + Material 3；风格：拟物沉浸风；协议：GPL-3.0。",
            null))
        mAboutData.add(AboutItemBean(
            resources.getDrawable(R.drawable.image_about_movtery, requireContext().theme),
            "鹙鸟（B站同名）",
            "Cubern Launcher 当前作者与维护者。开源项目，与 Mojang、Microsoft、网易无关。",
            AboutItemButtonBean(requireActivity(), "在爱发电支持作者", AfdianApi.FALLBACK_URL)))
        mAboutData.add(AboutItemBean(
            resources.getDrawable(R.drawable.ic_pojav_full, requireContext().theme),
            "Zalith Launcher 1 / PojavLauncher",
            "原作启动器与开源 Android Minecraft 启动内核，奠定本项目底层架构。",
            AboutItemButtonBean(requireActivity(), "Github", "https://github.com/ZalithLauncher")))
        mAboutData.add(AboutItemBean(
            resources.getDrawable(R.drawable.ic_pojav_full, requireContext().theme),
            "Terracotta / EasyTier",
            "为 Minecraft Java 版提供 P2P 联机方案。",
            AboutItemButtonBean(requireActivity(), "Github", "https://github.com/EasyTier/EasyTier")))
        mAboutData.add(AboutItemBean(
            resources.getDrawable(R.drawable.image_about_mcmod, requireContext().theme),
            "MC 百科（mcmod.cn）",
            "提供 Mod 与整合包中文翻译。",
            AboutItemButtonBean(requireActivity(), getString(R.string.about_access_link), UrlManager.URL_MCMOD)))
        mAboutData.add(AboutItemBean(
            resources.getDrawable(R.drawable.ic_pojav_full, requireContext().theme),
            "检查更新",
            "当前版本 v" + InfoDistributor.VERSION_NAME + "。点击检查是否有新版本。",
            AboutItemButtonBean(requireActivity(), "检查更新", "https://github.com/1953187487/CubernLauncher/releases")))
    }
}

