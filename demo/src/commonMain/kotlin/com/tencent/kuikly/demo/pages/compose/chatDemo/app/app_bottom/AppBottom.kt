package com.tencent.kuikly.demo.pages.compose.chatDemo.app.app_bottom

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.gestures.awaitEachGesture
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Spacer
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.offset
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.input.pointer.pointerInput
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.core.log.KLog
import com.tencent.kuikly.demo.pages.compose.chatDemo.app.ChatDemoAppState
import com.tencent.kuikly.demo.pages.compose.chatDemo.app.DEFAULT_PLACEHOLDER
import com.tencent.kuikly.demo.pages.compose.chatDemo.configs.*
import com.tencent.kuikly.demo.pages.compose.chatDemo.widgets.*

/**
 * 底部输入区域组件
 * 
 * 严格参考 QQAIBiz AppBottom.kt 的布局结构：
 * Column(height=appHeight, transform=bottomTranslate)
 *   ├── ChatCapsuleBar(weight=1f)  // Box: 胶囊栏 + 半浮层蒙层 + 半浮层
 *   ├── ChatBottomBar              // 输入栏，始终可见
 *   └── Spacer(offsetForNavBar)    // 底部安全区
 * 
 * 半浮层在 ChatCapsuleBar 内部的 Box 中，覆盖胶囊栏区域
 * 输入栏独立于半浮层，始终在底部可见
 */
@Composable
fun AppBottom(
    appState: ChatDemoAppState,
    animatedBottomOffset: Float,
    onSend: (String) -> Unit,
    onStop: () -> Unit,
    onExtensionPanelItemClick: (ExtensionPanelItemType) -> Unit,
    onVoiceRecordStart: () -> Unit,
    onVoiceRecordEnd: (shouldSend: Boolean, text: String) -> Unit,
    onCapsuleItemClick: (Int, CapsuleItemConfig) -> Unit = { _, _ -> }
) {
    val isHalfViewVisible = appState.halfViewState.isVisible
    
    // 参考 QQAIBiz AppBottom: Column(height=appHeight, transform=bottomTranslate)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(appState.pageViewHeight.dp)
            .offset(y = animatedBottomOffset.dp)
    ) {
        // 胶囊区域 - 参考 QQAIBiz: ChatCapsuleBar(appHeight) 使用 weight(1f)
        // 内部是 Box，包含：蒙层、胶囊栏、半浮层
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            // 半浮层蒙层 - 全屏覆盖，点击关闭半浮层，同时拦截所有滑动事件
            // 参考 QQAIBiz CapsuleMask 和 Popover 的 pointerInput 实现
            if (isHalfViewVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x33000000))
                        .pointerInput(Unit) {
                            // 消费所有触摸事件，阻止滑动事件透过蒙层
                            awaitPointerEventScope {
                                while (true) {
                                    val event = awaitPointerEvent()
                                    event.changes.forEach { it.consume() }
                                }
                            }
                        }
                        .clickable { appState.halfViewState.hide() }
                )
            }
            
            // 胶囊栏 - 底部对齐
            // 参考 QQAIBiz CapsuleBar: align(Alignment.BottomCenter)
            ChatCapsuleBar(
                config = appState.capsuleBarConfig,
                onItemClick = { index, item ->
                    KLog.i("AppBottom", "胶囊点击: index=$index, name=${item.name}, id=${item.id}")
                    when (item.id) {
                        "14" -> appState.halfViewState.show(appState.aiWriteHalfViewConfig)
                        "13" -> appState.halfViewState.show(appState.aiDrawHalfViewConfig)
                        else -> onCapsuleItemClick(index, item)
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
            
            // 半浮层 - 覆盖在胶囊栏上方，底部对齐
            // 参考 QQAIBiz CapsuleFloating: 半浮层从底部弹出覆盖胶囊栏
            AnimatedCapsuleHalfView(
                config = appState.halfViewState.currentConfig.value,
                onClose = { appState.halfViewState.hide() },
                onPlaceholderChange = { placeholder ->
                    KLog.i("AppBottom", "半浮层占位符更新: $placeholder")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
            )
        }
        
        // 底部输入栏 - 始终可见，不参与半浮层动画
        // 参考 QQAIBiz AppBottom: ChatBottomBar 独立
        ChatBottomBar(
            state = appState.bottomBarState,
            bottomSafeArea = 0.dp,  // 安全区由下方 Spacer 处理
            config = fullFeatureChatBottomBarConfig(
                pageId = "ChatDemo",
                placeholder = if (isHalfViewVisible) {
                    appState.halfViewState.currentConfig.value?.placeholder ?: DEFAULT_PLACEHOLDER
                } else {
                    DEFAULT_PLACEHOLDER
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .clip(
                    if (isHalfViewVisible) RoundedCornerShape(0.dp)
                    else RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp)
                ),
            voiceInputState = appState.voiceInputState,
            onSend = onSend,
            onStop = onStop,
            onExtensionClick = {},
            onExtensionPanelItemClick = onExtensionPanelItemClick,
            onKeyboardHeightChange = { params ->
                appState.updateKeyboardParams(params.height, params.duration.toInt())
            },
            onVoiceRecordStart = onVoiceRecordStart,
            onVoiceRecordEnd = onVoiceRecordEnd
        )
        
        // 底部安全区 - 参考 QQAIBiz AppBottom: Spacer(height=offsetForNavBar, bg=white)
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(appState.bottomSafeArea.dp)
                .background(Color.White)
        )
    }
}
