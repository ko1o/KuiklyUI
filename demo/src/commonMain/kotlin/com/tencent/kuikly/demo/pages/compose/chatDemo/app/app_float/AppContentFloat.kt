package com.tencent.kuikly.demo.pages.compose.chatDemo.app.app_float

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Spacer
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.core.log.KLog
import com.tencent.kuikly.demo.pages.compose.chatDemo.configs.*
import com.tencent.kuikly.demo.pages.compose.chatDemo.widgets.*

/**
 * 内容区域浮动元素组件
 * 
 * 参考 QQAIBiz 的 AppContentFloat 结构：
 * - 半浮层遮罩
 * - 胶囊栏
 * - 动画半浮层
 * 
 * 这些元素漂浮在消息列表上方，不参与内容区域的布局计算
 */
@Composable
fun AppContentFloat(
    halfViewState: CapsuleHalfViewState,
    capsuleBarConfig: ChatCapsuleBarConfig,
    aiWriteHalfViewConfig: CapsuleHalfViewConfig,
    aiDrawHalfViewConfig: CapsuleHalfViewConfig,
    onCapsuleItemClick: (Int, CapsuleItemConfig) -> Unit
) {
    // 使用 Column 从底部向上排列
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 半浮层遮罩 - 点击遮罩关闭半浮层
        if (halfViewState.isVisible) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0x33000000))
                    .clickable { halfViewState.hide() }
            )
        } else {
            Spacer(modifier = Modifier.weight(1f))
        }
        
        // 胶囊栏 - 半浮层显示时隐藏
        if (!halfViewState.isVisible) {
            ChatCapsuleBar(
                config = capsuleBarConfig,
                onItemClick = { index, item ->
                    KLog.i("AppContentFloat", "胶囊点击: index=$index, name=${item.name}, id=${item.id}")
                    
                    // 根据胶囊 buttonId 显示对应的半浮层
                    when (item.id) {
                        "14" -> halfViewState.show(aiWriteHalfViewConfig)  // AI写作
                        "13" -> halfViewState.show(aiDrawHalfViewConfig)  // AI生图
                        else -> onCapsuleItemClick(index, item)
                    }
                }
            )
            
            // 胶囊栏与底部输入框的间距
            Spacer(modifier = Modifier.height(12.dp))
        }
        
        // 半浮层 - 带动画切换
        AnimatedCapsuleHalfView(
            config = halfViewState.currentConfig.value,
            onClose = { halfViewState.hide() },
            onPlaceholderChange = { placeholder ->
                KLog.i("AppContentFloat", "半浮层占位符更新: $placeholder")
            }
        )
    }
}
