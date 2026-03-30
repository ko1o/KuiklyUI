package com.tencent.kuikly.demo.pages.compose.chatDemo.app.app_content

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.foundation.Canvas
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.layout.size
import com.tencent.kuikly.compose.foundation.layout.widthIn
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.material3.Text
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.Path
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.tencent.kuikly.demo.pages.compose.chatDemo.configs.ChatMessageItemConfig

/**
 * 聊天消息项组件
 */
@Composable
fun ChatMessageItem(
    message: String,
    isUser: Boolean,
    maxWidth: Dp,
    config: ChatMessageItemConfig = ChatMessageItemConfig()
) {
    if (isUser) {
        if (config.userMessageBuilder != null) {
            config.userMessageBuilder.invoke(message, maxWidth)
        } else {
            UserMessageItem(message = message, maxWidth = maxWidth, config = config)
        }
    } else {
        if (config.aiMessageBuilder != null) {
            config.aiMessageBuilder.invoke(message, maxWidth)
        } else {
            AiMessageItem(message = message, maxWidth = maxWidth, config = config)
        }
    }
}

@Composable
private fun UserMessageItem(message: String, maxWidth: Dp, config: ChatMessageItemConfig) {
    Box(
        modifier = Modifier
            .widthIn(max = maxWidth)
            .padding(bottom = config.userBottomPadding, end = config.userEndPadding)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .background(color = config.userBubbleColor, shape = RoundedCornerShape(config.userBubbleCornerRadius))
                    .padding(horizontal = config.userBubbleHorizontalPadding, vertical = config.userBubbleVerticalPadding)
            ) {
                Text(text = message, fontSize = config.userTextFontSize, color = config.userTextColor)
            }
            Canvas(
                modifier = Modifier.size(config.triangleWidth, config.triangleHeight).align(Alignment.CenterVertically)
            ) {
                val w = size.width; val h = size.height
                val path = Path().apply { moveTo(0f, 0f); lineTo(0f, h); lineTo(w, h / 2f); close() }
                drawPath(path = path, color = config.userBubbleColor)
            }
        }
    }
}

@Composable
private fun AiMessageItem(message: String, maxWidth: Dp, config: ChatMessageItemConfig) {
    Box(
        modifier = Modifier.widthIn(max = maxWidth).padding(horizontal = config.aiHorizontalPadding)
    ) {
        Text(text = message, fontSize = config.aiTextFontSize, color = config.aiTextColor)
    }
}
