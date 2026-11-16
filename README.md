# Cubern Launcher

基于 Zalith Launcher 1 重构开发的 Minecraft Java 版启动器，采用 GPLv3 开源协议。

## 主要特性

- **26.2+ 运行环境支持**：完善版本语义比较，禁止伪造版本号
- **自动 Java 运行时管理**：识别、下载、校验并注入兼容Java运行时
- **淘瓦联机功能**：集成 Zalith Launcher 2 联机功能
- **Vulkan 渲染支持**：四种渲染模式选择，自动检测与回退
- **MUIX Compose UI**：现代化用户界面
- **依赖库去重**：按 groupId:artifactId:version:classifier:abi 去重
- **安全启动**：明确错误提示，安全兼容回退

## 快速开始

```bash
# 克隆仓库
git clone https://github.com/1953187487/CubernLauncher.git

# 使用Gradle构建
cd CubernLauncher
./gradlew build
```

## 许可证

GNU General Public License v3.0
