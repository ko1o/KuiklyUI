package com.tencent.kuikly.demo.pages.compose.chatDemo

import com.tencent.kuikly.demo.pages.compose.chatDemo.configs.*
import com.tencent.kuikly.demo.pages.compose.chatDemo.widgets.*

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import com.tencent.kuikly.compose.ComposeContainer
import com.tencent.kuikly.compose.foundation.Canvas
import com.tencent.kuikly.compose.foundation.Image
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.Spacer
import com.tencent.kuikly.compose.foundation.layout.fillMaxHeight
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.offset
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.layout.size
import com.tencent.kuikly.compose.foundation.layout.width
import com.tencent.kuikly.compose.foundation.layout.widthIn
import com.tencent.kuikly.compose.foundation.layout.PaddingValues
import com.tencent.kuikly.compose.foundation.lazy.LazyColumn
import com.tencent.kuikly.compose.foundation.lazy.LazyRow
import com.tencent.kuikly.compose.foundation.lazy.items
import com.tencent.kuikly.compose.foundation.lazy.itemsIndexed
import com.tencent.kuikly.compose.foundation.lazy.rememberLazyListState
import com.tencent.kuikly.compose.foundation.shape.CircleShape
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.material3.Button
import com.tencent.kuikly.compose.material3.Text
import com.tencent.kuikly.compose.material3.TextField
import com.tencent.kuikly.compose.material3.TextFieldDefaults
import com.tencent.kuikly.compose.resources.DrawableResource
import com.tencent.kuikly.compose.resources.InternalResourceApi
import com.tencent.kuikly.compose.resources.painterResource
import com.tencent.kuikly.compose.setContent
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.Path
import com.tencent.kuikly.compose.ui.text.font.FontWeight
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.tencent.kuikly.compose.animation.core.animateFloatAsState
import com.tencent.kuikly.compose.animation.core.tween
import com.tencent.kuikly.compose.ui.platform.LocalFocusManager
import com.tencent.kuikly.core.annotations.Page
import com.tencent.kuikly.core.base.ColorStop
import com.tencent.kuikly.core.base.Direction
import com.tencent.kuikly.core.base.Translate
import com.tencent.kuikly.core.base.attr.ImageUri
import com.tencent.kuikly.core.coroutines.GlobalScope
import com.tencent.kuikly.core.coroutines.launch
import com.tencent.kuikly.core.module.RouterModule
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject
import com.tencent.kuikly.core.timer.setTimeout
import com.tencent.kuiklybase.markdown.compose.Markdown
import com.tencent.kuiklybase.markdown.model.rememberMarkdownState
import kotlinx.coroutines.delay

internal expect object NetworkClient {
    val client: Any?
}

@Page("ChatDemo")
internal class ChatDemo : ComposeContainer() {

    override fun willInit() {
        super.willInit()
        setContent {
            ChatScreen()
        }
    }

    @Composable
    internal fun ChatScreen() {
        var inputText by remember { mutableStateOf("") }
        val chatList = remember { mutableStateListOf<String>() }
        
        // FocusManager 用于收起键盘
        val focusManager = LocalFocusManager.current
        
        // 键盘高度状态
        var keyboardHeight by remember { mutableStateOf(0f) }
        
        // 扩展面板高度状态 - 参考 QQAIBiz: extBottomHeight
        var extPanelHeight by remember { mutableStateOf(0f) }
        
        // 键盘动画时长（毫秒）
        var keyboardAnimDuration by remember { mutableStateOf(250) }
        
        // 底部安全区高度
        // Android 上使用 androidBottomBavBarHeight，iOS 使用 safeAreaInsets.bottom
        // 参考 QQAIBiz: max(safeAreaInsets.bottom, 34f) 确保至少有最小值
        val bottomSafeArea = if (pagerData.isAndroid) {
            maxOf(pagerData.androidBottomBavBarHeight, 34f)
        } else {
            maxOf(pagerData.safeAreaInsets.bottom, 34f)
        }
        
        // 底部栏默认高度（包括输入框高度 + padding）
        val bottomBarDefaultHeight = 56f
        
        // 参考 QQAIBiz: marginBottom = layoutChatBottomDefaultHeight + offsetForNavBar
        // 内容区域底部边距 = 底部栏高度 + 底部安全区（固定值）
        val marginBottom = remember { bottomBarDefaultHeight + bottomSafeArea }
        
        // 内容区域高度 = 页面高度 - marginBottom（固定值，不随键盘变化）
        val contentHeight = remember { pagerData.pageViewHeight - marginBottom }
        
        // 参考 QQAIBiz: 统一计算键盘和扩展面板的偏移
        // offsetForKeyboard = -keyboardHeight + offsetForNavBar（键盘弹出时）
        // offsetForExtBottom = -extBottomHeight + offsetForNavBar（扩展面板弹出时）
        val offsetForKeyboard = if (keyboardHeight > 0f) {
            -keyboardHeight + (bottomBarDefaultHeight - bottomSafeArea)
        } else {
            0f
        }
        
        // 底部栏总偏移 = 仅键盘偏移（扩展面板已在 ChatBottomBar 内部撑高底部栏，无需额外偏移）
        val bottomBarOffset = offsetForKeyboard
        
        // 列表需要额外避让的高度
        // 注意：这是列表底部需要增加的 padding，用于避让键盘或扩展面板
        val listBottomPadding = if (keyboardHeight > 0f) {
            // 键盘弹出时：列表需要避让完整的键盘高度
            keyboardHeight
        } else if (extPanelHeight > 0f) {
            // 扩展面板显示时：列表需要避让扩展面板高度
            extPanelHeight
        } else {
            0f
        }
        
        // 底部栏位移动画
        val animatedBottomOffset by animateFloatAsState(
            targetValue = bottomBarOffset,
            animationSpec = tween(durationMillis = keyboardAnimDuration)
        )
        
        // 列表底部 padding 动画
        val animatedListPadding by animateFloatAsState(
            targetValue = listBottomPadding,
            animationSpec = tween(durationMillis = keyboardAnimDuration)
        )

        // 聊天列表滚动状态
        val listState = rememberLazyListState()
        
        // 标记是否为程序化滚动（非用户手动滚动）
        var isProgrammaticScroll by remember { mutableStateOf(false) }
        
        // 滚动时收起键盘（只在用户手动触发滚动时收起，程序化滚动不收起）
        LaunchedEffect(listState.isScrollInProgress) {
            if (listState.isScrollInProgress && !isProgrammaticScroll) {
                focusManager.clearFocus()
            }
            // 滚动结束后重置标志
            if (!listState.isScrollInProgress) {
                isProgrammaticScroll = false
            }
        }
        
        // 使用通用底部输入栏组件状态
        val bottomBarState = rememberChatBottomBarState()
        
        // 语音输入状态
        val voiceInputState = rememberVoiceInputState()
        
        // 同步输入文本状态
        LaunchedEffect(inputText) {
            if (bottomBarState.inputText.value != inputText) {
                bottomBarState.setInputText(inputText)
            }
        }
        LaunchedEffect(bottomBarState.inputText.value) {
            if (inputText != bottomBarState.inputText.value) {
                inputText = bottomBarState.inputText.value
            }
        }
        
        // 监听扩展面板状态变化，同步更新扩展面板高度
        // 互斥关系：扩展面板/键盘互斥（语音/文本互斥由底部栏内部处理）
        LaunchedEffect(bottomBarState.showExtensionPanel.value) {
            if (bottomBarState.showExtensionPanel.value) {
                // 扩展面板显示 - 设置扩展面板高度
                extPanelHeight = 286f  // LAYOUT_EXT_BOTTOM_HEIGHT
            } else {
                // 扩展面板隐藏 - 重置高度
                extPanelHeight = 0f
            }
        }
        
        // 语音模式变化时重置键盘高度
        LaunchedEffect(bottomBarState.inputType.value) {
            if (bottomBarState.inputType.value == BottomBarInputType.VOICE) {
                keyboardHeight = 0f
            }
        }

        // 根布局 - 整个页面高度固定
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(pagerData.pageViewHeight.dp)
                .background(Color(0xFFF4F4FE))
        ) {
            // 主内容区域 - 高度固定，不随键盘变化
            // 参考 QQAIBiz: Column height = contentHeight（固定值）
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(contentHeight.dp)
            ) {
                // 状态栏占位
                Spacer(modifier = Modifier.height(pagerData.statusBarHeight.dp))

                // 导航栏
                NavBar(onBack = {
                    getPager().acquireModule<RouterModule>(RouterModule.MODULE_NAME).closePage()
                })

                // 聊天列表 - 使用 weight 填充内容区域剩余空间
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    if (chatList.isNotEmpty()) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            state = listState,
                            // 列表底部留出空间，键盘弹出时避让
                            contentPadding = PaddingValues(
                                bottom = animatedListPadding.dp
                            )
                        ) {
                            itemsIndexed(chatList) { index, message ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 6.dp, vertical = 6.dp),
                                    horizontalArrangement = if (index % 2 == 0) Arrangement.End else Arrangement.Start
                                ) {
                                    ChatMessageItem(
                                        message = message,
                                        isUser = (index % 2 == 0),
                                        maxWidth = (0.7f * pagerData.pageViewWidth).dp
                                    )
                                }
                            }
                            item {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                        // 消息列表变化或键盘弹出时滚动到底部
                        LaunchedEffect(chatList.size, keyboardHeight) {
                            if (chatList.isNotEmpty()) {
                                isProgrammaticScroll = true
                                listState.animateScrollToItem(chatList.size)
                            }
                        }
                    } else {
                        welcome(
                            onInputTextChange = { inputText = it },
                            modifier = Modifier.fillMaxSize(),
                            listState = listState,
                            contentPadding = PaddingValues(bottom = animatedListPadding.dp)
                        )
                    }
                }
            }

            // 底部输入栏容器 - 参考 QQAIBiz: AppBottom 使用全屏高度容器 + transform 偏移
            // 使用 Column 包裹，高度为全屏，从顶部向下填充空白，确保底部栏内容不会被裁剪
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(pagerData.pageViewHeight.dp)
                    .offset(y = animatedBottomOffset.dp)
            ) {
                // 占位空间：页面高度 - 底部栏高度 - 底部安全区
                Spacer(modifier = Modifier.weight(1f))
                
                // 底部输入栏
                ChatBottomBar(
                    state = bottomBarState,
                    bottomSafeArea = bottomSafeArea.dp,
                    config = fullFeatureChatBottomBarConfig(
                        pageId = "ChatDemo",
                        placeholder = PLACEHOLDER
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    voiceInputState = voiceInputState,
                    onSend = { messageToSend ->
                        chatList.add(messageToSend)
                        bottomBarState.startGenerating()
                        GlobalScope.launch {
                            chatList.add("")
                            markdown.forEachIndexed { index, _ ->
                                delay(16)
                                chatList[chatList.lastIndex] =
                                    markdown.substring(0, index + 1)
                            }
                            bottomBarState.stopGenerating()
                        }
                    },
                    onStop = {
                        // 停止生成
                    },
                    onExtensionClick = {
                        // 扩展面板点击
                    },
                    onKeyboardHeightChange = { params ->
                        // 更新键盘高度和动画时长
                        keyboardAnimDuration = params.duration.toInt()
                        keyboardHeight = params.height
                        
                        // 键盘弹出时，收起扩展面板（扩展面板和键盘互斥）
                        if (params.height > 0f) {
                            extPanelHeight = 0f
                            // 同步更新状态（确保 ChatBottomBar 内部状态一致）
                            bottomBarState.showExtensionPanel.value = false
                        }
                    },
                    onVoiceRecordStart = {
                        // 开始录音 - 这里应该调用实际的录音接口
                        // TODO: 接入实际的录音模块 AIRealTimeRecorderModule
                        // 模拟录音：更新识别文字
                        voiceInputState.currentAudioText.value = "这是模拟的语音识别文字"
                    },
                    onVoiceRecordEnd = { shouldSend, text ->
                        // 结束录音 - 如果需要发送且有识别文字，则发送消息
                        if (shouldSend && text.isNotEmpty()) {
                            chatList.add(text)
                            // 模拟 AI 回复
                            bottomBarState.startGenerating()
                            GlobalScope.launch {
                                chatList.add("")
                                val response = "收到您的语音消息：\"$text\"\n\n正在为您处理..."
                                response.forEachIndexed { index, _ ->
                                    delay(16)
                                    chatList[chatList.lastIndex] = response.substring(0, index + 1)
                                }
                                bottomBarState.stopGenerating()
                            }
                        }
                    }
                )
            }
        }
    }

    @Composable
    fun NavBar(onBack: () -> Unit) {
        // 顶部导航栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            @OptIn(InternalResourceApi::class)
            val drawable = DrawableResource(ImageUri.pageAssets(BACK_ICON).toUrl("ChatDemo"))
            Image(
                painter = painterResource(drawable),
                contentDescription = "Back",
                modifier = Modifier
                    .size(16.dp)
                    .clickable { onBack() }
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "AI Chat",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.weight(1f))
            Box(modifier = Modifier.width(20.dp))
        }

        // 横线分割线
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFE3E3E3))
        )
    }

    @Composable
    fun welcome(
        onInputTextChange: (String) -> Unit,
        modifier: Modifier = Modifier,
        listState: com.tencent.kuikly.compose.foundation.lazy.LazyListState = rememberLazyListState(),
        contentPadding: PaddingValues = PaddingValues(0.dp)
    ) {
        // 使用循环生成卡片 - 艺术化设计
        val promptBoxes = listOf(
            PromptBox(
                "\uD83C\uDF93 高考志愿分析",
                "请帮我分析高考志愿填报方案，结合我的成绩和兴趣给出建议",
                "高考之路，有我护航",
                Color(0xFFCDC4BB)
            ),
            PromptBox(
                "\u26BD 世界杯观赛助手",
                "分析今天的世界杯战况如何",
                "分析比赛战况",
                Color(0xFFFEE1D3)
            ),
            PromptBox(
                "\u2600\uFE0F 医学健康助手",
                "请给出健康生活建议",
                "专业、科学",
                Color(0xFFF6BEBD)
            ),
            PromptBox(
                "\uD83C\uDF89 高考送祝福",
                "请写一段高考祝福语，祝考生金榜题名",
                "祝各位考生金榜题名",
                Color(0xFFCFAAA1)
            ),
            PromptBox(
                "\uD83D\uDCDA 学习计划助手",
                "帮我制定一个高效的学习计划，提升学习效率",
                "科学规划，高效学习",
                Color(0xFFD4E4F7)
            ),
            PromptBox(
                "\uD83C\uDFA8 创意写作助手",
                "帮我写一篇富有创意的短文或故事",
                "激发灵感，妙笔生花",
                Color(0xFFE8D5F2)
            )
        )

        LazyColumn(
            modifier = modifier,
            state = listState,
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = contentPadding
        ) {
            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
            
            item {
                // Kuikly logo
                @OptIn(InternalResourceApi::class)
                val logoDrawable = DrawableResource(ImageUri.pageAssets(LOGO_ICON).toUrl("ChatDemo"))
                Image(
                    painter = painterResource(logoDrawable),
                    contentDescription = "Kuikly Logo",
                    modifier = Modifier
                        .width(240.dp)
                        .height(70.dp)
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(40.dp))
            }

            items(promptBoxes) { box ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                com.tencent.kuikly.compose.ui.graphics.Brush.Companion.horizontalGradient(
                                    colors = listOf(box.startColor, box.endColor)
                                )
                            )
                            .clickable { onInputTextChange(box.prompt) }
                            .padding(vertical = 16.dp, horizontal = 18.dp)
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = box.title,
                                fontSize = 20.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = box.subtitle,
                                fontSize = 15.sp,
                                color = Color.Black.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
            
            item {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }


    @Composable
    fun ChatMessageItem(
        message: String,
        isUser: Boolean,
        maxWidth: Dp
    ) {
        if (isUser) {
            Box(
                modifier = Modifier
                    .widthIn(max = maxWidth)
                    .padding(bottom = 4.dp, end = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                color = Color(0xFFE9E9EB),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = message,
                            fontSize = 14.sp,
                            color = Color.Black
                        )
                    }
                    // 右侧三角
                    Canvas(
                        modifier = Modifier
                            .size(6.dp, 12.dp)
                            .align(Alignment.CenterVertically)
                    ) {
                        val width = size.width
                        val height = size.height
                        val path = Path().apply {
                            moveTo(0f, 0f)              // Box 右侧边的上点
                            lineTo(0f, height)             // Box 右侧边的下点
                            lineTo(width, height / 2f)     // 三角顶点
                            close()
                        }
                        drawPath(
                            path = path,
                            color = Color(0xFFE9E9EB)
                        )
                    }
                }
            }
        } else {
            val markdownState = rememberMarkdownState()
            LaunchedEffect(message) {
                markdownState.parse(message, false)
            }
            Markdown(
                state = markdownState,
                colors = markdownColor(text = Color.Black),
                typography = markdownTypography(),
                modifier = Modifier
                    .widthIn(max = pagerData.pageViewWidth.dp)
                    .padding(horizontal = 24.dp)
            )
        }
    }

    // ohos使用（原生模块方式）
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
                        // ArkTS端每次推送一段流式内容
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
        private const val BACK_ICON = "ic_back.png"
        private const val SEND_ICON = "ic_send.png"
        private const val EXT_ICON = "ic_add.png"
        private const val STOP_ICON = "ic_stop.png"
        private const val LOGO_ICON = "kuikly_logo.png"

        private const val PLACEHOLDER = "说点什么..."
        private val markdown = """
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

internal data class PromptBox(
    val title: String,
    val prompt: String,
    val subtitle: String = "",
    val startColor: Color = Color.White,
    val endColor: Color = Color.White
)
