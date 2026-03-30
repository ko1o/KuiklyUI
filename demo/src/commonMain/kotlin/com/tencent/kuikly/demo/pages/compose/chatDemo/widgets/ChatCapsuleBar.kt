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

// 渐变高度 - 参考 QQAIBiz: private val gradientHeight = 12.dp
private val CAPSULE_GRADIENT_HEIGHT = 36.dp

// 胶囊栏背景色 - 参考 QQAIBiz: AIProductUIToken.Color.bg_aio_01 = 0xFFF7F7F7
private val CAPSULE_BAR_BG_COLOR = Color(0xFFF7F7F7)

/**
 * 胶囊位栏组件 - 横向可滚动的功能胶囊列表
 *
 * 参考 QQAIBiz ChatCapsuleBar 实现（第 219-238 行）：
 * - 顶部有渐变过渡效果，从透明到背景色
 * - 使用 LazyRow 实现横向滚动
 * - 背景色为 bg_aio_01
 *
 * 布局结构（参考 QQAIBiz 第 219-238 行）：
 * - Box (整体容器)
 *   - Image (渐变图片，顶部过渡效果) - 从 tintColor=透明 到 bg_aio_01
 *   - LazyHorizontalGrid (胶囊列表，背景色 bg_aio_01)
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

    // 整体容器
    // 参考 QQAIBiz: Box(modifier = Modifier.fillMaxWidth().height(120.dp).align(Alignment.BottomCenter))
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // 顶部渐变过渡效果
        // 参考 QQAIBiz 第 220-231 行：使用渐变图片从透明过渡到背景色
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(CAPSULE_GRADIENT_HEIGHT)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            CAPSULE_BAR_BG_COLOR
                        )
                    )
                )
        )
        
        // 胶囊栏主体
        // 参考 QQAIBiz 第 232-238 行：LazyHorizontalGrid with background(bg_aio_01)
        // 高度 84dp，不设 top/bottom padding，胶囊项靠底部对齐
        // contentPadding 的 bottom = gradientHeight(12dp) 在 QQAIBiz 中处理
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(config.barHeight)
                .background(CAPSULE_BAR_BG_COLOR)  // 背景色，遮挡下层内容
        ) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                contentPadding = PaddingValues(
                    start = config.horizontalPadding,
                    end = config.horizontalPadding,
                    bottom = 12.dp  // 参考 QQAIBiz: contentPadding bottom = gradientHeight(12dp)
                ),
                horizontalArrangement = Arrangement.spacedBy(config.itemSpacing)
            ) {
                itemsIndexed(config.items) { index, item ->
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

/**
 * 单个胶囊项组件 - 严格参考 QQAIBiz ChatCapsuleItem
 * 布局: Box(borderRadius=12, border=0.5, height=72, minWidth=77)
 *   - Image(icon, 24x24, margin start=18 top=12)
 *   - Text(name, align=BottomStart, padding start=18 end=18, fontSize=14, W400)
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

    // 参考 QQAIBiz: Box 布局, 图标左上角, 文字左下角
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
        // 图标 - 参考 QQAIBiz: Image(src=icon, 24x24, margin start=18 top=12)
        val drawable = DrawableResource(item.icon)
        Image(
            painter = painterResource(drawable),
            contentDescription = item.name,
            modifier = Modifier
                .padding(start = 18.dp, top = 12.dp)
                .size(24.dp)
        )

        // 文字标签 - 参考 QQAIBiz: align=BottomStart, padding(start=18, end=18), fontSize=14, W400
        Text(
            text = item.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.W400,
            color = config.itemTextColor,
            maxLines = 1,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 18.dp, end = 18.dp, bottom = 12.dp)
        )
    }
}
