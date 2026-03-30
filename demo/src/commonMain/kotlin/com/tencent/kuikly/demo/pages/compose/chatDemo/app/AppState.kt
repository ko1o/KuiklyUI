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
    
    /** 底部栏默认高度 - 参考 QQAIBiz: layoutChatBottomDefaultHeight = 72dp */
    val bottomBarDefaultHeight = LAYOUT_CHAT_BOTTOM_DEFAULT_HEIGHT
    
    /**
     * 内容区域底部边距 = 底部栏高度 + 底部安全区（固定值）
     * 
     * 参考 QQAIBiz App.kt 第 38-39 行:
     *   marginBottom = layoutChatBottomDefaultHeight + offsetForNavBar
     */
    val marginBottom: Float
        get() = bottomBarDefaultHeight + bottomSafeArea
    
    /**
     * 内容区域高度 = 页面高度 - marginBottom（固定值，不随键盘变化）
     * 
     * 参考 QQAIBiz App.kt 第 41-46 行:
     *   contentHeight = appHeight - marginBottom
     * 
     * 注意：此高度不包含胶囊栏避让。胶囊栏的避让通过 listBottomPadding 实现
     */
    val contentHeight: Float
        get() = pageViewHeight - marginBottom
    
    /**
     * 键盘弹出时的偏移量
     * 
     * 参考 QQAIBiz BabyQBaseViewModel 第 331-336 行:
     *   offsetForKeyboard = -(keyboardHeight - offsetForNavBar)  // 键盘展开时
     *   offsetForKeyboard = 0                                    // 键盘收起时
     */
    private val offsetForKeyboard: Float
        get() = if (keyboardHeight > 0f) {
            -(keyboardHeight - bottomSafeArea)
        } else {
            0f
        }
    
    /**
     * 底部栏总偏移
     * 
     * 参考 QQAIBiz BabyQBaseViewModel 第 349 行:
     *   bottomBarMove = offsetForKeyboard + offsetForExtBottom
     */
    val bottomBarOffset: Float
        get() = offsetForKeyboard
    
    /** 图片选择区域高度（只在有选中图片时显示） */
    private val imagePickerHeight: Float
        get() = if (bottomBarState.hasPickedImages) IMAGE_PICKER_HEIGHT else 0f
    
    /**
     * 胶囊栏高度
     * 
     * 参考 QQAIBiz: capsuleHeight = layoutCapsuleBarDefaultHeight = 84dp
     */
    val capsuleBarHeight: Float
        get() = if (capsuleBarConfig.items.isNotEmpty()) CAPSULE_BAR_DEFAULT_HEIGHT.value else 0f
    
    /**
     * 胶囊栏区域总高度（渐变 + 胶囊栏 + 间距）
     * 
     * 参考 QQAIBiz ChatCapsuleBar:
     * - Box 120dp (包含渐变图片 121dp + 胶囊栏 84dp)
     * - 胶囊栏与输入框间距 12dp
     */
    private val capsuleAreaTotalHeight: Float
        get() = if (capsuleBarConfig.items.isNotEmpty()) {
            CAPSULE_BAR_DEFAULT_HEIGHT.value // 胶囊栏高度 84dp（内部 contentPadding 已含底部间距）
        } else {
            0f
        }
    
    /**
     * 列表底部 padding - 用于避让胶囊栏、键盘或扩展面板
     * 
     * 参考 QQAIBiz AppContent FootView:
     *   FootView 高度 = 8.dp + (-listMove.value)
     *   listMove.value = offsetForKeyboardAndPadding + offsetForExtBottom + offsetForSendArea
     *   offsetForSendArea -= capsuleHeight (= -84dp)
     * 
     * 胶囊栏始终需要避让（capsuleAreaTotalHeight）
     * 键盘弹出时额外加上键盘高度
     */
    val listBottomPadding: Float
        get() {
            val basePadding = capsuleAreaTotalHeight
            return when {
                keyboardHeight > 0f -> basePadding + keyboardHeight + imagePickerHeight
                extPanelHeight > 0f -> basePadding + extPanelHeight + imagePickerHeight
                bottomBarState.hasPickedImages -> basePadding + IMAGE_PICKER_HEIGHT
                else -> basePadding
            }
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
    
    // ==================== 半浮层选中内容拼接逻辑（参考 QQAIBiz BabyQCapsuleViewModel） ====================
    
    /**
     * 重新生成带选中条件的输入文本
     * 
     * 参考 QQAIBiz BabyQCapsuleViewModel.reGenerateInputText() 第 228-292 行：
     * - 如果半浮层显示，则将用户输入与选中的条件进行拼接
     * - 如果半浮层不显示，则直接返回原始输入
     * 
     * @param input 用户输入的原始文本
     * @return 拼接了选中条件后的完整文本
     */
    fun reGenerateInputText(input: String): String {
        // 如果半浮层正在显示，使用当前配置的拼接逻辑
        val currentConfig = halfViewState.currentConfig.value ?: return input
        return currentConfig.reGenerateInputText(input)
    }
    
    /**
     * 获取当前半浮层场景
     * 
     * 参考 QQAIBiz BabyQCapsuleViewModel.currentScene()
     */
    fun currentHalfViewScene(): HalfViewScene {
        val config = halfViewState.currentConfig.value ?: return HalfViewScene.NONE
        return when (config.id) {
            "ai_draw" -> HalfViewScene.AI_DRAW
            "ai_write" -> HalfViewScene.AI_WRITE
            else -> HalfViewScene.NONE
        }
    }
    
    /**
     * 发送消息后的清理操作
     * 
     * 参考 QQAIBiz BabyQBottomViewModel.doSendMsgInner() 第 511-516 行：
     * - 重置输入框
     * - 隐藏图片选择器
     * - 重置半浮层选中状态（但不关闭半浮层）
     * 
     * 注意：这里不会关闭半浮层，只是清空选中的条件内容
     */
    fun onSendMessageCleanup() {
        // 清空图片选择
        bottomBarState.imagePickerState.clearImages()
        
        // 重置半浮层选中内容（但不关闭半浮层）
        halfViewState.currentConfig.value?.reset()
    }
    
    /**
     * 关闭半浮层并清空所有状态
     * 
     * 参考 QQAIBiz BabyQCapsuleViewModel.hideCapsuleHalfView()
     */
    fun hideCapsuleHalfView() {
        halfViewState.hide()
    }
}

/**
 * 半浮层场景枚举
 * 
 * 参考 QQAIBiz AICapsuleScene
 */
enum class HalfViewScene {
    /** AI绘画场景 */
    AI_DRAW,
    /** AI写作场景 */
    AI_WRITE,
    /** 无场景 */
    NONE
}

/**
 * 创建并记住 AppState
 */
@Composable
fun rememberAppState(
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
