package com.tencent.kuikly.demo.pages.compose.chatDemo.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.tencent.kuikly.compose.animation.core.animateFloatAsState
import com.tencent.kuikly.compose.animation.core.tween
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Spacer
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.offset
import com.tencent.kuikly.compose.foundation.lazy.rememberLazyListState
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.platform.LocalFocusManager
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.core.log.KLog
import com.tencent.kuikly.demo.pages.compose.chatDemo.app.app_bottom.AppBottom
import com.tencent.kuikly.demo.pages.compose.chatDemo.app.app_content.AppContent
import com.tencent.kuikly.demo.pages.compose.chatDemo.app.app_float.AppContentFloat
import com.tencent.kuikly.demo.pages.compose.chatDemo.app.app_top.AppTop
import com.tencent.kuikly.demo.pages.compose.chatDemo.configs.*
import com.tencent.kuikly.demo.pages.compose.chatDemo.widgets.*

/**
 * ChatDemo App 主入口
 * 
 * 参考 QQAIBiz 的 BabyQApp 结构设计，整体布局如下：
 * - Box (根布局，全屏)
 *   - Column (内容区域，固定高度 = 页面高度 - 底部栏高度 - 底部安全区)
 *     - AppTop (顶部区域：状态栏 + 导航栏)
 *     - AppContent (消息内容区域，weight(1f) 填充剩余空间)
 *       - 消息列表 / 欢迎页
 *       - AppContentFloat (浮动元素：半浮层遮罩、胶囊栏)
 *   - AppBottom (底部输入区域，使用 transform 实现键盘附着动画)
 * 
 * 注意事项：
 * 1. 布局结构尽量减少模块之间的耦合关系，强耦合的才包在一起
 * 2. 内容区域高度固定，不随键盘变化，键盘弹出时通过 padding 避让
 * 3. 底部栏使用全屏高度容器 + transform 偏移，确保动画流畅
 */
@Composable
fun ChatDemoApp(
    appState: ChatDemoAppState,
    onBack: () -> Unit = {},
    onSend: (String) -> Unit = {},
    onStop: () -> Unit = {},
    onExtensionPanelItemClick: (ExtensionPanelItemType) -> Unit = {},
    onVoiceRecordStart: () -> Unit = {},
    onVoiceRecordEnd: (shouldSend: Boolean, text: String) -> Unit = { _, _ -> },
    onCapsuleItemClick: (Int, CapsuleItemConfig) -> Unit = { _, _ -> }
) {
    // FocusManager 用于收起键盘
    val focusManager = LocalFocusManager.current
    
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
    
    // 同步输入文本状态
    LaunchedEffect(appState.inputText) {
        if (appState.bottomBarState.inputText.value != appState.inputText) {
            appState.bottomBarState.setInputText(appState.inputText)
        }
    }
    LaunchedEffect(appState.bottomBarState.inputText.value) {
        if (appState.inputText != appState.bottomBarState.inputText.value) {
            appState.inputText = appState.bottomBarState.inputText.value
        }
    }
    
    // 监听扩展面板状态变化，同步更新扩展面板高度
    LaunchedEffect(appState.bottomBarState.showExtensionPanel.value) {
        if (appState.bottomBarState.showExtensionPanel.value) {
            appState.extPanelHeight = LAYOUT_EXT_BOTTOM_HEIGHT
        } else {
            appState.extPanelHeight = 0f
        }
    }
    
    // 语音模式变化时重置键盘高度
    LaunchedEffect(appState.bottomBarState.inputType.value) {
        if (appState.bottomBarState.inputType.value == BottomBarInputType.VOICE) {
            appState.keyboardHeight = 0f
        }
    }
    
    // 底部栏位移动画
    val animatedBottomOffset by animateFloatAsState(
        targetValue = appState.bottomBarOffset,
        animationSpec = tween(durationMillis = appState.keyboardAnimDuration)
    )
    
    // 列表底部 padding 动画
    val animatedListPadding by animateFloatAsState(
        targetValue = appState.listBottomPadding,
        animationSpec = tween(durationMillis = appState.keyboardAnimDuration)
    )

    // 根布局 - 整个页面高度固定
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(appState.pageViewHeight.dp)
            .background(Color(0xFFF4F4FE))
    ) {
        // 主内容区域 - 高度固定，不随键盘变化
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(appState.contentHeight.dp)
        ) {
            // 顶部区域：状态栏 + 导航栏
            AppTop(
                statusBarHeight = appState.statusBarHeight,
                onBack = onBack
            )

            // 消息内容区域 - 使用 weight 填充剩余空间
            AppContent(
                chatList = appState.chatList,
                listState = listState,
                listBottomPadding = animatedListPadding,
                pageViewWidth = appState.pageViewWidth,
                halfViewState = appState.halfViewState,
                capsuleBarConfig = appState.capsuleBarConfig,
                aiWriteHalfViewConfig = appState.aiWriteHalfViewConfig,
                aiDrawHalfViewConfig = appState.aiDrawHalfViewConfig,
                onInputTextChange = { appState.inputText = it },
                onCapsuleItemClick = onCapsuleItemClick,
                modifier = Modifier.weight(1f)
            )
        }

        // 底部输入栏容器 - 使用全屏高度容器 + transform 偏移
        AppBottom(
            appState = appState,
            animatedBottomOffset = animatedBottomOffset,
            onSend = onSend,
            onStop = onStop,
            onExtensionPanelItemClick = onExtensionPanelItemClick,
            onVoiceRecordStart = onVoiceRecordStart,
            onVoiceRecordEnd = onVoiceRecordEnd
        )
    }
}

// ==================== 常量定义 ====================

/** 扩展面板高度 */
const val LAYOUT_EXT_BOTTOM_HEIGHT = 286f

/** 图片选择区域高度 */
const val IMAGE_PICKER_HEIGHT = 117f

/** 底部栏默认高度（包括输入框高度 + padding） */
const val LAYOUT_CHAT_BOTTOM_DEFAULT_HEIGHT = 56f

/** 默认占位符文本 */
const val DEFAULT_PLACEHOLDER = "说点什么..."
