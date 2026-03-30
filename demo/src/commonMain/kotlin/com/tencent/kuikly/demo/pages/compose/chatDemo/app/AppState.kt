package com.tencent.kuikly.demo.pages.compose.chatDemo.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.tencent.kuikly.demo.pages.compose.chatDemo.configs.*
import com.tencent.kuikly.demo.pages.compose.chatDemo.widgets.*

/**
 * ChatDemo App 状态管理类
 * 
 * 参考 QQAIBiz 的 ViewModel 设计，集中管理所有状态
 */
class ChatDemoAppState(
    // 页面尺寸信息
    val pageViewHeight: Float,
    val pageViewWidth: Float,
    val statusBarHeight: Float,
    val bottomSafeArea: Float
) {
    // ==================== 输入相关状态 ====================
    
    /** 输入文本 */
    var inputText by mutableStateOf("")
    
    /** 聊天消息列表 */
    val chatList = mutableStateListOf<String>()
    
    /** 底部输入栏状态 */
    val bottomBarState = ChatBottomBarState()
    
    /** 语音输入状态 */
    val voiceInputState = VoiceInputState()
    
    // ==================== 胶囊和半浮层状态 ====================
    
    /** 半浮层状态 */
    val halfViewState = CapsuleHalfViewState()
    
    /** 胶囊栏配置 */
    val capsuleBarConfig = defaultCapsuleBarConfig()
    
    /** AI 写作半浮层配置 */
    val aiWriteHalfViewConfig = createAIWriteHalfViewConfig()
    
    /** AI 绘图半浮层配置 */
    val aiDrawHalfViewConfig = createAIDrawHalfViewConfig()
    
    // ==================== 键盘和面板状态 ====================
    
    /** 键盘高度 */
    var keyboardHeight by mutableStateOf(0f)
    
    /** 扩展面板高度 */
    var extPanelHeight by mutableStateOf(0f)
    
    /** 键盘动画时长（毫秒） */
    var keyboardAnimDuration by mutableStateOf(250)
    
    // ==================== 计算属性 ====================
    
    /** 底部栏默认高度 */
    private val bottomBarDefaultHeight = LAYOUT_CHAT_BOTTOM_DEFAULT_HEIGHT
    
    /** 内容区域底部边距 = 底部栏高度 + 底部安全区（固定值） */
    val marginBottom: Float
        get() = bottomBarDefaultHeight + bottomSafeArea
    
    /** 内容区域高度 = 页面高度 - marginBottom（固定值，不随键盘变化） */
    val contentHeight: Float
        get() = pageViewHeight - marginBottom
    
    /** 键盘弹出时的偏移量 */
    private val offsetForKeyboard: Float
        get() = if (keyboardHeight > 0f) {
            -keyboardHeight + (bottomBarDefaultHeight - bottomSafeArea)
        } else {
            0f
        }
    
    /** 底部栏总偏移 = 仅键盘偏移 */
    val bottomBarOffset: Float
        get() = offsetForKeyboard
    
    /** 图片选择区域高度（只在有选中图片时显示） */
    private val imagePickerHeight: Float
        get() = if (bottomBarState.hasPickedImages) IMAGE_PICKER_HEIGHT else 0f
    
    /** 胶囊栏高度 */
    val capsuleBarHeight: Float
        get() = if (capsuleBarConfig.items.isNotEmpty()) CAPSULE_BAR_DEFAULT_HEIGHT.value else 0f
    
    /** 列表底部 padding - 用于避让键盘或扩展面板 */
    val listBottomPadding: Float
        get() = when {
            keyboardHeight > 0f -> keyboardHeight + imagePickerHeight
            extPanelHeight > 0f -> extPanelHeight + imagePickerHeight
            bottomBarState.hasPickedImages -> IMAGE_PICKER_HEIGHT
            else -> 0f
        }
    
    // ==================== 方法 ====================
    
    /** 更新键盘高度和动画时长 */
    fun updateKeyboardParams(height: Float, duration: Int) {
        keyboardAnimDuration = duration
        keyboardHeight = height
        
        // 键盘弹出时，收起扩展面板（扩展面板和键盘互斥）
        if (height > 0f) {
            extPanelHeight = 0f
            bottomBarState.showExtensionPanel.value = false
        }
    }
}

/**
 * 创建并记住 ChatDemoAppState
 */
@Composable
fun rememberChatDemoAppState(
    pageViewHeight: Float,
    pageViewWidth: Float,
    statusBarHeight: Float,
    bottomSafeArea: Float
): ChatDemoAppState {
    return remember {
        ChatDemoAppState(
            pageViewHeight = pageViewHeight,
            pageViewWidth = pageViewWidth,
            statusBarHeight = statusBarHeight,
            bottomSafeArea = bottomSafeArea
        )
    }
}
