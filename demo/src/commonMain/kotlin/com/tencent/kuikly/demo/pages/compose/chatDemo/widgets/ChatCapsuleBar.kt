package com.tencent.kuikly.demo.pages.compose.chatDemo.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.tencent.kuikly.compose.foundation.Image
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Spacer
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.PaddingValues
import com.tencent.kuikly.compose.foundation.layout.defaultMinSize
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.layout.size
import com.tencent.kuikly.compose.foundation.lazy.LazyRow
import com.tencent.kuikly.compose.foundation.lazy.itemsIndexed
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.material3.Text
import com.tencent.kuikly.compose.resources.DrawableResource
import com.tencent.kuikly.compose.resources.InternalResourceApi
import com.tencent.kuikly.compose.resources.painterResource
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.alpha
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Brush
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.text.font.FontWeight
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.tencent.kuikly.demo.pages.compose.chatDemo.configs.CapsuleItemConfig
import com.tencent.kuikly.demo.pages.compose.chatDemo.configs.ChatCapsuleBarConfig

// ==================== 胶囊位栏组件（参考 QQAIBiz ChatCapsuleBar） ====================

/**
 * 胶囊位栏组件 - 横向可滚动的功能胶囊列表
 *
 * @param config 胶囊栏配置（包含项数据和样式）
 * @param onItemClick 胶囊项点击回调，参数为 (index, item)
 * @param modifier 修饰符
 * @param visible 是否可见
 */
@Composable
fun ChatCapsuleBar(
    config: ChatCapsuleBarConfig,
    onItemClick: (Int, CapsuleItemConfig) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier,
    visible: Boolean = true
) {
    if (!visible || config.items.isEmpty()) return

    // 如果有整体 builder，直接调用 builder 渲染
    if (config.builder != null) {
        config.builder.invoke(config, onItemClick)
        return
    }

    // 整体容器
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // 顶部渐变过渡效果
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(config.gradientHeight)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            config.capsuleBarBgColor
                        )
                    )
                )
        )
        
        // 胶囊栏主体
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(config.barHeight)
                .background(config.capsuleBarBgColor)
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                contentPadding = PaddingValues(
                    start = config.horizontalPadding,
                    end = config.horizontalPadding,
                    bottom = config.listBottomPadding
                ),
                horizontalArrangement = Arrangement.spacedBy(config.itemSpacing)
            ) {
                itemsIndexed(config.items) { index, item ->
                    if (config.itemBuilder != null) {
                        config.itemBuilder.invoke(item, index, config, { onItemClick(index, item) })
                    } else {
                        ChatCapsuleItem(
                            item = item,
                            index = index,
                            config = config,
                            onClick = { onItemClick(index, item) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * 单个胶囊项组件 - 参考 QQAIBiz ChatCapsuleItem
 */
@OptIn(InternalResourceApi::class)
@Composable
private fun ChatCapsuleItem(
    item: CapsuleItemConfig,
    index: Int,
    config: ChatCapsuleBarConfig,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(config.borderRadius)

    Box(
        modifier = Modifier
            .defaultMinSize(minWidth = config.itemMinWidth)
            .height(config.itemHeight)
            .clip(shape)
            .border(
                width = config.itemBorderWidth,
                color = config.itemBorderColor,
                shape = shape
            )
            .background(config.itemBackgroundColor, shape)
            .alpha(if (item.enabled) 1f else 0.6f)
            .clickable(enabled = item.enabled) { onClick() }
    ) {
        // 图标
        val drawable = DrawableResource(item.icon)
        Image(
            painter = painterResource(drawable),
            contentDescription = item.name,
            modifier = Modifier
                .padding(start = config.itemIconPaddingStart, top = config.itemIconPaddingTop)
                .size(config.itemIconDisplaySize)
        )

        // 文字标签
        Text(
            text = item.name,
            fontSize = config.itemFontSize,
            fontWeight = FontWeight.W400,
            color = config.itemTextColor,
            maxLines = 1,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = config.itemTextPaddingStart, end = config.itemTextPaddingEnd, bottom = config.itemTextPaddingBottom)
        )
    }
}
