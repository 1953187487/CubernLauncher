
> 🌐 **官方网站 / 资源下载站：** [https://1953187487.github.io/CubernLauncher/](https://1953187487.github.io/CubernLauncher/)  
<div align="center">

# Cubern Launcher

**基于 Zalith Launcher 1 二改开发的 Minecraft: Java Edition Android 启动器**

[![Release](https://img.shields.io/github/v/release/1953187487/CubernLauncher?label=Release)](https://github.com/1953187487/CubernLauncher/releases/latest)
[![Downloads](https://img.shields.io/github/downloads/1953187487/CubernLauncher/total?label=Downloads)](https://github.com/1953187487/CubernLauncher/releases)
[![License](https://img.shields.io/badge/license-GPL--3.0-blue.svg)](LICENSE)
[![Android](https://img.shields.io/badge/Android-8.0%2B-3DDC84.svg)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF.svg)](https://kotlinlang.org/)

</div>

---

## 项目简介

Cubern Launcher 面向 Android 设备，让 **Minecraft: Java Edition** 在手机上顺畅运行。项目以 [Zalith Launcher 1](https://github.com/ZalithLauncher/ZalithLauncher) 为基础，完整保留其启动内核与版本兼容性，在此之上重构了界面层，并陆续加入陶瓦联机、AI 版本助手、Java 运行时按需安装、Vulkan 渲染支持等能力。

| 项目 | 信息 |
| --- | --- |
| 当前版本 | **1.0.6**（测试版 / Pre-release） |
| 当前作者 | **鹙鸟** |
| 原项目作者 | **Zalith Launcher 1 启动器作者** |
| 开源协议 | **GNU General Public License v3.0** |
| 包名 | `com.movtery.cubernlauncher` |
| 最低系统 | Android 8.0（API 26） |

> [!IMPORTANT]
> Cubern Launcher 是一个**社区二次开发项目**，与 Mojang、Microsoft、网易无隶属关系。Minecraft 为 Mojang Studios 与 Microsoft 的商标，本项目仅用于在 Android 设备上启动正版游戏。

---

## 核心特性

### MIUIX Compose 双栏主界面

- 完整还原 Zalith Launcher 1 的双栏结构：左侧菜单栏（6.5 份）+ 右侧启动面板（3 份）
- 顶栏右侧四个入口：**打开主目录**、**陶瓦联机**、**下载**、**设置**
- 右侧面板内置启动器动画（呼吸缩放 + 旋转 + 渐变光晕）
- 左侧菜单保留版本管理、模组管理、文件管理、执行 JAR
- 设置导航顺序：**视频 → 游戏 → 启动器 → 实验性 → 控制布局 → 关于本启动器**

### 陶瓦联机（Terracotta Multiplayer）

- 移植自 Zalith Launcher 2 的完整联机实现，覆盖 `arm64-v8a` / `armeabi-v7a` / `x86` / `x86_64`
- 原生桥接层 `TerracottaAndroidAPI` 与 `libterracotta.so`
- VPN 隧道服务 `TerracottaVPNService`，配套独立前台通知渠道
- 基于 EasyTier 的虚拟局域网：节点拉取、自定义节点、房主 / 房客双模式、房间码生成与校验
- 联机日志采集与明确的异常原因提示
- 顶栏按钮与游戏内启动器菜单均可进入

### AI 版本助手

- **离线版本体检**：扫描已安装版本，识别版本异常与冗余
- **运行时匹配**：根据版本号语义比较推导所需 Java 主版本，并核对本机运行时完整性
- **管理建议**：按 Java 版本分组统计，给出版本清理与运行时安装建议
- **自然语言问答**：用户自备 `USER_LLM_API_KEY` 后启用，离线体检功能无需任何 Key
- **设计参考**：功能思路参考了 [Amethyst-iOS](https://github.com/herbrine8403/Amethyst-iOS) 与 [MyRemasteredAI](https://github.com/MyRemasteredAI/MyRemasteredAI) 两个开源项目

### Java 运行时按需安装

- 启动游戏时依据版本号语义比较判断所需 Java 主版本，仅准备对应运行时
- 运行时架构校验与文件完整性检查
- 运行时管理界面：查看已安装运行时、设置默认运行时、导入外部运行时

### Vulkan 与渲染模式

- Vulkan 能力检测：Vulkan API、物理设备、Vulkan 1.2 能力、图形队列、表面扩展与所需原生库
- 四种渲染模式：跟随游戏、OpenGL、Vulkan、自动选择
- 记录当前选择、检测结果与失败原因，环境不满足时回退到兼容渲染后端

### 版本、模组与资源管理

- 正式版、快照、Forge / Fabric / NeoForge / Quilt 等自定义环境安装与启动
- 版本隔离与自定义游戏目录路径
- 模组、整合包、资源包、存档、光影包下载与管理
- 内置文件管理器、虚拟鼠标、虚拟按键与手柄映射
- 下载源镜像路径映射验证与修复；缺少 CurseForge API Key 时自动回退 Modrinth

### 其他

- 更新下载进度动画重写为平滑追赶式进度条，进度不回退
- 首次启动展示**用户须知协议**，明确二改来源、网络与账号说明、本地存储说明与免责声明
- 中英文双语界面，浅色 / 深色主题跟随系统

---

## 安装

前往 [Releases](https://github.com/1953187487/CubernLauncher/releases) 下载与设备架构匹配的安装包：

| 架构 | 适用设备 | 安装包 |
| --- | --- | --- |
| 通用 APK | Android 8.0+ | `CubernLauncher-1.0.6.apk`（统一包，约 275 MB） |

---

## 从源码构建

### 环境要求

- JDK 17（构建主工程）
- JDK 8（构建 `jre_lwjgl3glfw` 模块）
- Android SDK Platform 34、Build-Tools 34.0.0、NDK 25.2.9519653
- Gradle Wrapper 已随仓库提供

### 配置 SDK 路径

在 `local.properties` 中写入本机 Android SDK 路径：

```properties
sdk.dir=/path/to/android-sdk
```

### 构建正式版

```bash
./gradlew :CubernLauncher:assembleRelease
```

产物位于 `CubernLauncher/build/outputs/apk/release/`。

### 按 ABI 拆分打包

按 ABI 拆分可显著减小安装包体积（arm64 约 159 MB、armeabi-v7a 约 153 MB、x86 约 144 MB、x86_64 约 162 MB）：

```bash
./gradlew :CubernLauncher:assembleRelease -Darch=arm64
./gradlew :CubernLauncher:assembleRelease -Darch=arm
./gradlew :CubernLauncher:assembleRelease -Darch=x86
./gradlew :CubernLauncher:assembleRelease -Darch=x86_64
```

### 构建调试版

```bash
./gradlew :CubernLauncher:assembleDebug
```

### 签名

正式版使用 `CubernLauncher/cubern-key.jks`。签名口令可通过环境变量 `CUBERN_KEYSTORE_PASSWORD` 指定，未设置时回退到 `gradle.properties` 中的 `cubern_keystore_password`。

---

## 特别感谢

Cubern Launcher 建立在众多开发者的开源成果之上，向以下项目与开发者致以诚挚谢意。

### 承接与移植的开源项目

| 项目 | 贡献内容 |
| --- | --- |
| [Zalith Launcher 1](https://github.com/ZalithLauncher/ZalithLauncher) | 基础项目、启动内核与版本兼容性来源 |
| [Zalith Launcher 2](https://github.com/ZalithLauncher/ZalithLauncher2) | 陶瓦联机（Terracotta）模块移植来源 |
| [PojavLauncher](https://github.com/PojavLauncherTeam/PojavLauncher) | Android 端 Minecraft 启动核心与大量底层实现 |
| [FoldCraftLauncher](https://github.com/FCL-Team/FoldCraftLauncher) | Terracotta 实现参考与 EasyTier 集成方案 |
| [herbrine8403 / Amethyst-iOS](https://github.com/herbrine8403/Amethyst-iOS) | AI 辅助游戏版本管理的功能设计参考 |
| [MyRemasteredAI](https://github.com/MyRemasteredAI/MyRemasteredAI) | AI 助手能力与启动器集成方案参考 |
| [MIUIX](https://github.com/compose-miuix-ui/miuix) | 界面层使用的 MIUIX Compose UI 组件库 |
| [EasyTier](https://github.com/EasyTier/EasyTier) | 陶瓦联机底层虚拟局域网方案 |
| [HMCL](https://github.com/HMCL-dev/HMCL) | 部分源码参考 |
| [CommonMark](https://github.com/thephpleague/commonmark) | Markdown 文本渲染 |
| [AndroidViewAnimations](https://github.com/daimajia/AndroidViewAnimations) | 部分动画源码参考 |
| [TapTargetView](https://github.com/KeepSafe/TapTargetView) | 新手引导实现 |

### 开发者与社区

- **墨北MovTery** —— Zalith Launcher 系列项目主要作者
- **鹙鸟** —— Cubern Launcher 当前作者与维护者
- **Vera-Firefly** —— Pojav-Glow-Worm 相关修复贡献
- **柃木湫竹** —— 原项目功能与代码贡献
- **ShirosakiMio** —— 原项目功能与代码贡献
- **bangbang93** —— 长期赞助支持开发
- **龙腾猫跃** —— 模组分类数据整理
- **MC 百科** —— 版本与模组元数据来源
- 以及所有通过 Issue、Pull Request、翻译、测试与反馈参与项目的社区伙伴

> 若你在使用本项目的过程中发现遗漏的贡献者，欢迎提交 Issue 补充致谢名单。

---

## 许可与协议

Cubern Launcher 基于 Zalith Launcher 1 进行二次开发，遵循 **GNU General Public License v3.0** 开源协议，完整协议见 [LICENSE](LICENSE)。

依据 GPL v3 第七条：

1. 分发修改版本时需以合理方式修改名称或版本号，明确标示其为非官方修改版；
2. 需保留原项目展示的版权声明。

陶瓦联机模块的相关许可文本随应用一同分发，见 `CubernLauncher/src/main/res/raw/terracotta_license.txt`。

---

## 免责声明

- 本项目与 Mojang Studios、Microsoft、网易无任何隶属关系，也未获得其官方授权或背书。
- Minecraft 为 Mojang Studios 与 Microsoft 的商标，游戏内容版权归其所有；请通过正规渠道购买正版游戏。
- 用户需自行承担使用本启动器及所连接网络服务产生的一切后果。
- 若你发现侵权内容，请联系仓库维护者处理。

---

<div align="center">

**Cubern Launcher —— 让 Minecraft 在 Android 上跑得更顺手**

[仓库主页](https://github.com/1953187487/CubernLauncher) · [问题反馈](https://github.com/1953187487/CubernLauncher/issues) · [版本发布](https://github.com/1953187487/CubernLauncher/releases)

</div>
