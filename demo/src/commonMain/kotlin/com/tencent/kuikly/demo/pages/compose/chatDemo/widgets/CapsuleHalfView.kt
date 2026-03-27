package com.tencent.kuikly.demo.pages.compose.chatDemo.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.tencent.kuikly.compose.animation.AnimatedVisibility
import com.tencent.kuikly.compose.animation.core.tween
import com.tencent.kuikly.compose.animation.fadeIn
import com.tencent.kuikly.compose.animation.fadeOut
import com.tencent.kuikly.compose.animation.slideInVertically
import com.tencent.kuikly.compose.animation.slideOutVertically
import com.tencent.kuikly.compose.foundation.Image
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.PaddingValues
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.Spacer
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.layout.size
import com.tencent.kuikly.compose.foundation.layout.width
import com.tencent.kuikly.compose.foundation.layout.wrapContentWidth
import com.tencent.kuikly.compose.foundation.lazy.LazyRow
import com.tencent.kuikly.compose.foundation.lazy.grid.GridCells
import com.tencent.kuikly.compose.foundation.lazy.grid.LazyHorizontalGrid
import com.tencent.kuikly.compose.foundation.lazy.grid.items
import com.tencent.kuikly.compose.foundation.lazy.items
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
import com.tencent.kuikly.demo.pages.compose.chatDemo.configs.*

// ==================== 半浮层动画时长 ====================
private const val HALF_VIEW_ANIM_DURATION = 250

// ==================== 胶囊半浮层组件（严格参考 QQAIBiz QueryHalfWriteView） ====================

/**
 * 胶囊半浮层组件 - 带动画的显隐控制
 * 参考 QQAIBiz CapsuleFloating 使用 AnimatedTransitionBox + DIRECTION_FROM_BOTTOM
 */
@Composable
fun AnimatedCapsuleHalfView(
    config: CapsuleHalfViewConfig?,
    onClose: () -> Unit = {},
    onPlaceholderChange: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = config != null,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(HALF_VIEW_ANIM_DURATION)
        ) + fadeIn(animationSpec = tween(HALF_VIEW_ANIM_DURATION)),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(HALF_VIEW_ANIM_DURATION)
        ) + fadeOut(animationSpec = tween(HALF_VIEW_ANIM_DURATION)),
        modifier = modifier
    ) {
        config?.let {
            CapsuleHalfView(
                config = it,
                onClose = onClose,
                onPlaceholderChange = onPlaceholderChange
            )
        }
    }
}

/**
 * 胶囊半浮层组件（参考 QQAIBiz QueryHalfWriteView 布局）
 *
 * 布局结构：
 * - 标题栏（图标 + 标题 + ... + 关闭按钮）高度 60dp，padding(start=24, top=24, end=24)
 * - 类型选择区域（"类型" 标签 + 2行网格）
 * - 要求选择区域（"要求" 标签 + 横向按钮行）
 * - 底部分割线 (0.5dp)
 * - 底部间距 (19dp)
 */
@Composable
fun CapsuleHalfView(
    config: CapsuleHalfViewConfig,
    onClose: () -> Unit = {},
    onPlaceholderChange: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = HALF_VIEW_CORNER_RADIUS, topEnd = HALF_VIEW_CORNER_RADIUS))
            .background(Color.White)
    ) {
        // 标题栏 - 参考 QQAIBiz: Row height=60dp, padding(start=24, top=24, end=24)
        HalfViewTitleBar(
            title = config.title,
            titleIcon = config.titleIcon,
            onClose = onClose
        )

        // 类型选择区域 - 参考 QQAIBiz QueryHalfWriteView
        if (config.showTypeSection && config.typeItems.isNotEmpty()) {
            HalfViewTypeSection(
                title = config.typeTitle,
                items = config.typeItems,
                gridRows = config.typeGridRows,
                onTypeSelected = { selectedType ->
                    // 单选逻辑 - 参考 QQAIBiz: 点击已选中的取消选中
                    config.typeItems.forEach { item ->
                        if (item == selectedType) {
                            item.picked.value = !item.picked.value
                            if (item.picked.value) {
                                config.requireBarItems.value = selectedType.requireBarItems
                            } else {
                                config.requireBarItems.value = config.defaultTypeItem?.requireBarItems ?: emptyList()
                            }
                        } else {
                            item.picked.value = false
                        }
                    }

                    // 更新占位符
                    val currentSelected = config.getSelectedTypeItem()
                    val placeholder = currentSelected?.placeholder ?: config.defaultTypeItem?.placeholder ?: config.placeholder
                    onPlaceholderChange(placeholder)
                }
            )
        }

        // 要求选择区域 - 参考 QQAIBiz: "要求" 标签 + 横向按钮行
        if (config.showRequireSection && config.requireBarItems.value.isNotEmpty()) {
            HalfViewRequireSection(
                title = config.requireTitle,
                items = config.requireBarItems.value,
                showTitle = config.requireTitle.isNotEmpty()
            )
        }

        // 底部分割线 - 参考 QQAIBiz: height=0.5dp, margin(top=24dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(0.5.dp)
                .background(Color(0xFFE5E5E5))
        )

        // 底部间距 - 参考 QQAIBiz: Spacer height=19dp
        Spacer(modifier = Modifier.height(19.dp))
    }
}

// ==================== 标题栏 - 参考 QQAIBiz QueryHalfWriteView 标题栏 ====================

@Composable
private fun HalfViewTitleBar(
    title: String,
    titleIcon: String,
    onClose: () -> Unit
) {
    // 参考 QQAIBiz: Row fillMaxWidth height=60dp padding(start=24, top=24, end=24)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(HALF_VIEW_TITLE_HEIGHT)
            .padding(start = HALF_VIEW_HORIZONTAL_PADDING, top = 24.dp, end = HALF_VIEW_HORIZONTAL_PADDING),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左侧：图标 + 标题 - 参考 QQAIBiz: Row(verticalAlignment=CenterVertically) { Image + Text }
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (titleIcon.isNotEmpty()) {
                // 图标 - 参考 QQAIBiz: Image(titleIcon, size=24dp, margin end=4dp)
                @OptIn(InternalResourceApi::class)
                val drawable = DrawableResource(titleIcon)
                Image(
                    painter = painterResource(drawable),
                    contentDescription = title,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }

            // 标题 - 参考 QQAIBiz: fontSize=16dp, fontWeight=W500
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333)
            )
        }

        // 中间占位 - 参考 QQAIBiz: View(modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.weight(1f))

        // 关闭按钮 - 参考 QQAIBiz: Image size=24dp, clickable
        Box(
            modifier = Modifier
                .size(24.dp)
                .clickable { onClose() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✕",
                fontSize = 16.sp,
                color = Color(0xFF999999)
            )
        }
    }
}

// ==================== 类型选择区域 - 参考 QQAIBiz QueryHalfWriteView ====================

@Composable
private fun HalfViewTypeSection(
    title: String,
    items: List<HalfViewTypeItem>,
    gridRows: Int,
    onTypeSelected: (HalfViewTypeItem) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // "类型" 标签 - 参考 QQAIBiz: fontSize=12dp, padding(start=24, top=12, bottom=6)
        if (title.isNotEmpty()) {
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color(0xFF333333),
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(start = HALF_VIEW_HORIZONTAL_PADDING, top = 12.dp, bottom = 6.dp)
            )
        }

        // 类型按钮网格 - 参考 QQAIBiz: LazyHorizontalGrid rows=Fixed(2), listHeight=32*2+12
        // rowsSpacing=8dp, columnsSpacing=10dp, contentPadding(left=24, right=24)
        LazyHorizontalGrid(
            rows = GridCells.Fixed(gridRows),
            modifier = Modifier
                .fillMaxWidth()
                .height((HALF_VIEW_TYPE_BUTTON_HEIGHT.value * gridRows + (gridRows - 1) * 12f).dp),
            contentPadding = PaddingValues(horizontal = HALF_VIEW_HORIZONTAL_PADDING),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items) { item ->
                TypeButton(
                    item = item,
                    isSelected = item.picked.value,
                    onClick = { onTypeSelected(item) }
                )
            }
        }
    }
}

// ==================== 类型按钮 - 参考 QQAIBiz TypeButton ====================

@Composable
private fun TypeButton(
    item: HalfViewTypeItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // 参考 QQAIBiz TypeButton:
    // - 选中: border=brand_standard, bg=brand_standard, text=white
    // - 未选中: border=border_standard, bg=TRANSPARENT, text=text_primary
    val backgroundColor = if (isSelected) Color(0xFF5B6CFF) else Color.Transparent
    val borderColor = if (isSelected) Color(0xFF5B6CFF) else Color(0xFFE5E5E5)
    val textColor = if (isSelected) Color.White else Color(0xFF333333)

    Box(
        modifier = Modifier
            .height(HALF_VIEW_TYPE_BUTTON_HEIGHT)
            .wrapContentWidth()
            .clip(RoundedCornerShape(HALF_VIEW_TYPE_BUTTON_HEIGHT / 2))
            .border(0.5.dp, borderColor, RoundedCornerShape(HALF_VIEW_TYPE_BUTTON_HEIGHT / 2))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = HALF_VIEW_TYPE_BUTTON_HEIGHT / 2),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = item.name,
            fontSize = 14.sp,
            color = textColor
        )
    }
}

// ==================== 要求选择区域 - 参考 QQAIBiz QueryHalfWriteView ====================

@Composable
private fun HalfViewRequireSection(
    title: String,
    items: List<HalfViewRequireBarItem>,
    showTitle: Boolean = true
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // "要求" 标签 - 参考 QQAIBiz: fontSize=12dp, padding(start=24, top=16, end=24, bottom=6)
        if (showTitle && title.isNotEmpty()) {
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color(0xFF333333),
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(start = HALF_VIEW_HORIZONTAL_PADDING, top = 16.dp, end = HALF_VIEW_HORIZONTAL_PADDING, bottom = 6.dp)
            )
        }

        // 要求按钮行 - 参考 QQAIBiz: LazyHorizontalGrid rows=Fixed(1), listHeight=32dp
        // rowsSpacing=8dp, contentPadding(left=24, right=24)
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(HALF_VIEW_TYPE_BUTTON_HEIGHT),
            contentPadding = PaddingValues(horizontal = HALF_VIEW_HORIZONTAL_PADDING),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items) { item ->
                RequireBarButton(
                    item = item,
                    onItemSelected = { selectedItem ->
                        // 单选逻辑
                        item.items.forEach {
                            it.picked.value = (it == selectedItem)
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// ==================== 要求栏按钮 - 参考 QQAIBiz QueryHalfWriteView ====================
// QQAIBiz 使用 Popover 弹出菜单，这里简化为循环切换

@Composable
private fun RequireBarButton(
    item: HalfViewRequireBarItem,
    onItemSelected: (HalfViewRequireItem) -> Unit
) {
    val selectedItem = item.getSelectedItem()

    // 参考 QQAIBiz: Row height=32dp, borderRadius=32/2, border=0.5dp
    // padding(start=32/2, end=32/2), clickable
    Row(
        modifier = Modifier
            .height(HALF_VIEW_TYPE_BUTTON_HEIGHT)
            .clip(RoundedCornerShape(HALF_VIEW_TYPE_BUTTON_HEIGHT / 2))
            .border(0.5.dp, Color(0xFFE5E5E5), RoundedCornerShape(HALF_VIEW_TYPE_BUTTON_HEIGHT / 2))
            .clickable {
                if (item.enable.value && item.items.isNotEmpty()) {
                    // 循环选择下一项
                    val currentIndex = item.items.indexOfFirst { it.picked.value }
                    val nextIndex = if (currentIndex < 0 || currentIndex >= item.items.size - 1) 0 else currentIndex + 1
                    onItemSelected(item.items[nextIndex])
                }
            }
            .padding(horizontal = HALF_VIEW_TYPE_BUTTON_HEIGHT / 2),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 按钮名称 - 参考 QQAIBiz: fontSize=14sp
        Text(
            text = item.buttonName,
            fontSize = 14.sp,
            color = if (item.enable.value) Color(0xFF333333) else Color(0xFF999999)
        )

        // 已选项名称 - 参考 QQAIBiz: 选中项显示在按钮名称右侧
        selectedItem?.let {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = it.name,
                fontSize = 14.sp,
                color = if (item.enable.value) Color(0xFF5B6CFF) else Color(0xFF999999)
            )
        }

        // 下拉箭头 - 参考 QQAIBiz: Image chevron_down size=12dp
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = "▼",
            fontSize = 10.sp,
            color = Color(0xFF999999)
        )
    }
}

// ==================== 胶囊半浮层状态管理 ====================

/**
 * 胶囊半浮层状态
 */
class CapsuleHalfViewState {
    /** 当前显示的半浮层配置 */
    var currentConfig: MutableState<CapsuleHalfViewConfig?> = mutableStateOf(null)

    /** 是否显示半浮层 */
    val isVisible: Boolean get() = currentConfig.value != null

    /** 显示半浮层 */
    fun show(config: CapsuleHalfViewConfig) {
        currentConfig.value = config
    }

    /** 隐藏半浮层 */
    fun hide() {
        currentConfig.value?.reset()
        currentConfig.value = null
    }
}

/**
 * 记住胶囊半浮层状态
 */
@Composable
fun rememberCapsuleHalfViewState(): CapsuleHalfViewState {
    return remember { CapsuleHalfViewState() }
}
