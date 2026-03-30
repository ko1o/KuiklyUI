package com.tencent.kuikly.demo.pages.compose.chatDemo.app.app_bottom

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Spacer
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.offset
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.core.base.attr.ImageUri
import com.tencent.kuikly.core.coroutines.GlobalScope
import com.tencent.kuikly.core.coroutines.launch
import com.tencent.kuikly.core.log.KLog
import com.tencent.kuikly.demo.pages.compose.chatDemo.app.ChatDemoAppState
import com.tencent.kuikly.demo.pages.compose.chatDemo.app.DEFAULT_PLACEHOLDER
import com.tencent.kuikly.demo.pages.compose.chatDemo.configs.*
import com.tencent.kuikly.demo.pages.compose.chatDemo.widgets.*
import kotlinx.coroutines.delay

/**
 * 底部输入区域组件
 * 
 * 参考 QQAIBiz 的 AppBottom 结构：
 * - 使用全屏高度容器 + transform 偏移
 * - 确保底部栏内容不会被裁剪
 * - 支持键盘动画附着
 * 
 * 主要包含：
 * - 胶囊栏（已移到 AppContentFloat）
 * - 底部输入栏（ChatBottomBar）
 * - 扩展面板
 */
@Composable
fun AppBottom(
    appState: ChatDemoAppState,
    animatedBottomOffset: Float,
    onSend: (String) -> Unit,
    onStop: () -> Unit,
    onExtensionPanelItemClick: (ExtensionPanelItemType) -> Unit,
    onVoiceRecordStart: () -> Unit,
    onVoiceRecordEnd: (shouldSend: Boolean, text: String) -> Unit
) {
    // 底部输入栏容器 - 使用全屏高度容器 + transform 偏移
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(appState.pageViewHeight.dp)
            .offset(y = animatedBottomOffset.dp)
    ) {
        // 占位空间：填充顶部空白
        Spacer(modifier = Modifier.weight(1f))
        
        // 底部输入栏
        ChatBottomBar(
            state = appState.bottomBarState,
            bottomSafeArea = appState.bottomSafeArea.dp,
            config = fullFeatureChatBottomBarConfig(
                pageId = "ChatDemo",
                placeholder = DEFAULT_PLACEHOLDER
            ),
            modifier = Modifier.fillMaxWidth(),
            voiceInputState = appState.voiceInputState,
            onSend = onSend,
            onStop = onStop,
            onExtensionClick = {
                // 扩展面板点击
            },
            onExtensionPanelItemClick = onExtensionPanelItemClick,
            onKeyboardHeightChange = { params ->
                appState.updateKeyboardParams(params.height, params.duration.toInt())
            },
            onVoiceRecordStart = onVoiceRecordStart,
            onVoiceRecordEnd = onVoiceRecordEnd
        )
    }
}
