# Cubern Launcher

基于 **Zalith Launcher1** 进行二改开发的 Minecraft Java 版 Android 启动器，采用 **GNU General Public License v3.0** 开源协议。

当前启动器作者：**万古沧海**
原作者：**Zalith Launcher1 启动器作者**

## 新增内容

- **26.2+ 运行环境支持**：版本号语义比较，杜绝通过伪造或跳过版本号绕过限制
- **Java 运行时自动管理**：自动识别、下载、校验并注入兼容运行时，校验架构与完整性
- **淘瓦联机功能**：参考 Zalith Launcher2 实现，入口位于主界面右上角
- **Vulkan 支持检测**：核验 Vulkan API、物理设备、Vulkan 1.2 能力、图形队列、表面扩展与所需原生库
- **四种渲染模式**：跟随游戏、OpenGL、Vulkan、自动选择
- **MIUIX Compose UI**：主界面与设置界面采用 MIUIX 框架重写布局
- **设置界面整合**：原主界面的「关于启动器」与「管理控制」布局已合并进设置
- **依赖库去重**：按 groupId:artifactId:version:classifier:abi 去重
- **安全回退**：环境不满足时给出明确原因并回退到兼容渲染后端

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
