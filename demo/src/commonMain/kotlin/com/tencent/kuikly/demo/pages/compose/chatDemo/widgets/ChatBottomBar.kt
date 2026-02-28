package com.tencent.kuikly.demo.pages.compose.chatDemo.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.tencent.kuikly.compose.animation.core.animateFloatAsState
import com.tencent.kuikly.compose.animation.core.tween
import com.tencent.kuikly.compose.extension.keyboardHeightChange
import com.tencent.kuikly.compose.foundation.Image
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.RowScope
import com.tencent.kuikly.compose.foundation.layout.Spacer
import com.tencent.kuikly.compose.foundation.layout.defaultMinSize
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.heightIn
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.layout.size
import com.tencent.kuikly.compose.foundation.layout.width
import com.tencent.kuikly.compose.foundation.layout.wrapContentHeight
import com.tencent.kuikly.compose.foundation.shape.CircleShape
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.foundation.interaction.MutableInteractionSource
import com.tencent.kuikly.compose.foundation.layout.PaddingValues
import com.tencent.kuikly.compose.foundation.text.BasicTextField
import com.tencent.kuikly.compose.material3.ExperimentalMaterial3Api
import com.tencent.kuikly.compose.material3.Text
import com.tencent.kuikly.compose.material3.TextFieldDefaults
import com.tencent.kuikly.compose.ui.graphics.SolidColor
import com.tencent.kuikly.compose.ui.text.input.VisualTransformation
import com.tencent.kuikly.compose.resources.DrawableResource
import com.tencent.kuikly.compose.resources.InternalResourceApi
import com.tencent.kuikly.compose.resources.painterResource
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.alpha
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.draw.rotate
import com.tencent.kuikly.compose.ui.focus.FocusRequester
import com.tencent.kuikly.compose.ui.focus.focusRequester
import com.tencent.kuikly.compose.ui.platform.LocalFocusManager
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.text.TextStyle
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.tencent.kuikly.core.views.KeyboardParams
import com.tencent.kuikly.demo.pages.compose.chatDemo.configs.*

// ==================== 状态管理 ====================

/**
 * 底部输入栏输入类型 - 语音和文本互斥
 */
enum class BottomBarInputType {
    /** 文本输入 - 显示输入框 */
    TEXT,
    /** 语音输入 - 显示"按住 说话"按钮 */
    VOICE
}

/**
 * 底部输入栏输入模式 - 兼容旧代码，保留三种模式
 * 注意：实际上只有两组互斥关系
 * - 语音/文本互斥（输入类型）
 * - 扩展面板/键盘互斥（底部面板）
 */
enum class BottomBarInputMode {
    /** 文本输入模式 - 显示输入框，可能显示键盘 */
    TEXT,
    /** 语音输入模式 - 显示"按住 说话"按钮 */
    VOICE,
    /** 扩展面板模式 - 显示扩展面板（照片、拍摄、文件、文档） */
    EXTENSION
}

/**
 * 底部输入栏状态
 * 
 * 互斥关系：
 * - 语音/文本输入 互斥（inputType）
 * - 扩展面板/键盘 互斥（showExtensionPanel + keyboardHeight）
 */
class ChatBottomBarState(
    initialText: String = "",
    initialInputType: BottomBarInputType = BottomBarInputType.TEXT,
    initialShowExtPanel: Boolean = false
) {
    // 输入文本
    val inputText: MutableState<String> = mutableStateOf(initialText)
    
    // 输入类型 - 语音/文本互斥
    val inputType: MutableState<BottomBarInputType> = mutableStateOf(initialInputType)
    
    // 是否显示扩展面板 - 扩展面板/键盘互斥
    val showExtensionPanel: MutableState<Boolean> = mutableStateOf(initialShowExtPanel)
    
    // 是否正在生成（流式响应）
    val isGenerating: MutableState<Boolean> = mutableStateOf(false)
    
    // 键盘高度
    val keyboardHeight: MutableState<Float> = mutableStateOf(0f)
    
    // 语音按钮是否启用
    val voiceIconEnabled: MutableState<Boolean> = mutableStateOf(true)
    
    // ==================== 兼容旧代码的 inputMode ====================
    
    // inputMode 现在是一个派生属性，根据 inputType 和 showExtensionPanel 计算
    val inputMode: MutableState<BottomBarInputMode>
        get() = object : MutableState<BottomBarInputMode> {
            override var value: BottomBarInputMode
                get() = when {
                    showExtensionPanel.value -> BottomBarInputMode.EXTENSION
                    inputType.value == BottomBarInputType.VOICE -> BottomBarInputMode.VOICE
                    else -> BottomBarInputMode.TEXT
                }
                set(newValue) {
                    when (newValue) {
                        BottomBarInputMode.TEXT -> {
                            inputType.value = BottomBarInputType.TEXT
                            showExtensionPanel.value = false
                        }
                        BottomBarInputMode.VOICE -> {
                            inputType.value = BottomBarInputType.VOICE
                            showExtensionPanel.value = false
                        }
                        BottomBarInputMode.EXTENSION -> {
                            showExtensionPanel.value = true
                        }
                    }
                }
            override fun component1(): BottomBarInputMode = value
            override fun component2(): (BottomBarInputMode) -> Unit = { value = it }
        }
    
    // ==================== 便捷属性（兼容旧代码） ====================
    
    // 是否处于语音输入模式
    val isVoiceMode: MutableState<Boolean>
        get() = object : MutableState<Boolean> {
            override var value: Boolean
                get() = inputType.value == BottomBarInputType.VOICE
                set(newValue) {
                    inputType.value = if (newValue) BottomBarInputType.VOICE else BottomBarInputType.TEXT
                }
            override fun component1(): Boolean = value
            override fun component2(): (Boolean) -> Unit = { value = it }
        }
    
    // 是否可发送
    val canSend: Boolean
        get() = inputText.value.isNotBlank() && !isGenerating.value
    
    // ==================== 模式切换方法 ====================
    
    /**
     * 切换到文本输入模式（收起扩展面板）
     */
    fun switchToTextMode() {
        inputType.value = BottomBarInputType.TEXT
        showExtensionPanel.value = false
    }
    
    /**
     * 切换到语音输入模式（收起扩展面板）
     */
    fun switchToVoiceMode() {
        inputType.value = BottomBarInputType.VOICE
        showExtensionPanel.value = false
        // 切换到语音模式时重置键盘高度
        keyboardHeight.value = 0f
    }
    
    /**
     * 切换到扩展面板模式（不改变输入类型）
     */
    fun switchToExtensionMode() {
        showExtensionPanel.value = true
    }
    
    /**
     * 收起扩展面板（不改变输入类型）
     */
    fun hideExtensionPanel() {
        showExtensionPanel.value = false
    }
    
    /**
     * 切换语音/文本输入类型（点击语音按钮）
     * - 如果当前是语音模式 -> 切换到文本模式（同时关闭扩展面板）
     * - 如果当前是文本模式 -> 切换到语音模式（同时关闭扩展面板）
     */
    fun toggleVoiceMode() {
        if (inputType.value == BottomBarInputType.VOICE) {
            inputType.value = BottomBarInputType.TEXT
            showExtensionPanel.value = false
        } else {
            inputType.value = BottomBarInputType.VOICE
            showExtensionPanel.value = false
            keyboardHeight.value = 0f
        }
    }
    
    /**
     * 切换扩展面板（点击加号按钮）
     * - 如果扩展面板已显示 -> 收起扩展面板
     * - 如果扩展面板未显示 -> 显示扩展面板
     * 注意：不影响输入类型（语音/文本）
     */
    fun toggleExtensionPanel() {
        showExtensionPanel.value = !showExtensionPanel.value
    }
    
    // ==================== 其他方法 ====================
    
    // 清空输入
    fun clearInput() {
        inputText.value = ""
    }
    
    // 设置输入文本
    fun setInputText(text: String) {
        inputText.value = text
    }
    
    // 开始生成
    fun startGenerating() {
        isGenerating.value = true
    }
    
    // 停止生成
    fun stopGenerating() {
        isGenerating.value = false
    }
}

/**
 * 创建并记住底部输入栏状态
 */
@Composable
fun rememberChatBottomBarState(
    initialText: String = "",
    initialVoiceMode: Boolean = false,
    initialShowExtPanel: Boolean = false
): ChatBottomBarState {
    // 根据 initialVoiceMode 确定输入类型
    val inputType = if (initialVoiceMode) BottomBarInputType.VOICE else BottomBarInputType.TEXT
    return remember {
        ChatBottomBarState(initialText, inputType, initialShowExtPanel)
    }
}

// ==================== 组件实现 ====================

/**
 * 通用聊天底部输入栏 - 还原 QQAIBiz 布局
 * 
 * @param state 底部栏状态
 * @param config 底部栏配置
 * @param bottomSafeArea 底部安全区域
 * @param onSend 发送消息回调
 * @param onStop 停止生成回调
 * @param onVoiceClick 语音按钮点击回调
 * @param onExtensionClick 扩展按钮点击回调
 * @param onExpandClick 展开按钮点击回调
 * @param onKeyboardHeightChange 键盘高度变化回调，用于外部处理键盘附着效果
 * @param modifier 修饰符
 * @param voiceInputState 语音输入状态（可选，用于自定义语音输入）
 * @param onVoiceRecordStart 语音录音开始回调
 * @param onVoiceRecordEnd 语音录音结束回调（shouldSend: 是否发送，text: 识别的文字）
 * @param extensionPanelContent 扩展面板自定义内容
 */
@Composable
fun ChatBottomBar(
    state: ChatBottomBarState,
    config: ChatBottomBarConfig = ChatBottomBarConfig(),
    bottomSafeArea: Dp = 0f.dp,
    onSend: (String) -> Unit = {},
    onStop: () -> Unit = {},
    onVoiceClick: () -> Unit = {},
    onExtensionClick: () -> Unit = {},
    onExpandClick: () -> Unit = {},
    onKeyboardHeightChange: (KeyboardParams) -> Unit = {},
    modifier: Modifier = Modifier,
    voiceInputState: VoiceInputState? = null,
    onVoiceRecordStart: () -> Unit = {},
    onVoiceRecordEnd: (shouldSend: Boolean, text: String) -> Unit = { _, _ -> },
    extensionPanelContent: @Composable (() -> Unit)? = null
) {
    // 如果未传入语音输入状态，则创建一个内部状态
    val internalVoiceState = rememberVoiceInputState()
    val voiceState = voiceInputState ?: internalVoiceState
    
    // 输入框焦点控制 - 移到外层以便在扩展面板切换时使用
    val focusRequester = remember { FocusRequester() }
    
    // FocusManager 用于收起键盘（清除焦点）
    val focusManager = LocalFocusManager.current
    
    // 键盘高度变化处理回调 - 需要应用在 TextField 上
    val handleKeyboardHeightChange: (KeyboardParams) -> Unit = { params ->
        state.keyboardHeight.value = params.height
        // 键盘弹出时，收起扩展面板（扩展面板和键盘互斥）
        // 注意：不改变输入类型（语音/文本），因为它们是独立的
        if (params.height > 0f && state.showExtensionPanel.value) {
            state.showExtensionPanel.value = false
        }
        onKeyboardHeightChange(params)
    }
    
    // 监听输入类型和扩展面板状态变化 - 处理键盘和焦点状态
    // 互斥关系：
    // - 语音/文本输入 互斥
    // - 扩展面板/键盘 互斥
    LaunchedEffect(state.inputType.value, state.showExtensionPanel.value) {
        when {
            state.showExtensionPanel.value -> {
                // 扩展面板显示时 - 清除焦点，收起键盘
                focusManager.clearFocus()
            }
            state.inputType.value == BottomBarInputType.VOICE -> {
                // 语音输入模式 - 清除焦点，收起键盘
                focusManager.clearFocus()
            }
            state.inputType.value == BottomBarInputType.TEXT -> {
                // 文本输入模式 - 延迟一帧等待 BasicTextField 渲染完成后再请求焦点
                // 从语音模式切换回文本模式时，BasicTextField 刚被重新挂载，
                // 需要等待其 focusRequester 附着到节点后再调用 requestFocus
                kotlinx.coroutines.delay(50)
                focusRequester.requestFocus()
            }
        }
    }
    
    // 外层容器 - 使用 Box 叠加录音视图
    Box(modifier = modifier) {
        // 底部输入栏 - 安全距离在背景内部
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(
                    config.backgroundColor,
                    RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
                .clickable { /* 拦截点击事件 */ }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(
                        start = config.horizontalPadding,
                        top = config.topPadding,
                        end = config.horizontalPadding,
                        bottom = config.bottomPadding
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 左侧：语音按钮
                if (config.enableVoiceInput && config.voiceButton != null) {
                    BottomVoiceIcon(
                        state = state,
                        config = config,
                        onVoiceClick = {
                            // 切换语音/文本模式（状态互斥由 toggleVoiceMode 内部处理）
                            state.toggleVoiceMode()
                            onVoiceClick()
                        }
                    )
                }
                
                // 中间：输入框区域
                // 语音模式：显示"按住 说话"按钮
                // 文本模式：显示输入框
                // 注意：扩展面板显示时也根据 inputType 决定显示输入框还是语音按钮
                
                // 使用 inputType 检查是否是语音模式（语音/文本互斥）
                val isVoiceMode = state.inputType.value == BottomBarInputType.VOICE
                
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier
                        .weight(1f)
                        // 语音模式和文本模式使用相同的最小高度
                        .defaultMinSize(minHeight = TEXT_INPUT_BG_MIN_HEIGHT)
                        .background(config.inputBackgroundColor)
                ) {
                    if (isVoiceMode) {
                        // 语音模式：显示"按住 说话"按钮
                        VoiceInputStyleLayout(
                            state = voiceState,
                            onPressDown = {
                                onVoiceRecordStart()
                            },
                            onPressUp = { shouldSend ->
                                val recognizedText = voiceState.currentAudioText.value
                                onVoiceRecordEnd(shouldSend, recognizedText)
                            }
                        )
                    } else {
                        // 文本模式：显示输入框
                        val colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent,
                            focusedContainerColor = Color.Transparent,
                            cursorColor = config.cursorColor,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent
                        )
                        val interactionSource = remember { MutableInteractionSource() }
                        
                        BasicTextField(
                            value = state.inputText.value,
                            onValueChange = { newValue ->
                                if (config.maxInputLength == 0 && newValue.isNotEmpty()) {
                                    state.inputText.value = ""
                                    return@BasicTextField
                                }
                                state.inputText.value = newValue
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .defaultMinSize(minHeight = TEXT_INPUT_BG_MIN_HEIGHT)
                                .wrapContentHeight()
                                .heightIn(max = TEXT_INPUT_BG_MAX_HEIGHT)
                                .focusRequester(focusRequester)
                                .keyboardHeightChange(handleKeyboardHeightChange),
                            textStyle = TextStyle(
                                color = config.textColor,
                                fontSize = TEXT_INPUT_FONT_SIZE.sp,
                                lineHeight = (TEXT_INPUT_FONT_SIZE * TEXT_INPUT_LINE_HEIGHT_MULTIPLIER).sp
                            ),
                            singleLine = false,
                            interactionSource = interactionSource,
                            cursorBrush = SolidColor(config.cursorColor),
                            decorationBox = @OptIn(ExperimentalMaterial3Api::class) @Composable { innerTextField ->
                                TextFieldDefaults.DecorationBox(
                                    value = state.inputText.value,
                                    innerTextField = innerTextField,
                                    enabled = true,
                                    singleLine = false,
                                    visualTransformation = VisualTransformation.None,
                                    interactionSource = interactionSource,
                                    placeholder = {
                                        Text(
                                            text = config.placeholder,
                                            fontSize = TEXT_INPUT_FONT_SIZE.sp,
                                            maxLines = 1,
                                            color = config.placeholderColor,
                                        )
                                    },
                                    colors = colors,
                                    contentPadding = PaddingValues(0.dp),
                                )
                            }
                        )
                    }
                }
                
                // 右侧按钮区域
                RightButtonsArea(
                    state = state,
                    config = config,
                    focusRequester = focusRequester,
                    onSend = {
                        val text = state.inputText.value
                        state.clearInput()
                        onSend(text)
                    },
                    onStop = {
                        state.stopGenerating()
                        onStop()
                    },
                    onExtensionClick = {
                        // 切换扩展面板（状态互斥由 toggleExtensionPanel 内部处理）
                        // LaunchedEffect 会根据 inputMode 变化自动处理键盘焦点
                        state.toggleExtensionPanel()
                        onExtensionClick()
                    }
                )
            }
            
            // 扩展面板 - 优先使用自定义内容，否则显示默认面板
            // 显示条件：扩展面板显示 且 键盘未弹出（扩展面板和键盘互斥）
            val showExtPanel = state.showExtensionPanel.value && state.keyboardHeight.value <= 0f
            if (showExtPanel) {
                if (extensionPanelContent != null) {
                    extensionPanelContent()
                } else {
                    // 默认扩展面板
                    ExtensionPanel(
                        config = defaultExtensionPanelConfig(config.isDarkMode),
                        onItemClick = { itemType ->
                            // 面板项点击回调 - 具体逻辑由外部实现
                            // 这里仅关闭面板
                        }
                    )
                }
            }
            
            // 底部安全距离 - 参考 QQAIBiz AppBottom，一直保持显示
            // 偏移计算会根据键盘高度正确处理
            if (bottomSafeArea > 0.dp) {
                Spacer(modifier = Modifier.height(bottomSafeArea))
            }
        }
        
        // 语音录制视图 - 覆盖在底部栏上方
        if (voiceState.isShowRecordView.value) {
            VoiceRecordView(state = voiceState)
        }
    }
}

/**
 * 语音按钮图标
 */
@OptIn(InternalResourceApi::class)
@Composable
private fun RowScope.BottomVoiceIcon(
    state: ChatBottomBarState,
    config: ChatBottomBarConfig,
    onVoiceClick: () -> Unit
) {
    val voiceButton = config.voiceButton ?: return
    val isEnabled = state.voiceIconEnabled.value && voiceButton.enabled
    
    // 使用 inputType 检查是否是语音模式（语音/文本互斥）
    val isVoiceMode = state.inputType.value == BottomBarInputType.VOICE
    
    // 根据语音模式选择图标 - 直接使用 CDN 链接
    val iconUrl = if (isVoiceMode) {
        voiceButton.activeIcon ?: voiceButton.normalIcon
    } else {
        voiceButton.normalIcon
    }
    
    // CDN 图片直接使用 URL
    val drawable = DrawableResource(iconUrl)
    Image(
        painter = painterResource(drawable),
        contentDescription = if (isVoiceMode) "切换键盘" else "语音输入",
        modifier = Modifier
            .size(config.iconSize)
            .alpha(if (isEnabled) 1f else 0.6f)
            .clickable(enabled = isEnabled) { onVoiceClick() }
    )
    Spacer(modifier = Modifier.width(config.iconSpacing))
}

/**
 * 右侧按钮区域 - 与 QQAIBiz 布局一致：扩展按钮 + 发送按钮/停止按钮
 */
@Composable
private fun RowScope.RightButtonsArea(
    state: ChatBottomBarState,
    config: ChatBottomBarConfig,
    focusRequester: FocusRequester,
    onSend: () -> Unit,
    onStop: () -> Unit,
    onExtensionClick: () -> Unit
) {
    // 扩展按钮（加号）- 常驻显示
    if (config.enableExtension && config.extensionButton != null) {
        BottomExtButton(
            state = state,
            config = config,
            onClick = onExtensionClick
        )
    }
    
    // 流式状态显示停止按钮
    if (state.isGenerating.value && config.stopButton != null) {
        BottomStopButton(
            config = config,
            onClick = onStop
        )
    } else if (config.sendButton != null) {
        // 发送按钮 - 只在文本输入模式且有文本内容时显示
        // 使用 inputType 检查是否是语音模式
        val isVoiceMode = state.inputType.value == BottomBarInputType.VOICE
        val showSendButton = !isVoiceMode && state.inputText.value.isNotBlank()
        if (showSendButton) {
            BottomSendButton(
                state = state,
                config = config,
                onClick = onSend
            )
        }
    }
}

/**
 * 扩展按钮（加号）- 使用 CDN 图片
 */
@OptIn(InternalResourceApi::class)
@Composable
private fun RowScope.BottomExtButton(
    state: ChatBottomBarState,
    config: ChatBottomBarConfig,
    onClick: () -> Unit
) {
    val extButton = config.extensionButton ?: return
    
    Spacer(modifier = Modifier.width(config.iconSpacing))
    
    // 直接使用 inputMode 检查当前模式
    val isExtensionMode = state.inputMode.value == BottomBarInputMode.EXTENSION
    
    // 旋转动画
    val rotationAngle = animateFloatAsState(
        targetValue = if (isExtensionMode) 45f else 0f,
        animationSpec = tween(durationMillis = 300)
    )
    
    // 使用 CDN 图片
    val drawable = DrawableResource(extButton.normalIcon)
    Image(
        painter = painterResource(drawable),
        contentDescription = "更多",
        modifier = Modifier
            .size(config.iconSize)
            .rotate(rotationAngle.value)
            .clickable { onClick() }
    )
}

/**
 * 停止生成按钮 - 使用 CDN 图片
 */
@OptIn(InternalResourceApi::class)
@Composable
private fun RowScope.BottomStopButton(
    config: ChatBottomBarConfig,
    onClick: () -> Unit
) {
    val stopButton = config.stopButton ?: return
    
    Spacer(modifier = Modifier.width(config.iconSpacing))
    
    // 使用 CDN 图片
    val drawable = DrawableResource(stopButton.normalIcon)
    Image(
        painter = painterResource(drawable),
        contentDescription = "停止生成",
        modifier = Modifier
            .size(config.iconSize)
            .clickable { onClick() }
    )
}

/**
 * 发送按钮 - 使用 CDN 图片
 */
@OptIn(InternalResourceApi::class)
@Composable
private fun RowScope.BottomSendButton(
    state: ChatBottomBarState,
    config: ChatBottomBarConfig,
    onClick: () -> Unit
) {
    val sendButton = config.sendButton ?: return
    
    Spacer(modifier = Modifier.width(config.iconSpacing))
    
    val canSend = state.canSend
    
    // 使用 CDN 图片
    val drawable = DrawableResource(sendButton.normalIcon)
    Image(
        painter = painterResource(drawable),
        contentDescription = "发送",
        modifier = Modifier
            .size(config.iconSize)
            .alpha(if (canSend) 1f else 0.6f)
            .clickable(enabled = canSend) { onClick() }
    )
}
