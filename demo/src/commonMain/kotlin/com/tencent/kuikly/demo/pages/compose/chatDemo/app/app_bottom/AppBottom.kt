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
import com.tencent.kuikly.demo.pages.compose.chatDemo.configs.*
import com.tencent.kuikly.demo.pages.compose.chatDemo.widgets.*

/**
 * 底部输入区域组件
 */
@Composable
fun AppBottom(
    appState: ChatDemoAppState,
    uiConfig: AppUIConfig = appState.uiConfig,
    animatedBottomOffset: Float,
    onSend: (String) -> Unit,
    onStop: () -> Unit,
    onExtensionPanelItemClick: (ExtensionPanelItemType) -> Unit,
    onVoiceRecordStart: () -> Unit,
    onVoiceRecordEnd: (shouldSend: Boolean, text: String) -> Unit,
    onCapsuleItemClick: (Int, CapsuleItemConfig) -> Unit = { _, _ -> }
) {
    val config = uiConfig.app
    val isHalfViewVisible = appState.halfViewState.isVisible

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(appState.pageViewHeight.dp)
            .offset(y = animatedBottomOffset.dp)
    ) {
        // 胶囊区域
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            // 半浮层蒙层
            if (isHalfViewVisible) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x33000000))
                        .pointerInput(Unit) {
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

            // 胶囊栏
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

            // 半浮层
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

        // 底部输入栏
        ChatBottomBar(
            state = appState.bottomBarState,
            bottomSafeArea = 0.dp,
            config = fullFeatureChatBottomBarConfig(
                pageId = "ChatDemo",
                placeholder = if (isHalfViewVisible) {
                    appState.halfViewState.currentConfig.value?.placeholder ?: config.defaultPlaceholder
                } else {
                    config.defaultPlaceholder
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

        // 底部安全区
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(appState.bottomSafeArea.dp)
                .background(Color.White)
        )
    }
}
