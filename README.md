# Ignore Input

![Build](https://github.com/conisdaw/IgnoreInput/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/MARKETPLACE_ID.svg)](https://plugins.jetbrains.com/plugin/MARKETPLACE_ID)

<!-- Plugin description -->
**Ignore Input** 是一个轻量级代码片段管理工具，专为代码演示和直播场景设计。提前保存代码片段，演示时按下任意键即可逐字输出预设代码，模拟真实打字效果。

本项目灵感来源于 Visual Studio 插件 [Snippet-Kit](https://github.com/bmy687/Snippet-Kit)，在 JetBrains 平台上实现了与之相同的功能。Snippet-Kit 采用 [MIT License](https://opensource.org/licenses/MIT) 协议开源。

**功能特性：**
- 保存代码片段并分配至 5 个快捷键槽位（Ctrl+1 ~ Ctrl+5）
- 一键演示模式：接管键盘输入，将预设代码逐字输出到编辑器
- 自然打字模拟：随机 1~3 字符批量输出，模拟不均匀打字速度
- 括号自动配对：(、[ 等括号自动闭合
- 面板内预览：点击"预览"按钮弹出完整代码查看
- 删除确认：删除片段前弹出确认对话框，防止误删
- 状态栏进度：实时显示当前打字行号进度
- Ctrl+0 随时停止演示，Ctrl+H 切换面板显示
<!-- Plugin description end -->

## 安装

- 通过 IDE 内置插件系统：

  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>搜索 "Ignore Input"</kbd> >
  <kbd>Install</kbd>

- 手动安装：

  从 [Releases](https://github.com/conisdaw/IgnoreInput/releases/latest) 下载最新的 `.zip` 文件，然后：
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

## 使用方法

### 保存代码片段
1. 打开 Ignore Input 面板（**Tools → 代码片段工具** 或按 **Ctrl+H**）
2. 输入片段名称，粘贴代码，选择快捷键槽位
3. 点击 **保存**

### 使用代码片段
- **面板方式**：在已保存列表中找到目标片段，点击 **使用**
- **快捷键方式**：直接按 **Ctrl+1~5** 对应槽位
- **菜单方式**：**Tools → 代码片段工具 → 应用代码片段**，从列表中选择

### 停止演示
- 按 **Ctrl+0**，或点击状态栏进度指示器

## 快捷键

| 快捷键 | 功能 |
|---|---|
| Ctrl+H | 打开/关闭面板 |
| Ctrl+1 ~ Ctrl+5 | 使用对应槽位的代码片段 |
| Ctrl+0 | 停止演示 |

## 致谢

本项目受 [Snippet-Kit](https://github.com/bmy687/Snippet-Kit) 启发，在 JetBrains 平台上以 IntelliJ Platform Plugin 形式实现了相同的代码模拟打字功能。感谢 Snippet-Kit 作者的开源贡献（MIT License）。

## 许可

本项目采用 [Apache License 2.0](LICENSE) 协议开源。

---

基于 [IntelliJ Platform Plugin Template](https://github.com/JetBrains/intellij-platform-plugin-template) 构建。
