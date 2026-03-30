package com.tencent.kuikly.demo.pages.compose.chatDemo

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.ComposeContainer
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.attr.ImageUri
import com.tencent.kuikly.core.coroutines.GlobalScope
import com.tencent.kuikly.core.coroutines.launch
import com.tencent.kuikly.core.log.KLog
import com.tencent.kuikly.core.module.Module
import com.tencent.kuikly.core.module.RouterModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.demo.pages.base.BridgeModule
import com.tencent.kuikly.demo.pages.compose.chatDemo.app.*
import com.tencent.kuikly.demo.pages.compose.chatDemo.configs.*
import com.tencent.kuikly.demo.pages.compose.chatDemo.modules.MediaModule
import com.tencent.kuikly.demo.pages.compose.chatDemo.widgets.*
import kotlinx.coroutines.delay

/**
 * 平台相关网络客户端
 */
internal expect object NetworkClient {
    val client: Any?
}

/**
 * ChatDemo 页面入口
 * 
 * 参考 QQAIBiz 的 BabyQPage 设计，将页面逻辑和 UI 组件分离：
 * - ChatDemo: 页面入口，负责模块管理和生命周期
 * - ChatDemoApp: 主 UI 组件，负责布局和交互
 * - ChatDemoAppState: 状态管理，负责数据和业务逻辑
 * 
 * 目录结构：
 * - app/                    # 主应用模块
 *   - App.kt               # 主入口组件
 *   - AppState.kt          # 状态管理
 *   - app_top/             # 顶部区域（状态栏、导航栏）
 *   - app_content/         # 内容区域（消息列表、欢迎页）
 *   - app_bottom/          # 底部区域（输入栏、扩展面板）
 *   - app_float/           # 浮动元素（胶囊栏、半浮层）
 * - configs/               # 配置文件
 * - widgets/               # 通用组件
 * - data/                  # 数据模型
 * - modules/               # 原生模块
 */
@Page("ChatDemo")
internal open class ChatDemo : ComposeContainer() {

    // MediaModule 实例
    private val mediaModule by lazy { 
        acquireModule(MediaModule.MODULE_NAME) as? MediaModule
    }
    
    // BridgeModule 实例（用于 Toast 等）
    private val bridgeModule by lazy {
        acquireModule(BridgeModule.MODULE_NAME) as? BridgeModule
    }

    /**
     * 获取 AppUIConfig - 所有 UI 配置的总入口
     *
     * 子类重写此方法即可定制整个 App 的 UI 样式，支持 copy 快速改某个属性：
     * ```kotlin
     * override fun getAppUIConfig(): AppUIConfig {
     *     val default = super.getAppUIConfig()
     *     return default.copy(
     *         app = default.app.copy(backgroundColor = Color.Black),
     *         appTop = default.appTop.copy(titleColor = Color.White),
     *         chatMessageItem = default.chatMessageItem.copy(userBubbleColor = Color.Blue)
     *     )
     * }
     * ```
     */
    open fun getAppUIConfig(): AppUIConfig = AppUIConfig()

    override fun createExternalModules(): Map<String, Module>? {
        val modules = super.createExternalModules() as? HashMap ?: hashMapOf()
        modules[MediaModule.MODULE_NAME] = MediaModule()
        modules[BridgeModule.MODULE_NAME] = BridgeModule()
        return modules
    }

    override fun willInit() {
        super.willInit()
        setContent {
            ChatScreen()
        }
    }

    @Composable
    private fun ChatScreen() {
        // 获取 UI 配置（子类可重写 getAppUIConfig() 来定制）
        val uiConfig = getAppUIConfig()
        
        // 计算底部安全区高度
        val bottomSafeArea = if (pagerData.isAndroid) {
            maxOf(pagerData.androidBottomBavBarHeight, 34f)
        } else {
            maxOf(pagerData.safeAreaInsets.bottom, 34f)
        }
        
        // 创建应用状态，传入 uiConfig
        val appState = rememberAppState(
            pageViewHeight = pagerData.pageViewHeight,
            pageViewWidth = pagerData.pageViewWidth,
            statusBarHeight = pagerData.statusBarHeight,
            bottomSafeArea = bottomSafeArea,
            uiConfig = uiConfig
        )
        
        // 主 UI 组件，传入 uiConfig
        App(
            appState = appState,
            uiConfig = uiConfig,
            onBack = {
                getPager().acquireModule<RouterModule>(RouterModule.MODULE_NAME).closePage()
            },
            onSend = { messageToSend ->
                handleSendMessage(appState, messageToSend)
            },
            onStop = {
                appState.bottomBarState.stopGenerating()
            },
            onExtensionPanelItemClick = { itemType ->
                handleExtensionPanelItemClick(appState, itemType)
            },
            onVoiceRecordStart = {
                handleVoiceRecordStart(appState)
            },
            onVoiceRecordEnd = { shouldSend, text ->
                handleVoiceRecordEnd(appState, shouldSend, text)
            },
            onCapsuleItemClick = { index, item ->
                bridgeModule?.toast("点击了: ${item.name}")
            }
        )
    }
    
    /**
     * 处理发送消息
     * 
     * 参考 QQAIBiz BabyQBottomViewModel.doSendMsgInner()：
     * 1. 使用 reGenerateInputText() 将用户输入与半浮层选中的条件拼接
     * 2. 发送拼接后的完整文本
     * 3. 发送完成后调用 onSendMessageCleanup() 清空选中状态
     */
    private fun handleSendMessage(appState: ChatDemoAppState, message: String) {
        // 获取拼接后的完整文本（包含半浮层选中的条件）
        val fullMessage = appState.reGenerateInputText(message)
        
        // 记录日志，方便调试
        val scene = appState.currentHalfViewScene()
        if (scene != HalfViewScene.NONE) {
            KLog.i("ChatDemo", "半浮层场景: $scene")
            KLog.i("ChatDemo", "原始输入: $message")
            KLog.i("ChatDemo", "拼接后文本: $fullMessage")
        }
        
        // 添加到聊天列表
        appState.chatList.add(fullMessage)
        appState.bottomBarState.startGenerating()
        
        // 发送后收起半浮层面板并清空选中内容
        appState.hideCapsuleHalfView()
        appState.onSendMessageCleanup()
        
        // 模拟 AI 回复
        GlobalScope.launch {
            appState.chatList.add("")
            MOCK_MARKDOWN_RESPONSE.forEachIndexed { index, _ ->
                delay(16)
                appState.chatList[appState.chatList.lastIndex] =
                    MOCK_MARKDOWN_RESPONSE.substring(0, index + 1)
            }
            appState.bottomBarState.stopGenerating()
        }
    }
    
    /**
     * 处理扩展面板项点击
     */
    private fun handleExtensionPanelItemClick(appState: ChatDemoAppState, itemType: ExtensionPanelItemType) {
        when (itemType) {
            ExtensionPanelItemType.PHOTO -> {
                KLog.i("ChatDemo", "点击照片按钮，打开相册")
                handlePhotoSelection(appState)
            }
            ExtensionPanelItemType.CAMERA -> {
                KLog.i("ChatDemo", "点击拍摄按钮，打开相机")
                handleCameraCapture(appState)
            }
            ExtensionPanelItemType.FILE -> {
                KLog.i("ChatDemo", "点击文件按钮")
                bridgeModule?.toast("功能未实现")
            }
            ExtensionPanelItemType.DOCUMENT -> {
                KLog.i("ChatDemo", "点击文档按钮")
                bridgeModule?.toast("功能未实现")
            }
        }
    }
    
    /**
     * 处理照片选择
     */
    private fun handlePhotoSelection(appState: ChatDemoAppState) {
        val currentCount = appState.bottomBarState.imagePickerState.imageCount
        val maxCount = MAX_IMAGE_PICK_COUNT - currentCount
        
        if (maxCount <= 0) {
            bridgeModule?.toast("最多添加${MAX_IMAGE_PICK_COUNT}张照片")
            return
        }
        
        // 模拟选择图片
        val selectCount = minOf(maxCount, (1..3).random())
        val mockImages = TestImageResources.getMockSelectedImages(selectCount)
        appState.bottomBarState.imagePickerState.addImages(mockImages)
        KLog.i("ChatDemo", "模拟选图成功，添加 ${mockImages.size} 张图片")
        
        appState.bottomBarState.showExtensionPanel.value = false
    }
    
    /**
     * 处理相机拍摄
     */
    private fun handleCameraCapture(appState: ChatDemoAppState) {
        val currentCount = appState.bottomBarState.imagePickerState.imageCount
        if (currentCount >= MAX_IMAGE_PICK_COUNT) {
            bridgeModule?.toast("最多添加${MAX_IMAGE_PICK_COUNT}张照片")
            return
        }
        
        val mockImage = LocalMediaInfo(
            path = ImageUri.commonAssets("panda.png").toUrl(""),
            width = 200,
            height = 200,
            fileSize = 1024 * 100,
            mimeType = "image/png"
        )
        
        appState.bottomBarState.imagePickerState.addImage(mockImage)
        KLog.i("ChatDemo", "模拟拍照成功")
        
        appState.bottomBarState.showExtensionPanel.value = false
    }
    
    /**
     * 处理语音录制开始
     */
    private fun handleVoiceRecordStart(appState: ChatDemoAppState) {
        // TODO: 接入实际的录音模块
        appState.voiceInputState.currentAudioText.value = "这是模拟的语音识别文字"
    }
    
    /**
     * 处理语音录制结束
     */
    private fun handleVoiceRecordEnd(appState: ChatDemoAppState, shouldSend: Boolean, text: String) {
        if (shouldSend && text.isNotEmpty()) {
            appState.chatList.add(text)
            appState.bottomBarState.startGenerating()
            
            GlobalScope.launch {
                appState.chatList.add("")
                val response = "收到您的语音消息：\"$text\"\n\n正在为您处理..."
                response.forEachIndexed { index, _ ->
                    delay(16)
                    appState.chatList[appState.chatList.lastIndex] = response.substring(0, index + 1)
                }
                appState.bottomBarState.stopGenerating()
            }
        }
    }

    // ohos 使用（原生模块方式）
    private fun sendOhosMessage(
        url: String,
        model: String,
        apiKey: String,
        prompt: String,
        chatList: MutableList<String>
    ) {
        chatList.add(prompt)
        chatList.add("")
        val msgIndex = chatList.lastIndex
        println("prompt: $prompt")

        getPager().acquireModule<OhosStreamRequestModule>(OhosStreamRequestModule.MODULE_NAME)
            .request(url, model, apiKey, prompt) { event ->
                when (event?.optString("event")) {
                    "data" -> {
                        val delta = extractContentFromDelta(event.optString("data"))
                        if (delta.isNotEmpty()) {
                            chatList[msgIndex] = chatList[msgIndex] + delta
                            println(chatList[msgIndex])
                        }
                    }
                    "error" -> {
                        chatList.add("[出错：${event.optString("data")}]")
                    }
                }
            }
    }

    private fun extractContentFromDelta(delta: String): String {
        val json = JSONObject(delta)
        val choices = json.optJSONArray("choices")
        if (choices != null && choices.length() > 0) {
            val firstChoice = choices.optJSONObject(0)
            val deltaObj = firstChoice?.optJSONObject("delta")
            if (deltaObj != null) {
                return deltaObj.optString("content", "")
            }
        }
        return ""
    }

    companion object {
        /** 模拟 AI 回复的 Markdown 文本 */
        private val MOCK_MARKDOWN_RESPONSE = """
            # 一级标题
            ## 二级标题
            这是一段模拟AI回复的markdown文本，**这是一段AI回复的加粗markdown文本**
            *这是一段模拟AI回复的斜体markdown文本*

            ~~这是一段模拟AI回复的删除线markdown文本~~
            > 这是一段AI引用的markdown文本

            | 列1         |  列2  |
            |------------|-----------|
            | 数据1 [1](@ref) | 数据2 |
            | 示例A        | 示例B |
            | 测试1        | 测试2 |
            | 临时A        | 临时B |

            这是一段AI回复的无序列表:
            - 项目1
        """.trimIndent()
    }
}
