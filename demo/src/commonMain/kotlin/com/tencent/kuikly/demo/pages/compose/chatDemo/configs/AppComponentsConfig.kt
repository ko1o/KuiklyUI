package com.tencent.kuikly.demo.pages.compose.chatDemo.configs

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.TextUnit
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp

// ==================== App 主入口配置 ====================

/**
 * App 主入口配置（保留用于布局相关的值）
 */
data class AppConfig(
    /** 页面背景色 */
    val backgroundColor: Color = Color(0xFFF7F7F7),
    /** 扩展面板高度 */
    val extPanelHeight: Float = 286f,
    /** 图片选择区域高度 */
    val imagePickerHeight: Float = 117f,
    /** 底部栏默认高度（包括输入框高度 + padding） */
    val chatBottomDefaultHeight: Float = 56f,
    /** 默认占位符文本 */
    val defaultPlaceholder: String = "说点什么...",
    /** 整体 builder */
    val builder: (@Composable (appState: Any, onBack: () -> Unit, onSend: (String) -> Unit) -> Unit)? = null
)

// ==================== AppUIConfig 总配置（聚合所有子 Config） ====================

/**
 * AppUIConfig - 聚合所有 UI 子配置的总入口
 *
 * 设计理念：
 * 1. 所有 UI 组件的样式配置统一收口到此类
 * 2. ChatDemo 通过 open fun getAppUIConfig() 返回默认配置
 * 3. 其他工程子类重写 getAppUIConfig()，通过 copy 快速改某个属性即可定制
 *
 * 使用示例：
 * ```kotlin
 * // 只改欢迎页标题字号和消息气泡颜色，其他保持默认
 * override fun getAppUIConfig() = AppUIConfig(
 *     welcomeView = WelcomeViewConfig(cardTitleFontSize = 24.sp),
 *     chatMessageItem = ChatMessageItemConfig(userBubbleColor = Color.Blue)
 * )
 *
 * // 或者基于默认值 copy 某个字段
 * override fun getAppUIConfig(): AppUIConfig {
 *     val default = super.getAppUIConfig()
 *     return default.copy(
 *         app = default.app.copy(backgroundColor = Color.Black),
 *         appTop = default.appTop.copy(titleColor = Color.White)
 *     )
 * }
 * ```
 */
data class AppUIConfig(
    /** App 主入口配置（背景色、高度常量、占位符等） */
    val app: AppConfig = AppConfig(),
    /** 顶部导航栏配置 */
    val appTop: AppTopConfig = AppTopConfig(),
    /** 欢迎页配置 */
    val welcomeView: WelcomeViewConfig = WelcomeViewConfig(),
    /** 聊天消息项配置 */
    val chatMessageItem: ChatMessageItemConfig = ChatMessageItemConfig(),
    /** 底部输入栏配置（仅 UI 样式部分，按钮配置通过 fullFeatureChatBottomBarConfig 生成） */
    val chatBottomBar: ChatBottomBarConfig = ChatBottomBarConfig(),
    /** 胶囊栏配置 */
    val capsuleBar: ChatCapsuleBarConfig = defaultCapsuleBarConfig(),
    /** 半浮层 UI 配置 */
    val halfViewUI: CapsuleHalfViewUIConfig = CapsuleHalfViewUIConfig(),
    /** 扩展面板配置 */
    val extensionPanel: ExtensionPanelConfig = ExtensionPanelConfig(),
    /** 语音输入配置 */
    val voiceInput: VoiceInputConfig = VoiceInputConfig(),
    /** 图片选择器配置 */
    val imagePicker: ImagePickerConfig = ImagePickerConfig(),
    /** 弹出菜单配置 */
    val popover: PopoverConfig = PopoverConfig()
)

// ==================== AppTop 配置 ====================

/**
 * 顶部导航栏配置
 *
 * 将状态栏、导航栏相关的 UI 参数全部抽象到此配置中，
 * 其他工程复用时只需修改此配置即可自定义样式。
 */
data class AppTopConfig(
    /** 导航栏高度 */
    val navBarHeight: Dp = 44.dp,
    /** 导航栏水平内边距 */
    val navBarHorizontalPadding: Dp = 12.dp,
    /** 标题文字 */
    val title: String = "AI Chat",
    /** 标题字体大小 */
    val titleFontSize: TextUnit = 17.sp,
    /** 标题颜色 */
    val titleColor: Color = Color.Black,
    /** 返回按钮图标名 */
    val backIcon: String = "ic_back.png",
    /** 返回按钮大小 */
    val backIconSize: Dp = 16.dp,
    /** 右侧占位宽度 */
    val rightPlaceholderWidth: Dp = 20.dp,
    /** 分割线高度 */
    val dividerHeight: Dp = 1.dp,
    /** 分割线颜色 */
    val dividerColor: Color = Color(0xFFE3E3E3),
    /** 页面名称（用于资源加载） */
    val pageName: String = "ChatDemo",
    // ==================== Builder ====================
    /** 整体 builder - 如果有值，直接调用 builder 来渲染整个顶部栏 */
    val builder: (@Composable (statusBarHeight: Float, onBack: () -> Unit) -> Unit)? = null,
    /** 导航栏 builder - 如果有值，直接调用 builder 来渲染导航栏部分 */
    val navBarBuilder: (@Composable (title: String, onBack: () -> Unit) -> Unit)? = null
)

// ==================== WelcomeView 配置 ====================

/**
 * 欢迎页快捷提示卡片数据
 */
data class WelcomePromptBox(
    val title: String,
    val prompt: String,
    val subtitle: String = "",
    val startColor: Color = Color.White,
    val endColor: Color = Color.White
)

/**
 * 欢迎页配置
 */
data class WelcomeViewConfig(
    /** Logo 图标名 */
    val logoIcon: String = "kuikly_logo.png",
    /** Logo 宽度 */
    val logoWidth: Dp = 240.dp,
    /** Logo 高度 */
    val logoHeight: Dp = 70.dp,
    /** Logo 顶部间距 */
    val logoTopSpacing: Dp = 32.dp,
    /** Logo 底部间距 */
    val logoBottomSpacing: Dp = 40.dp,
    /** 卡片间距 */
    val cardSpacing: Dp = 10.dp,
    /** 卡片水平内边距 */
    val cardHorizontalPadding: Dp = 16.dp,
    /** 卡片宽度比例 */
    val cardWidthFraction: Float = 0.92f,
    /** 卡片圆角 */
    val cardCornerRadius: Dp = 16.dp,
    /** 卡片内垂直内边距 */
    val cardVerticalPadding: Dp = 16.dp,
    /** 卡片内水平内边距 */
    val cardInnerHorizontalPadding: Dp = 18.dp,
    /** 卡片标题字体大小 */
    val cardTitleFontSize: TextUnit = 20.sp,
    /** 卡片标题颜色 */
    val cardTitleColor: Color = Color.Black,
    /** 卡片副标题字体大小 */
    val cardSubtitleFontSize: TextUnit = 15.sp,
    /** 卡片副标题颜色透明度 */
    val cardSubtitleAlpha: Float = 0.9f,
    /** 卡片标题副标题间距 */
    val cardTitleSubtitleSpacing: Dp = 6.dp,
    /** 预设快捷提示列表 */
    val promptBoxes: List<WelcomePromptBox> = defaultWelcomePromptBoxes(),
    /** 页面名称 */
    val pageName: String = "ChatDemo",
    // ==================== Builder ====================
    /** 整体 builder - 如果有值，直接调用 builder 来渲染整个欢迎页 */
    val builder: (@Composable (onInputTextChange: (String) -> Unit) -> Unit)? = null,
    /** 卡片 builder - 如果有值，直接调用 builder 来渲染单张卡片 */
    val cardBuilder: (@Composable (box: WelcomePromptBox, onClick: () -> Unit) -> Unit)? = null
)

/** 默认欢迎页快捷提示 */
fun defaultWelcomePromptBoxes(): List<WelcomePromptBox> {
    return listOf(
        WelcomePromptBox(
            title = "\uD83C\uDF93 高考志愿分析",
            prompt = "请帮我分析高考志愿填报方案，结合我的成绩和兴趣给出建议",
            subtitle = "高考之路，有我护航",
            startColor = Color(0xFFCDC4BB)
        ),
        WelcomePromptBox(
            title = "\u26BD 世界杯观赛助手",
            prompt = "分析今天的世界杯战况如何",
            subtitle = "分析比赛战况",
            startColor = Color(0xFFFEE1D3)
        ),
        WelcomePromptBox(
            title = "\u2600\uFE0F 医学健康助手",
            prompt = "请给出健康生活建议",
            subtitle = "专业、科学",
            startColor = Color(0xFFF6BEBD)
        ),
        WelcomePromptBox(
            title = "\uD83C\uDF89 高考送祝福",
            prompt = "请写一段高考祝福语，祝考生金榜题名",
            subtitle = "祝各位考生金榜题名",
            startColor = Color(0xFFCFAAA1)
        ),
        WelcomePromptBox(
            title = "\uD83D\uDCDA 学习计划助手",
            prompt = "帮我制定一个高效的学习计划，提升学习效率",
            subtitle = "科学规划，高效学习",
            startColor = Color(0xFFD4E4F7)
        ),
        WelcomePromptBox(
            title = "\uD83C\uDFA8 创意写作助手",
            prompt = "帮我写一篇富有创意的短文或故事",
            subtitle = "激发灵感，妙笔生花",
            startColor = Color(0xFFE8D5F2)
        )
    )
}

// ==================== ChatMessageItem 配置 ====================

/**
 * 聊天消息项配置
 */
data class ChatMessageItemConfig(
    // ==================== 用户消息气泡 ====================
    /** 用户消息气泡背景色 */
    val userBubbleColor: Color = Color(0xFFE9E9EB),
    /** 用户消息气泡圆角 */
    val userBubbleCornerRadius: Dp = 8.dp,
    /** 用户消息气泡水平内边距 */
    val userBubbleHorizontalPadding: Dp = 10.dp,
    /** 用户消息气泡垂直内边距 */
    val userBubbleVerticalPadding: Dp = 10.dp,
    /** 用户消息字体大小 */
    val userTextFontSize: TextUnit = 14.sp,
    /** 用户消息字体颜色 */
    val userTextColor: Color = Color.Black,
    /** 用户消息底部间距 */
    val userBottomPadding: Dp = 4.dp,
    /** 用户消息右侧间距 */
    val userEndPadding: Dp = 8.dp,
    /** 气泡三角大小 */
    val triangleWidth: Dp = 6.dp,
    val triangleHeight: Dp = 12.dp,

    // ==================== AI 消息 ====================
    /** AI 消息水平内边距 */
    val aiHorizontalPadding: Dp = 24.dp,
    /** AI 消息字体大小 */
    val aiTextFontSize: TextUnit = 14.sp,
    /** AI 消息字体颜色 */
    val aiTextColor: Color = Color.Black,

    // ==================== Builder ====================
    /** 用户消息 builder - 如果有值，直接调用 builder 来渲染用户消息 */
    val userMessageBuilder: (@Composable (message: String, maxWidth: Dp) -> Unit)? = null,
    /** AI 消息 builder - 如果有值，直接调用 builder 来渲染 AI 消息 */
    val aiMessageBuilder: (@Composable (message: String, maxWidth: Dp) -> Unit)? = null
)

// ==================== VoiceInput 配置 ====================

/**
 * 语音输入配置
 */
data class VoiceInputConfig(
    // ==================== 按住说话按钮 ====================
    /** 按住说话文字 */
    val voiceInputText: String = "按住 说话",
    /** 按住说话字体大小 */
    val voiceInputFontSize: TextUnit = 16.sp,
    /** 按住说话字体颜色 */
    val voiceInputTextColor: Color = Color(0xFF1A1C1E),
    /** 按住说话按钮高度 */
    val voiceButtonHeight: Dp = 32.dp,
    /** 取消上移阈值 */
    val cancelThreshold: Float = 100f,

    // ==================== 录音视图 ====================
    /** 松手发送提示文字 */
    val defaultTip: String = "松手发送 上移取消",
    /** 松手取消提示文字 */
    val cancelTip: String = "松手取消",
    /** 默认提示文字 */
    val defaultHint: String = "您好，请说话",
    /** 倒计时后缀 */
    val countdownSuffix: String = "后将停止录音",
    /** 录音超时时间（秒） */
    val recordTimeout: Int = 60,
    /** 提示文字字体大小 */
    val tipFontSize: TextUnit = 14.sp,
    /** 提示文字颜色 - 正常 */
    val tipColorNormal: Color = Color(0xFF999999),
    /** 提示文字颜色 - 取消 */
    val tipColorCancel: Color = Color(0xFFEF5350),
    /** 倒计时字体大小 */
    val countdownFontSize: TextUnit = 16.sp,
    /** 倒计时颜色 */
    val countdownColor: Color = Color(0xFF1A1C1E),
    /** 波形条背景色 - 正常 */
    val waveBgNormal: Color = Color(0xFF1A1C1E),
    /** 波形条背景色 - 取消 */
    val waveBgCancel: Color = Color(0xFFEF5350),
    /** 波形条颜色 */
    val waveColor: Color = Color.White,
    /** 波形条数量 */
    val waveBarCount: Int = 30,
    /** 波形条最大高度 */
    val waveMaxHeight: Dp = 24.dp,
    /** 波形条最小高度 */
    val waveMinHeight: Dp = 4.dp,
    /** 波形条宽度 */
    val waveBarWidth: Dp = 3.dp,
    /** 波形条间距 */
    val waveBarSpacing: Dp = 4.dp,
    /** 波形区域高度 */
    val waveAreaHeight: Dp = 48.dp,
    /** 波形区域圆角 */
    val waveAreaCornerRadius: Dp = 14.dp,
    /** 波形区域水平内边距 */
    val waveAreaHorizontalPadding: Dp = 16.dp,

    // ==================== Builder ====================
    /** 按住说话按钮 builder */
    val voiceButtonBuilder: (@Composable (onPressDown: () -> Unit, onPressUp: (Boolean) -> Unit) -> Unit)? = null,
    /** 录音视图 builder */
    val recordViewBuilder: (@Composable () -> Unit)? = null
)

// ==================== ImagePicker 配置 ====================

/**
 * 图片选择器配置
 */
data class ImagePickerConfig(
    /** 选择区域高度 */
    val pickerHeight: Dp = 120.dp,
    /** 最大选图数量 */
    val maxPickCount: Int = 9,
    /** 图片缩略图大小 */
    val thumbnailSize: Dp = 88.dp,
    /** 图片圆角 */
    val thumbnailCornerRadius: Dp = 12.dp,
    /** 图片容器大小 */
    val containerSize: Dp = 100.dp,
    /** 图片间距 */
    val imageSpacing: Dp = 8.dp,
    /** 列表左右内边距 */
    val listHorizontalPadding: Dp = 16.dp,
    /** 列表顶部内边距 */
    val listTopPadding: Dp = 8.dp,
    /** 删除按钮大小 */
    val deleteButtonSize: Dp = 24.dp,
    /** 删除按钮背景色 */
    val deleteButtonBgColor: Color = Color(0xFF666666),
    /** 删除按钮图标 */
    val deleteButtonText: String = "×",
    /** 删除按钮字体大小 */
    val deleteButtonFontSize: TextUnit = 16.sp,
    /** 添加按钮边框颜色（亮色） */
    val addButtonBorderColorLight: Color = Color(0xFFD0D0D0),
    /** 添加按钮边框颜色（暗色） */
    val addButtonBorderColorDark: Color = Color(0xFF444444),
    /** 添加按钮图标颜色（亮色） */
    val addButtonIconColorLight: Color = Color(0xFF999999),
    /** 添加按钮图标颜色（暗色） */
    val addButtonIconColorDark: Color = Color(0xFFAAAAAA),
    /** 添加按钮文本 */
    val addButtonText: String = "+",
    /** 添加按钮字体大小 */
    val addButtonFontSize: TextUnit = 36.sp,
    /** 添加按钮顶部内边距 */
    val addButtonTopPadding: Dp = 12.dp,
    // ==================== Builder ====================
    /** 整体 builder */
    val builder: (@Composable (images: List<Any>, onAddClick: () -> Unit, onDeleteClick: (Int) -> Unit) -> Unit)? = null
)

// ==================== Popover 配置 ====================

/**
 * Popover 弹出菜单配置
 */
data class PopoverConfig(
    /** 背景色 */
    val backgroundColor: Color = Color.White,
    /** 蒙层颜色 */
    val scrimColor: Color = Color.Transparent,
    /** 圆角 */
    val cornerRadius: Dp = 12.dp,
    /** 阴影 */
    val elevation: Dp = 8.dp,
    /** 距离屏幕边缘的最小间距 */
    val screenEdgePadding: Dp = 12.dp,
    /** 三角形高度 */
    val triangleHeight: Dp = 8.dp,
    /** 动画时长 */
    val animDuration: Int = 200,
    /** 缩放起始值 */
    val scaleStart: Float = 0.1f,
    // ==================== Builder ====================
    /** 整体 builder */
    val builder: (@Composable (isShow: Boolean, onDismiss: () -> Unit, content: @Composable () -> Unit) -> Unit)? = null
)
