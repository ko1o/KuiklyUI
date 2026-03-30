package com.tencent.kuikly.demo.pages.compose.chatDemo.configs

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.TextUnit
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp

// ==================== 常量定义（参考 QQAIBiz） ====================

/** 底部栏默认高度 */
val BOTTOM_BAR_DEFAULT_HEIGHT = 72f.dp

/** 输入框字体大小 */
const val TEXT_INPUT_FONT_SIZE = 16f

/** 输入框行高倍数（行高 = 字体大小 * 行高倍数） - 参考 QQAIBiz 1.25f */
const val TEXT_INPUT_LINE_HEIGHT_MULTIPLIER = 1.25f

/** 单行文字高度 */
val SINGLE_LINE_HEIGHT = (TEXT_INPUT_FONT_SIZE * TEXT_INPUT_LINE_HEIGHT_MULTIPLIER).dp

/** 输入框上下内边距 - 参考 QQAIBiz textInputTopPadding */
const val TEXT_INPUT_TOP_PADDING = 4f

/** 输入框背景最小高度 - 参考 QQAIBiz textInputBGMinHeight = 32f + aiFontScaleAddSize */
val TEXT_INPUT_BG_MIN_HEIGHT = 32f.dp

/** 输入框背景最大高度 - 参考 QQAIBiz textInputBGMaxHeight = 152f */
val TEXT_INPUT_BG_MAX_HEIGHT = 152f.dp

// ==================== CDN 图片链接（来自 QQAIBiz AIProductUIToken.CDN） ====================

/**
 * 底部输入栏 CDN 图片配置 - 与 QQAIBiz 保持一致
 */
object BottomBarCDN {
    // 扩展按钮（加号）
    const val EXT_LIGHT = "https://tianquan.gtimg.cn/shoal/vaclient/b9273cac-3188-4a49-a4a1-4e9edacdd5d5.png"
    const val EXT_DARK = "https://tianquan.gtimg.cn/shoal/vaclient/5f67876e-e8f5-4fcf-8516-ffe3441d6623.png"
    
    // 发送按钮 - 注意亮色和暗色的链接不同
    const val SEND_LIGHT = "https://tianquan.gtimg.cn/shoal/vaclient/4b10956c-107c-4b38-b0b4-1d744315f1ac.png"
    const val SEND_DARK = "https://tianquan.gtimg.cn/shoal/vaclient/4d8a4abd-7003-40d6-835e-0678443ab39e.png"
    const val SEND_DISABLE = "https://tianquan.gtimg.cn/shoal/vaclient/4d8a4abd-7003-40d6-835e-0678443ab39e.png"
    
    // 停止按钮
    const val STOP_LIGHT = "https://tianquan.gtimg.cn/shoal/vaclient/3ebcdaa4-a465-46d0-81f8-9c3d58346679.png"
    const val STOP_DARK = "https://tianquan.gtimg.cn/shoal/vaclient/1744c81b-4a43-4802-a8cf-c799a58b99e7.png"
    
    // 语音按钮
    const val VOICE_LIGHT = "https://tianquan.gtimg.cn/shoal/vaclient/10b20155-6742-412c-acb8-ac63b88196ba.png"
    const val VOICE_DARK = "https://tianquan.gtimg.cn/shoal/vaclient/b552c2e3-7425-44c2-a858-4532222a10bc.png"
    
    // 键盘按钮（语音模式下切换回键盘）
    const val KEYBOARD_LIGHT = "https://tianquan.gtimg.cn/shoal/vaclient/e5cee99d-142c-4a7d-acf8-f5bb9d07dfe2.png"
    const val KEYBOARD_DARK = "https://tianquan.gtimg.cn/shoal/vaclient/cc9f4943-2202-4195-85e2-d9b2b8f12d62.png"
    
    // 根据暗黑模式获取图片
    fun ext(isDark: Boolean = false) = if (isDark) EXT_DARK else EXT_LIGHT
    fun send(isDark: Boolean = false) = if (isDark) SEND_DARK else SEND_LIGHT
    fun stop(isDark: Boolean = false) = if (isDark) STOP_DARK else STOP_LIGHT
    fun voice(isDark: Boolean = false) = if (isDark) VOICE_DARK else VOICE_LIGHT
    fun keyboard(isDark: Boolean = false) = if (isDark) KEYBOARD_DARK else KEYBOARD_LIGHT
}

// ==================== 配置数据结构 ====================

/**
 * 底部输入栏按钮类型
 */
enum class BottomBarButtonType {
    VOICE,      // 语音按钮
    EXTENSION,  // 扩展/加号按钮
    SEND,       // 发送按钮
    STOP,       // 停止生成按钮
    EXPAND      // 展开全屏输入按钮
}

/**
 * 底部输入栏按钮配置
 */
data class BottomBarButtonConfig(
    val type: BottomBarButtonType,
    val normalIcon: String,
    val activeIcon: String? = null,
    val disabledIcon: String? = null,
    val visible: Boolean = true,
    val enabled: Boolean = true
)

/**
 * 底部输入栏整体配置
 */
data class ChatBottomBarConfig(
    // 布局配置 - 与 QQAIBiz 保持一致
    // 参考 QQAIBiz: padding(start = 24f, top = 16f, end = 24f, bottom = 16f)
    val horizontalPadding: Dp = 16.dp,  // 减少左右间距
    val topPadding: Dp = 12.dp,         // 适当的顶部间距
    val bottomPadding: Dp = 12.dp,      // 适当的底部间距
    val iconSize: Dp = 32.dp,
    val iconSpacing: Dp = 8.dp,         // 减少图标和输入框的间距
    val inputBorderRadius: Dp = 18.dp,  // 输入框圆角
    
    // 颜色配置 - 与 QQAIBiz AIProductUIToken.Color 保持一致
    val backgroundColor: Color = Color.White,             // 整个底部栏白色背景
    val inputBackgroundColor: Color = Color.Red,  // 输入框背景透明
    val placeholderColor: Color = Color(0xFF999999),      // text_tertiary
    val textColor: Color = Color(0xFF333333),             // text_primary
    val cursorColor: Color = Color(0xFF505DE5),           // brand_standard
    
    // 文本配置
    val placeholder: String = "输入消息...",
    val maxInputLength: Int = 2000,
    /** 输入框字体大小 */
    val inputFontSize: TextUnit = TEXT_INPUT_FONT_SIZE.sp,
    /** 输入框行高倍数 */
    val inputLineHeightMultiplier: Float = TEXT_INPUT_LINE_HEIGHT_MULTIPLIER,
    /** 占位符字体大小 */
    val placeholderFontSize: TextUnit = TEXT_INPUT_FONT_SIZE.sp,
    
    // 按钮配置
    val voiceButton: BottomBarButtonConfig? = null,
    val extensionButton: BottomBarButtonConfig? = null,
    val sendButton: BottomBarButtonConfig? = null,
    val stopButton: BottomBarButtonConfig? = null,
    
    // 功能开关
    val enableVoiceInput: Boolean = false,
    val enableExtension: Boolean = true,
    val showExpandButton: Boolean = true,
    val expandThreshold: Dp = 60.dp,
    
    // 页面标识
    val pageId: String = "ChatDemo",
    
    // 暗黑模式
    val isDarkMode: Boolean = false,
    
    // ==================== Builder ====================
    /** 输入框 builder - 如果有值，直接调用 builder 来渲染输入框区域 */
    val inputBuilder: (@Composable (text: String, onValueChange: (String) -> Unit) -> Unit)? = null,
    /** 右侧按钮区域 builder - 如果有值，直接调用 builder 来渲染右侧按钮 */
    val rightButtonsBuilder: (@Composable (canSend: Boolean, isGenerating: Boolean, onSend: () -> Unit, onStop: () -> Unit) -> Unit)? = null,
    /** 整体 builder - 如果有值，直接调用 builder 来渲染整个底部栏 */
    val builder: (@Composable (config: ChatBottomBarConfig, onSend: (String) -> Unit, onStop: () -> Unit) -> Unit)? = null
)

// ==================== 便捷构建器 ====================

/**
 * 创建默认的聊天底部栏配置（仅发送按钮，使用 CDN 图片）
 */
fun defaultChatBottomBarConfig(
    pageId: String = "ChatDemo",
    placeholder: String = "输入消息...",
    isDarkMode: Boolean = false
): ChatBottomBarConfig {
    return ChatBottomBarConfig(
        pageId = pageId,
        placeholder = placeholder,
        isDarkMode = isDarkMode,
        backgroundColor = if (isDarkMode) Color(0xFF1A1C1E) else Color.White,
        inputBackgroundColor = Color.Transparent,
        textColor = if (isDarkMode) Color(0xFFFFFFFF) else Color(0xFF333333),
        enableVoiceInput = false,
        enableExtension = false,
        showExpandButton = false,
        sendButton = BottomBarButtonConfig(
            type = BottomBarButtonType.SEND,
            normalIcon = BottomBarCDN.send(isDarkMode)
        )
    )
}

/**
 * 创建带语音和扩展功能的聊天底部栏配置（使用 CDN 图片）
 */
fun fullFeatureChatBottomBarConfig(
    pageId: String = "ChatDemo",
    placeholder: String = "输入消息...",
    isDarkMode: Boolean = false
): ChatBottomBarConfig {
    return ChatBottomBarConfig(
        pageId = pageId,
        placeholder = placeholder,
        isDarkMode = isDarkMode,
        backgroundColor = if (isDarkMode) Color(0xFF1A1C1E) else Color.White,
        inputBackgroundColor = Color.Transparent,
        textColor = if (isDarkMode) Color(0xFFFFFFFF) else Color(0xFF333333),
        enableVoiceInput = true,
        enableExtension = true,
        showExpandButton = true,
        voiceButton = BottomBarButtonConfig(
            type = BottomBarButtonType.VOICE,
            normalIcon = BottomBarCDN.voice(isDarkMode),
            activeIcon = BottomBarCDN.keyboard(isDarkMode)
        ),
        extensionButton = BottomBarButtonConfig(
            type = BottomBarButtonType.EXTENSION,
            normalIcon = BottomBarCDN.ext(isDarkMode)
        ),
        sendButton = BottomBarButtonConfig(
            type = BottomBarButtonType.SEND,
            normalIcon = BottomBarCDN.send(isDarkMode),
            disabledIcon = BottomBarCDN.SEND_DISABLE
        ),
        stopButton = BottomBarButtonConfig(
            type = BottomBarButtonType.STOP,
            normalIcon = BottomBarCDN.stop(isDarkMode)
        )
    )
}

/**
 * 创建带扩展功能的聊天底部栏配置（无语音，使用 CDN 图片）
 */
fun extensionChatBottomBarConfig(
    pageId: String = "ChatDemo",
    placeholder: String = "输入消息...",
    isDarkMode: Boolean = false
): ChatBottomBarConfig {
    return ChatBottomBarConfig(
        pageId = pageId,
        placeholder = placeholder,
        isDarkMode = isDarkMode,
        backgroundColor = if (isDarkMode) Color(0xFF1A1C1E) else Color.White,
        inputBackgroundColor = Color.Transparent,
        textColor = if (isDarkMode) Color(0xFFFFFFFF) else Color(0xFF333333),
        enableVoiceInput = false,
        enableExtension = true,
        showExpandButton = true,
        extensionButton = BottomBarButtonConfig(
            type = BottomBarButtonType.EXTENSION,
            normalIcon = BottomBarCDN.ext(isDarkMode)
        ),
        sendButton = BottomBarButtonConfig(
            type = BottomBarButtonType.SEND,
            normalIcon = BottomBarCDN.send(isDarkMode),
            disabledIcon = BottomBarCDN.SEND_DISABLE
        ),
        stopButton = BottomBarButtonConfig(
            type = BottomBarButtonType.STOP,
            normalIcon = BottomBarCDN.stop(isDarkMode)
        )
    )
}
