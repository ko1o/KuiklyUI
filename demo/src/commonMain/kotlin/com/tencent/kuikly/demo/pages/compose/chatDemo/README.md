<div align = center>

# AI Chat Demo

</div>

## Demo Showcase

<div style="display: flex; flex-wrap: wrap; justify-content: center; gap: 20px;">
    <img src="/img/chatdemo_android.png" width=150 />
    <img src="/img/chatdemo_ios.png" width=150 />
    <img src="/img/chatdemo_ohos.jpg" width=150 />
</div>

## API Key Application

This demo uses Tencent Hunyuan Large Model as the conversational model, and sends requests using an OpenAI-compatible interface. For details on how to apply for an API Key, please refer to [this link](https://cloud.tencent.com/document/product/1729/111008). The main steps include:

- After registering and completing personal or enterprise authentication, log in to [Tencent Cloud](https://cloud.tencent.com/).
- The Tencent Hunyuan Large Model API is now open to the public. Go to the [console](https://console.cloud.tencent.com/hunyuan/settings) to enable the service.
- Go to [Console > Start Access](https://console.cloud.tencent.com/hunyuan/start), select the OpenAI SDK access method, and click to create an API KEY.

After obtaining the API Key, fill the key into the `companion object` section in the `ChatDemo.kt` file:

```Kotlin
companion object {
    ...
    private const val CHAT_API_KEY = "<YUANBAO-API-KEY>"
    ...
}
```

Of course, you can also choose to integrate other models that are compatible with the OpenAI API. In addition to changing the `CHAT_API_KEY`, you will also need to modify the `CHAT_URL` and `CHAT_MODEL` values.

```Kotlin
companion object {
    ...
    private const val CHAT_URL = "https://URL/v1/chat/completions"
    private const val CHAT_MODEL = "model"
    private const val CHAT_API_KEY = "<YUANBAO-API-KEY>"
    ...
}
```

## Compilation on Different Platforms

Since the AI request implementation differs across platforms in this demo: Android and iOS use ktor to implement SSE network requests, while HarmonyOS uses native methods for SSE network requests. Therefore, to run this demo, you need to modify some files (if not modified, only a default Markdown text will be returned).

### Android and iOS

- First, uncomment the following function and import the corresponding ktor package in the `ChatDemo.kt` file:

```Kotlin
/* For Android and iOS (ktor request method) */
private suspend fun sendStreamMessage(
    client: HttpClient,
    url: String,
    model: String,
    apiKey: String,
    prompt: String,
    chatList: MutableList<String>,
) {
    try {
        withContext(Dispatchers.Main) {
            chatList.add("")
        }
        val msgIndex = chatList.lastIndex
        var streamingMsg = ""

        withContext(Dispatchers.IO) {
            val response: HttpResponse = client.post(url) {
                headers {
                    append("Authorization", "Bearer $apiKey")
                    append("Content-Type", "application/json")
                    append("Accept", "text/event-stream")
                }
                setBody(
                    """
                    {
                        "model": "$model",
                        "messages": [{"role": "user", "content": "$prompt"}],
                        "stream": true
                    }
                    """.trimIndent()
                )
            }

            val channel: ByteReadChannel = response.bodyAsChannel()

            while (!channel.isClosedForRead) {
                val line = channel.readUTF8Line() ?: break
                if (line.startsWith("data:")) {
                    val data = line.removePrefix("data: ").trim()
                    if (data == "[DONE]") break
                    val delta = extractContentFromDelta(data)
                    if (delta.isNotEmpty()) {
                        streamingMsg += delta
                        withContext(Dispatchers.Main) {
                            chatList[msgIndex] = streamingMsg
                        }
                    }
                }
            }
        }
    } catch (e: Exception) {
        withContext(Dispatchers.Main) {
            chatList.add("[Error：${e.message}]")
        }
    }
}
```

- At line 220 in `ChatDemo.kt`, uncomment the Android and iOS request logic:

```Kotlin
GlobalScope.launch {
    // For Android and iOS, send and process messages via ktor interface
    sendStreamMessage(
        client = NetworkClient.client as HttpClient,
        url = CHAT_URL,
        model = CHAT_MODEL,
        apiKey = CHAT_API_KEY,
        prompt = messageToSend,
        chatList = chatList
    )

    /* For OHOS, send and process messages via native bridge module
    sendOhosMessage(
        url = CHAT_URL,
        model = CHAT_MODEL,
        apiKey = CHAT_API_KEY,
        prompt = messageToSend,
        chatList = chatList
    ) */
}

/* Comment out the default simulated reply text
GlobalScope.launch {
    chatList.add("")
    markdown.forEachIndexed { index, _ ->
        delay(16)
        chatList[chatList.lastIndex] =
            markdown.substring(0, index + 1)
    }
} */
```

- Re-sync and then build and run.

### HarmonyOS

- Comment out the `sendStreamMessage` function for Android and iOS and the ktor-related imports.
- At line 220 in `ChatDemo.kt`, uncomment the HarmonyOS request logic:

```Kotlin
GlobalScope.launch {
    /* For Android and iOS, send and process messages via ktor interface
    sendStreamMessage(
        client = NetworkClient.client as HttpClient,
        url = CHAT_URL,
        model = CHAT_MODEL,
        apiKey = CHAT_API_KEY,
        prompt = messageToSend,
        chatList = chatList
    ) */

    // For OHOS, send and process messages via native bridge module
    sendOhosMessage(
        url = CHAT_URL,
        model = CHAT_MODEL,
        apiKey = CHAT_API_KEY,
        prompt = messageToSend,
        chatList = chatList
    )
}

/* Comment out the default simulated reply text
GlobalScope.launch {
    chatList.add("")
    markdown.forEachIndexed { index, _ ->
        delay(16)
        chatList[chatList.lastIndex] =
            markdown.substring(0, index + 1)
    }
} */
```

- Rebuild and run.

## Integrating Markdown Render Separately

Specify the Maven repository and add the dependency:

```gradle
maven("https://mirrors.tencent.com/nexus/repository/maven-public/")
```

```gradle
dependencies {
    implementation("com.tencent.kuiklybase:markdown:${version}")
}
```

> The latest version for Android and iOS is 0.1.0, and for HarmonyOS it is 0.1.0-ohos.

---

## UI Config 化规范

本项目所有 UI 组件严格遵循 **Config 驱动 + Builder 优先** 的设计原则，目的是让其他工程复用时**只需修改 Config 即可自定义样式**，无需修改组件源码。

### 核心规则

1. **所有涉及 UI 的值必须抽到 Config 中**
   - 颜色（背景色、文字色、边框色、分割线色等）
   - 字体大小（`TextUnit`）
   - 间距 / 内边距（`Dp`）
   - 圆角、边框宽度、图标尺寸
   - 文案文本（占位符、按钮文字、提示语等）
   - 动画时长

2. **每个 Config 都支持 `builder` 属性**
   - 如果 `builder` 有值 → 直接调用 builder 来渲染 UI，完全绕过默认实现
   - 如果 `builder` 为 `null` → 走默认实现，使用 Config 中的值
   - 粒度分层：整体 `builder` + 子区域 `builder`，按需覆盖

3. **默认值保持原有行为**
   - 所有 Config 字段都有默认值，不传 Config 时表现与之前完全一致

### 目录结构

```
chatDemo/
├── configs/                              # 所有 Config 定义
│   ├── CapsuleHalfViewConfig.kt          # 半浮层配置 + CapsuleHalfViewUIConfig
│   ├── ChatBottomBarConfig.kt            # 底部输入栏配置
│   ├── ChatCapsuleBarConfig.kt           # 胶囊栏配置
│   └── AppComponentsConfig.kt           # App 层组件配置集合
│       ├── AppConfig                      #   App 主入口
│       ├── AppTopConfig                  #   顶部导航栏
│       ├── WelcomeViewConfig             #   欢迎页
│       ├── ChatMessageItemConfig         #   消息气泡
│       ├── VoiceInputConfig              #   语音输入
│       ├── ImagePickerConfig             #   图片选择器
│       └── PopoverConfig                 #   弹出菜单
│
├── widgets/                              # 通用 UI 组件（读 Config 渲染）
│   ├── CapsuleHalfView.kt
│   ├── ChatBottomBar.kt
│   ├── ChatCapsuleBar.kt
│   ├── ExtensionPanel.kt                # 含 ExtensionPanelConfig
│   ├── ImagePickerView.kt
│   ├── Popover.kt
│   └── VoiceInputLayout.kt
│
├── app/                                  # 主应用模块
│   ├── App.kt
│   ├── AppState.kt
│   ├── app_top/AppTop.kt               # 使用 AppTopConfig
│   ├── app_content/
│   │   ├── AppContent.kt
│   │   ├── ChatMessageItem.kt           # 使用 ChatMessageItemConfig
│   │   └── WelcomeView.kt              # 使用 WelcomeViewConfig
│   └── app_bottom/AppBottom.kt
│
├── data/                                 # JSON 数据
├── modules/                              # 原生模块
└── ChatDemo.kt                          # 页面入口
```

### Config 与 Builder 对照表

| 组件 | Config 类 | Builder 属性 |
|------|-----------|-------------|
| App 主入口 | `AppConfig` | `builder` |
| 半浮层 | `CapsuleHalfViewUIConfig` | `contentBuilder`、`titleBarBuilder`、`typeSectionBuilder`、`requireSectionBuilder`、`aiDrawContentBuilder` |
| 胶囊栏 | `ChatCapsuleBarConfig` | `builder`、`itemBuilder` |
| 底部输入栏 | `ChatBottomBarConfig` | `builder`、`inputBuilder`、`rightButtonsBuilder` |
| 扩展面板 | `ExtensionPanelConfig` | `builder`、`itemBuilder` |
| 顶部导航栏 | `AppTopConfig` | `builder`、`navBarBuilder` |
| 欢迎页 | `WelcomeViewConfig` | `builder`、`cardBuilder` |
| 消息气泡 | `ChatMessageItemConfig` | `userMessageBuilder`、`aiMessageBuilder` |
| 语音输入 | `VoiceInputConfig` | `voiceButtonBuilder`、`recordViewBuilder` |
| 图片选择器 | `ImagePickerConfig` | `builder` |
| 弹出菜单 | `PopoverConfig` | `builder` |

### 核心架构：`AppUIConfig` + `getAppUIConfig()`

所有子 Config 统一聚合在 **`AppUIConfig`** 中，`ChatDemo` 通过 `open fun getAppUIConfig()` 返回默认配置。

其他工程要定制 UI，只需**继承 `ChatDemo` 并重写 `getAppUIConfig()`**，用 `copy` 快速改想改的属性即可。

```
AppUIConfig (总入口)
├── app: AppConfig                       # 背景色、高度常量、占位符
├── appTop: AppTopConfig                 # 导航栏
├── welcomeView: WelcomeViewConfig       # 欢迎页
├── chatMessageItem: ChatMessageItemConfig # 消息气泡
├── chatBottomBar: ChatBottomBarConfig   # 底部输入栏
├── capsuleBar: ChatCapsuleBarConfig     # 胶囊栏
├── halfViewUI: CapsuleHalfViewUIConfig  # 半浮层 UI
├── extensionPanel: ExtensionPanelConfig # 扩展面板
├── voiceInput: VoiceInputConfig         # 语音输入
├── imagePicker: ImagePickerConfig       # 图片选择器
└── popover: PopoverConfig               # 弹出菜单
```

数据流：
```
ChatDemo.getAppUIConfig()
    ↓
rememberAppState(uiConfig = ...)
    ↓
App(uiConfig = ...)
    ├── AppTop(config = uiConfig.appTop)
    ├── AppContent → WelcomeView / ChatMessageItem
    └── AppBottom(uiConfig = ...)
        ├── ChatCapsuleBar(config = uiConfig.capsuleBar)
        ├── CapsuleHalfView(uiConfig = uiConfig.halfViewUI)
        └── ChatBottomBar(config = ...)
```

### 使用示例

#### 1. 只改某个属性（用 copy，定制哪个改哪个）

```kotlin
@Page("MyChat")
class MyChat : ChatDemo() {
    override fun getAppUIConfig(): AppUIConfig {
        val default = super.getAppUIConfig()
        return default.copy(
            // 只改背景色和占位符
            app = default.app.copy(
                backgroundColor = Color(0xFF1A1C1E),
                defaultPlaceholder = "Ask me anything..."
            ),
            // 只改导航栏标题颜色
            appTop = default.appTop.copy(
                titleColor = Color.White,
                title = "My AI"
            )
        )
    }
}
```

#### 2. 批量自定义多个组件

```kotlin
@Page("DarkChat")
class DarkChat : ChatDemo() {
    override fun getAppUIConfig() = AppUIConfig(
        app = AppConfig(backgroundColor = Color.Black),
        appTop = AppTopConfig(titleColor = Color.White, dividerColor = Color(0xFF333333)),
        chatMessageItem = ChatMessageItemConfig(
            userBubbleColor = Color(0xFF2A2A2A),
            userTextColor = Color.White,
            aiTextColor = Color(0xFFCCCCCC)
        ),
        halfViewUI = CapsuleHalfViewUIConfig(
            backgroundColor = Color(0xFF1A1C1E),
            titleColor = Color.White,
            typeButtonSelectedBgColor = Color(0xFFFF6B6B)
        )
    )
}
```

#### 3. 用 Builder 替换某个子区域

```kotlin
@Page("CustomTitleChat")
class CustomTitleChat : ChatDemo() {
    override fun getAppUIConfig(): AppUIConfig {
        return super.getAppUIConfig().copy(
            halfViewUI = CapsuleHalfViewUIConfig(
                titleBarBuilder = { title, _, onClose ->
                    Row(Modifier.fillMaxWidth().padding(16.dp)) {
                        Text("🤖 $title", fontSize = 20.sp, color = Color.Blue)
                        Spacer(Modifier.weight(1f))
                        IconButton(onClick = onClose) { Text("关闭") }
                    }
                }
            ),
            capsuleBar = defaultCapsuleBarConfig().copy(
                itemBuilder = { item, index, config, onClick ->
                    MyCustomCapsuleItem(item, onClick)
                }
            )
        )
    }
}
```

#### 4. 用 Builder 完全替换整个组件

```kotlin
@Page("FullCustomChat")
class FullCustomChat : ChatDemo() {
    override fun getAppUIConfig(): AppUIConfig {
        return super.getAppUIConfig().copy(
            appTop = AppTopConfig(
                builder = { statusBarHeight, onBack ->
                    MyEntirelyCustomTopBar(statusBarHeight, onBack)
                }
            )
        )
    }
}
```

### 新增组件 Checklist

新增 UI 组件时，必须按以下步骤操作：

- [ ] 在 `configs/` 下创建对应的 `XxxConfig` data class
- [ ] 将所有硬编码的颜色、字号、间距、文案提取为 Config 字段，给出合理默认值
- [ ] 添加 `builder` 属性（整体 builder + 按需添加子区域 builder）
- [ ] 组件实现中优先检查 `builder`，有值则直接调用；否则使用 Config 字段渲染
- [ ] 确保不传 Config 时行为与硬编码时完全一致
