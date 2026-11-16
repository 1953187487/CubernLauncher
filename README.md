# Cubern Launcher

Cubern Launcher 是一款基于 **Zalith Launcher1 进行二改开发**的 Minecraft Java 版 Android 启动器，采用 **GNU General Public License v3.0** 开源协议。

当前版本：**1.0.1**
当前启动器作者：**万古沧海**
原作者：**Zalith Launcher1 启动器作者**

## 这是什么

Cubern Launcher 面向 Android 设备，让 Minecraft: Java Edition 在手机上运行。它以 Zalith Launcher1 为基础，保留原项目的启动内核与兼容性，同时重构了界面层并引入了多项新特性。

## 当前特性

### 陶瓦联机（Terracotta Multiplayer）

从 Zalith Launcher 2 移植的完整联机实现：

- 原生桥接层 `TerracottaAndroidAPI` 与 `libterracotta.so`，覆盖 arm64-v8a / armeabi-v7a / x86 / x86_64
- VPN 隧道服务 `TerracottaVPNService`，配套独立前台通知渠道
- EasyTier 节点拉取（`https://terracotta.glavo.site/nodes`），支持自定义节点
- 房主 / 房客双模式、房间码生成与校验、完整状态机
- 联机日志采集与明确的异常原因提示
- 入口位于主界面右上角

### MIUIX Compose 界面重构

- 主界面采用 MIUIX Compose 框架重写，替代原 View 布局
- 布局与原版保持一致：当前版本卡片、启动游戏、版本选择、执行 JAR、设置入口
- 右上角提供「打开主目录」与「陶瓦联机」入口
- 设置界面整合了原主界面的「关于启动器」与「管理控制」，控制布局位于倒数第二项、关于启动器位于最后一项

### 26.2+ 运行环境与工具链

- 版本号语义比较，杜绝通过伪造或跳过版本号绕过限制
- Java 运行时自动识别、下载、校验与注入，校验架构与完整性
- 版本 JSON 解析、启动参数生成、依赖库下载与 class path 构建流程重构
- 依赖库按 `groupId:artifactId:version:classifier:abi` 去重
- LWJGL / OpenGL / Vulkan 原生库映射补全
- Android 系统原生库搜索路径（受 ABI、路径优先级与必要性约束）

### Vulkan 与渲染模式

- Vulkan 支持检测：核验 Vulkan API、物理设备、Vulkan 1.2 能力、图形队列、表面扩展与所需原生库
- 四种渲染模式：跟随游戏、OpenGL、Vulkan、自动选择
- 记录当前选择、检测结果与失败原因
- 环境不满足时给出明确原因并安全回退到兼容渲染后端

### 其他

- 更新下载进度动画重写为平滑追赶式进度条
- 下载平台自动适配：无 CurseForge API Key 时隐藏该平台并回退 Modrinth
- 下载源镜像路径映射验证与修复
- 中英文双语界面

## 兼容性

保留 26.2 之前的正式版、快照、Forge / Fabric / NeoForge / Quilt 等自定义环境的启动能力。

## 环境要求

- Android 8.0 (API 26) 及以上
- JDK 17 用于构建
- JDK 8 用于构建 `jre_lwjgl3glfw` 模块
- Android SDK Platform 34、Build-Tools 34.0.0、NDK 25.2.9519653

## 构建

配置 `local.properties` 指向本机 Android SDK：

```properties
sdk.dir=/path/to/android-sdk
```

构建正式版：

```bash
./gradlew :CubernLauncher:assembleRelease
```

产物位于 `CubernLauncher/build/outputs/apk/release/`。

构建调试版：

```bash
./gradlew :CubernLauncher:assembleDebug
```

## 签名

正式版使用 `CubernLauncher/cubern-key.jks`，可通过环境变量 `CUBERN_KEYSTORE_PASSWORD` 指定口令，未设置时回退到 `gradle.properties` 中的 `cubern_keystore_password`。

## 许可与致谢

本项目基于 Zalith Launcher1 二改开发，遵循 GPL v3.0 协议。原项目作者与致谢信息保留在启动器「设置 → 关于启动器」中。

- Zalith Launcher1：https://github.com/ZalithLauncher/ZalithLauncher
- Zalith Launcher 2（陶瓦联机模块来源）：https://github.com/ZalithLauncher/ZalithLauncher2
- PojavLauncher：https://github.com/PojavLauncherTeam/PojavLauncher
- FoldCraftLauncher（Terracotta 参考实现）：https://github.com/FCL-Team/FoldCraftLauncher
