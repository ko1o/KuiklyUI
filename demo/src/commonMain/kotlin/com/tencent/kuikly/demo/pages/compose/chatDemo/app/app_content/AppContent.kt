package com.tencent.kuikly.demo.pages.compose.chatDemo.app.app_content

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.tencent.kuikly.compose.foundation.Image
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.PaddingValues
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.Spacer
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.layout.width
import com.tencent.kuikly.compose.foundation.lazy.LazyColumn
import com.tencent.kuikly.compose.foundation.lazy.LazyListState
import com.tencent.kuikly.compose.foundation.lazy.items
import com.tencent.kuikly.compose.foundation.lazy.itemsIndexed
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.material3.Text
import com.tencent.kuikly.compose.resources.DrawableResource
import com.tencent.kuikly.compose.resources.InternalResourceApi
import com.tencent.kuikly.compose.resources.painterResource
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.text.font.FontWeight
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.tencent.kuikly.core.base.attr.ImageUri
import com.tencent.kuikly.core.log.KLog
import com.tencent.kuikly.demo.pages.compose.chatDemo.app.app_float.AppContentFloat
import com.tencent.kuikly.demo.pages.compose.chatDemo.configs.*
import com.tencent.kuikly.demo.pages.compose.chatDemo.widgets.*

/**
 * 消息内容区域组件
 * 
 * 参考 QQAIBiz 的 AppContent 结构：
 * - 消息列表 / 欢迎页（二选一）
 * - 浮动元素（半浮层遮罩、胶囊栏等）
 * 
 * 注意：为了首屏速度和 layout 刷新速度，不要依赖别的布局来计算高度
 */
@Composable
fun AppContent(
    chatList: List<String>,
    listState: LazyListState,
    listBottomPadding: Float,
    pageViewWidth: Float,
    halfViewState: CapsuleHalfViewState,
    capsuleBarConfig: ChatCapsuleBarConfig,
    aiWriteHalfViewConfig: CapsuleHalfViewConfig,
    aiDrawHalfViewConfig: CapsuleHalfViewConfig,
    onInputTextChange: (String) -> Unit,
    onCapsuleItemClick: (Int, CapsuleItemConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxWidth()) {
        // 消息列表区域
        if (chatList.isNotEmpty()) {
            ChatMessageList(
                chatList = chatList,
                listState = listState,
                listBottomPadding = listBottomPadding,
                pageViewWidth = pageViewWidth
            )
        } else {
            // 欢迎页
            WelcomeView(
                onInputTextChange = onInputTextChange,
                listState = listState,
                contentPadding = PaddingValues(bottom = listBottomPadding.dp),
                modifier = Modifier.fillMaxSize()
            )
        }
        
        // 浮动元素区域
        AppContentFloat(
            halfViewState = halfViewState,
            capsuleBarConfig = capsuleBarConfig,
            aiWriteHalfViewConfig = aiWriteHalfViewConfig,
            aiDrawHalfViewConfig = aiDrawHalfViewConfig,
            onCapsuleItemClick = onCapsuleItemClick
        )
    }
}

/**
 * 聊天消息列表组件
 */
@Composable
private fun ChatMessageList(
    chatList: List<String>,
    listState: LazyListState,
    listBottomPadding: Float,
    pageViewWidth: Float
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(bottom = listBottomPadding.dp)
    ) {
        itemsIndexed(chatList) { index, message ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                horizontalArrangement = if (index % 2 == 0) Arrangement.End else Arrangement.Start
            ) {
                ChatMessageItem(
                    message = message,
                    isUser = (index % 2 == 0),
                    maxWidth = (0.7f * pageViewWidth).dp
                )
            }
        }
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
    
    // 消息列表变化时滚动到底部
    LaunchedEffect(chatList.size) {
        if (chatList.isNotEmpty()) {
            listState.animateScrollToItem(chatList.size)
        }
    }
}
