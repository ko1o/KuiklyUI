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
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.text.TextStyle
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.tencent.kuikly.core.views.KeyboardParams
import com.tencent.kuikly.demo.pages.compose.chatDemo.configs.*

// ==================== 状态管理 ====================

/**
 * 底部输入栏状态
 */
class ChatBottomBarState(
    initialText: String = "",
    initialVoiceMode: Boolean = false
) {
    // 输入文本
    val inputText: MutableState<String> = mutableStateOf(initialText)
    
    // 是否处于语音输入模式
    val isVoiceMode: MutableState<Boolean> = mutableStateOf(initialVoiceMode)
    
    // 是否显示扩展面板
    val showExtensionPanel: MutableState<Boolean> = mutableStateOf(false)
    
    // 是否正在生成（流式响应）
    val isGenerating: MutableState<Boolean> = mutableStateOf(false)
    
    // 键盘高度
    val keyboardHeight: MutableState<Float> = mutableStateOf(0f)
    
    // 语音按钮是否启用
    val voiceIconEnabled: MutableState<Boolean> = mutableStateOf(true)
    
    // 是否可发送
    val canSend: Boolean
        get() = inputText.value.isNotBlank() && !isGenerating.value
    
    // 切换语音/文本模式
    fun toggleVoiceMode() {
        isVoiceMode.value = !isVoiceMode.value
        // 切换到语音模式时重置键盘高度
        if (isVoiceMode.value) {
            keyboardHeight.value = 0f
        }
    }
    
    // 切换扩展面板
    fun toggleExtensionPanel() {
        showExtensionPanel.value = !showExtensionPanel.value
    }
    
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
    initialVoiceMode: Boolean = false
): ChatBottomBarState {
    return remember {
        ChatBottomBarState(initialText, initialVoiceMode)
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
    
    // 键盘高度变化处理回调 - 需要应用在 TextField 上
    val handleKeyboardHeightChange: (KeyboardParams) -> Unit = { params ->
        state.keyboardHeight.value = params.height
        onKeyboardHeightChange(params)
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
                            state.toggleVoiceMode()
                            onVoiceClick()
                        }
                    )
                }
                
                // 中间：输入框区域
                // 语音模式：显示"按住 说话"按钮
                // 文本模式：显示输入框
                // 输入框焦点控制
                val focusRequester = remember { FocusRequester() }
                
                // 语音模式切换到文本模式时自动聚焦
                LaunchedEffect(state.isVoiceMode.value) {
                    if (!state.isVoiceMode.value) {
                        focusRequester.requestFocus()
                    }
                }
                
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = config.iconSize)
                        .background(config.inputBackgroundColor)
                ) {
                    if (state.isVoiceMode.value) {
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
                        state.toggleExtensionPanel()
                        onExtensionClick()
                    }
                )
            }
            
            // 扩展面板
            if (state.showExtensionPanel.value && extensionPanelContent != null) {
                extensionPanelContent()
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
    
    // 根据语音模式选择图标 - 直接使用 CDN 链接
    val iconUrl = if (state.isVoiceMode.value) {
        voiceButton.activeIcon ?: voiceButton.normalIcon
    } else {
        voiceButton.normalIcon
    }
    
    // CDN 图片直接使用 URL
    val drawable = DrawableResource(iconUrl)
    Image(
        painter = painterResource(drawable),
        contentDescription = if (state.isVoiceMode.value) "切换键盘" else "语音输入",
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
        val showSendButton = !state.isVoiceMode.value && state.inputText.value.isNotBlank()
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
    
    // 旋转动画
    val rotationAngle = animateFloatAsState(
        targetValue = if (state.showExtensionPanel.value) 45f else 0f,
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
