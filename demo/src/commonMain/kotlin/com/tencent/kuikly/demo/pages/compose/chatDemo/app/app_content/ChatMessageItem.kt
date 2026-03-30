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

/**
 * 聊天消息项组件
 * 
 * 支持用户消息和 AI 消息两种样式：
 * - 用户消息：右对齐，带气泡和右侧三角
 * - AI 消息：左对齐，使用 Markdown 渲染
 */
@Composable
fun ChatMessageItem(
    message: String,
    isUser: Boolean,
    maxWidth: Dp
) {
    if (isUser) {
        UserMessageItem(message = message, maxWidth = maxWidth)
    } else {
        AiMessageItem(message = message, maxWidth = maxWidth)
    }
}

/**
 * 用户消息项
 */
@Composable
private fun UserMessageItem(
    message: String,
    maxWidth: Dp
) {
    Box(
        modifier = Modifier
            .widthIn(max = maxWidth)
            .padding(bottom = 4.dp, end = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // 消息气泡
            Box(
                modifier = Modifier
                    .background(
                        color = Color(0xFFE9E9EB),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 10.dp, vertical = 10.dp)
            ) {
                Text(
                    text = message,
                    fontSize = 14.sp,
                    color = Color.Black
                )
            }
            
            // 右侧三角
            Canvas(
                modifier = Modifier
                    .size(6.dp, 12.dp)
                    .align(Alignment.CenterVertically)
            ) {
                val width = size.width
                val height = size.height
                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(0f, height)
                    lineTo(width, height / 2f)
                    close()
                }
                drawPath(path = path, color = Color(0xFFE9E9EB))
            }
        }
    }
}

/**
 * AI 消息项
 * 
 * 使用 Markdown 渲染 AI 回复内容
 */
@Composable
private fun AiMessageItem(
    message: String,
    maxWidth: Dp
) {
    // 简化实现：直接显示文本
    // 实际使用时可以集成 Markdown 组件
    Box(
        modifier = Modifier
            .widthIn(max = maxWidth)
            .padding(horizontal = 24.dp)
    ) {
        Text(
            text = message,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}
