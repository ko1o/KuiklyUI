package com.tencent.kuikly.demo.pages.compose.chatDemo.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.tencent.kuikly.compose.animation.core.animateFloatAsState
import com.tencent.kuikly.compose.animation.core.tween
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.lazy.rememberLazyListState
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.platform.LocalFocusManager
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.demo.pages.compose.chatDemo.app.app_bottom.AppBottom
import com.tencent.kuikly.demo.pages.compose.chatDemo.app.app_content.AppContent
import com.tencent.kuikly.demo.pages.compose.chatDemo.app.app_top.AppTop
import com.tencent.kuikly.demo.pages.compose.chatDemo.configs.*
import com.tencent.kuikly.demo.pages.compose.chatDemo.widgets.*

/**
 * ChatDemo App 主入口
 *
 * 所有 UI 相关的常量（背景色、高度、占位符等）通过 [AppConfig] 配置。
 * 支持 builder：如果 config.builder 有值，直接调用 builder 渲染整个 App。
 */
@Composable
fun App(
    appState: ChatDemoAppState,
    uiConfig: AppUIConfig = appState.uiConfig,
    onBack: () -> Unit = {},
    onSend: (String) -> Unit = {},
    onStop: () -> Unit = {},
    onExtensionPanelItemClick: (ExtensionPanelItemType) -> Unit = {},
    onVoiceRecordStart: () -> Unit = {},
    onVoiceRecordEnd: (shouldSend: Boolean, text: String) -> Unit = { _, _ -> },
    onCapsuleItemClick: (Int, CapsuleItemConfig) -> Unit = { _, _ -> }
) {
    val config = uiConfig.app
    
    // 如果有整体 builder，直接调用
    if (config.builder != null) {
        config.builder.invoke(appState, onBack, onSend)
        return
    }

    val focusManager = LocalFocusManager.current
    val listState = rememberLazyListState()
    var isProgrammaticScroll by remember { mutableStateOf(false) }

    // 滚动时收起键盘
    LaunchedEffect(listState.isScrollInProgress) {
        if (listState.isScrollInProgress && !isProgrammaticScroll) {
            focusManager.clearFocus()
        }
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

    // 监听扩展面板状态变化
    LaunchedEffect(appState.bottomBarState.showExtensionPanel.value) {
        if (appState.bottomBarState.showExtensionPanel.value) {
            appState.extPanelHeight = config.extPanelHeight
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

    // 根布局
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(appState.pageViewHeight.dp)
            .background(config.backgroundColor)
    ) {
        // 主内容区域
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(appState.contentHeight.dp)
        ) {
            AppTop(
                statusBarHeight = appState.statusBarHeight,
                onBack = onBack,
                config = uiConfig.appTop
            )

            AppContent(
                chatList = appState.chatList,
                listState = listState,
                listBottomPadding = animatedListPadding,
                pageViewWidth = appState.pageViewWidth,
                onInputTextChange = { appState.inputText = it },
                modifier = Modifier.weight(1f)
            )
        }

        // 底部输入栏
        AppBottom(
            appState = appState,
            uiConfig = uiConfig,
            animatedBottomOffset = animatedBottomOffset,
            onSend = onSend,
            onStop = onStop,
            onExtensionPanelItemClick = onExtensionPanelItemClick,
            onVoiceRecordStart = onVoiceRecordStart,
            onVoiceRecordEnd = onVoiceRecordEnd,
            onCapsuleItemClick = onCapsuleItemClick
        )
    }
}
