package com.tencent.kuikly.demo.pages.compose.chatDemo.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.tencent.kuikly.compose.animation.AnimatedVisibility
import com.tencent.kuikly.compose.animation.core.tween
import com.tencent.kuikly.compose.animation.slideInVertically
import com.tencent.kuikly.compose.animation.slideOutVertically
import com.tencent.kuikly.compose.foundation.Canvas
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
import com.tencent.kuikly.compose.ui.geometry.Offset
import com.tencent.kuikly.compose.ui.geometry.Rect
import com.tencent.kuikly.compose.ui.geometry.Size
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.Path
import com.tencent.kuikly.compose.ui.layout.onGloballyPositioned
import com.tencent.kuikly.compose.ui.text.font.FontWeight
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.tencent.kuikly.demo.pages.compose.chatDemo.configs.*

// ==================== 半浮层动画时长 ====================
private const val HALF_VIEW_ANIM_DURATION = 250

// ==================== 胶囊半浮层组件 ====================

/**
 * 带动画的胶囊半浮层组件
 * 
 * 参考 QQAIBiz CapsuleFloating + DrawFloatingCapsule 的实现：
 * - 使用 AnimatedVisibility + slideInVertically/slideOutVertically 实现从底部弹出/收起的动画
 * - 使用 lastConfig 缓存配置，确保退出动画期间内容不消失
 * - 动画时长 250ms
 * 
 * 关键实现（参考 DrawFloatingCapsule 第 126-178 行）：
 * - 通过缓存 lastConfig 在退出动画期间保持内容显示
 * - AnimatedVisibility 内部直接使用 config 或 lastConfig 来渲染内容
 */
@Composable
fun AnimatedCapsuleHalfView(
    config: CapsuleHalfViewConfig?,
    onClose: () -> Unit = {},
    onPlaceholderChange: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // 缓存最后一个有效的 config，用于退出动画期间显示内容
    val lastConfig = remember { mutableStateOf<CapsuleHalfViewConfig?>(null) }
    
    // 参考 QQAIBiz DrawFloatingCapsule：进入时缓存 config
    LaunchedEffect(config) {
        if (config != null) {
            lastConfig.value = config
        }
    }
    
    // 参考 QQAIBiz AnimatedTransitionBox + DIRECTION_FROM_BOTTOM
    AnimatedVisibility(
        visible = config != null,
        enter = slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight }, // 从底部进入
            animationSpec = tween(HALF_VIEW_ANIM_DURATION)
        ),
        exit = slideOutVertically(
            targetOffsetY = { fullHeight -> fullHeight }, // 向底部退出
            animationSpec = tween(HALF_VIEW_ANIM_DURATION)
        ),
        modifier = modifier
    ) {
        // 使用 config 或 lastConfig 来渲染内容
        // 退出动画期间 config 为 null，使用 lastConfig 保持内容显示
        val displayConfig = config ?: lastConfig.value
        displayConfig?.let {
            CapsuleHalfView(
                config = it,
                onClose = onClose,
                onPlaceholderChange = onPlaceholderChange
            )
        }
    }
    
    // 退出动画完成后清空缓存
    LaunchedEffect(config) {
        if (config == null) {
            kotlinx.coroutines.delay(HALF_VIEW_ANIM_DURATION.toLong() + 50)
            lastConfig.value = null
        }
    }
}

// 半浮层背景色 - 参考 QQAIBiz: AIProductUIToken.Color.bg_bottom_light
private val HALF_VIEW_BG_COLOR = Color.White

/**
 * 带底部输入栏的胶囊半浮层组件
 * 
 * 参考 QQAIBiz CapsuleFloating + QueryHalfWriteView 的实现：
 * - 半浮层和底部输入栏作为一体，从底部弹出覆盖胶囊栏
 * - 使用 AnimatedVisibility 控制整体的进入/退出动画
 * - 半浮层有顶部圆角，输入栏无圆角，两者连成一体
 * - 整体有白色背景，完全覆盖下层内容（包括胶囊栏）
 * 
 * 布局结构：
 * - Column (整体，有白色背景和顶部圆角)
 *   - CapsuleHalfView (半浮层内容)
 *   - bottomBar (底部输入栏，无圆角)
 */
@Composable
fun AnimatedCapsuleHalfViewWithBottomBar(
    config: CapsuleHalfViewConfig?,
    onClose: () -> Unit = {},
    onPlaceholderChange: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    bottomBar: @Composable () -> Unit = {}
) {
    // 缓存最后一个有效的 config，用于退出动画期间显示内容
    val lastConfig = remember { mutableStateOf<CapsuleHalfViewConfig?>(null) }
    
    // 参考 QQAIBiz DrawFloatingCapsule：进入时缓存 config
    LaunchedEffect(config) {
        if (config != null) {
            lastConfig.value = config
        }
    }
    
    // 参考 QQAIBiz AnimatedTransitionBox + DIRECTION_FROM_BOTTOM
    // 整个半浮层 + 输入栏作为一体进行动画
    AnimatedVisibility(
        visible = config != null,
        enter = slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight }, // 从底部进入
            animationSpec = tween(HALF_VIEW_ANIM_DURATION)
        ),
        exit = slideOutVertically(
            targetOffsetY = { fullHeight -> fullHeight }, // 向底部退出
            animationSpec = tween(HALF_VIEW_ANIM_DURATION)
        ),
        modifier = modifier
    ) {
        // 使用 config 或 lastConfig 来渲染内容
        // 退出动画期间 config 为 null，使用 lastConfig 保持内容显示
        val displayConfig = config ?: lastConfig.value
        displayConfig?.let {
            // 半浮层 + 输入栏垂直排列
            // 参考 QQAIBiz QueryHalfWriteView：整体有背景色，完全覆盖下层内容
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = HALF_VIEW_CORNER_RADIUS, topEnd = HALF_VIEW_CORNER_RADIUS))
                    .background(HALF_VIEW_BG_COLOR)  // 白色背景，完全覆盖胶囊栏
            ) {
                // 半浮层内容
                CapsuleHalfViewContent(
                    config = it,
                    onClose = onClose,
                    onPlaceholderChange = onPlaceholderChange
                )
                
                // 底部输入栏（无圆角，与半浮层连成一体）
                bottomBar()
            }
        }
    }
    
    // 退出动画完成后清空缓存
    LaunchedEffect(config) {
        if (config == null) {
            kotlinx.coroutines.delay(HALF_VIEW_ANIM_DURATION.toLong() + 50)
            lastConfig.value = null
        }
    }
}

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
        CapsuleHalfViewContent(
            config = config,
            onClose = onClose,
            onPlaceholderChange = onPlaceholderChange
        )
    }
}

/**
 * 半浮层内容组件 - 不包含圆角和背景色
 * 用于 AnimatedCapsuleHalfViewWithBottomBar 中，避免重复设置圆角和背景
 */
@Composable
private fun CapsuleHalfViewContent(
    config: CapsuleHalfViewConfig,
    onClose: () -> Unit = {},
    onPlaceholderChange: (String) -> Unit = {}
) {
    HalfViewTitleBar(
        title = config.title,
        titleIcon = config.titleIcon,
        onClose = onClose
    )

    if (config.showTypeSection && config.typeItems.isNotEmpty()) {
        HalfViewTypeSection(
            title = config.typeTitle,
            items = config.typeItems,
            gridRows = config.typeGridRows,
            onTypeSelected = { selectedType ->
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
                val currentSelected = config.getSelectedTypeItem()
                val placeholder = currentSelected?.placeholder ?: config.defaultTypeItem?.placeholder ?: config.placeholder
                onPlaceholderChange(placeholder)
            }
        )
    }

    if (config.showRequireSection && config.requireBarItems.value.isNotEmpty()) {
        HalfViewRequireSection(
            title = config.requireTitle,
            items = config.requireBarItems.value,
            showTitle = config.requireTitle.isNotEmpty()
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(0.5.dp)
            .background(Color(0xFFE5E5E5))
    )
}

// ==================== 标题栏 ====================

@Composable
private fun HalfViewTitleBar(
    title: String,
    titleIcon: String,
    onClose: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(HALF_VIEW_TITLE_HEIGHT)
            .padding(start = HALF_VIEW_HORIZONTAL_PADDING, top = 24.dp, end = HALF_VIEW_HORIZONTAL_PADDING),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (titleIcon.isNotEmpty()) {
                @OptIn(InternalResourceApi::class)
                val drawable = DrawableResource(titleIcon)
                Image(
                    painter = painterResource(drawable),
                    contentDescription = title,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .size(24.dp)
                .clickable { onClose() },
            contentAlignment = Alignment.Center
        ) {
            Text(text = "✕", fontSize = 16.sp, color = Color(0xFF999999))
        }
    }
}

// ==================== 类型选择区域 ====================

@Composable
private fun HalfViewTypeSection(
    title: String,
    items: List<HalfViewTypeItem>,
    gridRows: Int,
    onTypeSelected: (HalfViewTypeItem) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color(0xFF333333),
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(start = HALF_VIEW_HORIZONTAL_PADDING, top = 12.dp, bottom = 6.dp)
            )
        }
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

// ==================== 类型按钮 ====================

@Composable
private fun TypeButton(
    item: HalfViewTypeItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
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
        Text(text = item.name, fontSize = 14.sp, color = textColor)
    }
}

// ==================== 要求选择区域 ====================

@Composable
private fun HalfViewRequireSection(
    title: String,
    items: List<HalfViewRequireBarItem>,
    showTitle: Boolean = true
) {
    val expandedItem = remember { mutableStateOf<HalfViewRequireBarItem?>(null) }
    // 记录每个按钮的位置信息
    val buttonRects = remember { mutableStateOf<Map<HalfViewRequireBarItem, Rect>>(emptyMap()) }
    val showPopover = remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        if (showTitle && title.isNotEmpty()) {
            Text(
                text = title,
                fontSize = 12.sp,
                color = Color(0xFF333333),
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(start = HALF_VIEW_HORIZONTAL_PADDING, top = 16.dp, end = HALF_VIEW_HORIZONTAL_PADDING, bottom = 6.dp)
            )
        }

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
                    isExpanded = expandedItem.value == item,
                    onClick = {
                        if (expandedItem.value == item) {
                            expandedItem.value = null
                            showPopover.value = false
                        } else {
                            expandedItem.value = item
                            showPopover.value = true
                        }
                    },
                    onPositioned = { rect ->
                        buttonRects.value = buttonRects.value + (item to rect)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Popover - 使用独立 AIPopover 组件（参考 QQAIBiz AIPopover + QUIPopover）
    val clickViewRect = expandedItem.value?.let { buttonRects.value[it] }
        ?: Rect(Offset(80f, 200f), Size(100f, 32f))

    AIPopover(
        isShow = showPopover,
        clickViewRect = clickViewRect,
        popoverWidth = 160.dp,
        isTriangleTop = false,  // 三角形在底部，菜单在按钮上方
        onDismiss = {
            // 点击背景关闭时，清空选中状态
            expandedItem.value = null
        }
    ) {
        val items = expandedItem.value?.items ?: emptyList()
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            items.forEach { requireItem ->
                // 参考 QQAIBiz: Row height=44dp, padding(16, 0, 16, 0)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clickable {
                            expandedItem.value?.let { currentItem ->
                                currentItem.items.forEach {
                                    if (it == requireItem) {
                                        it.picked.value = !it.picked.value
                                    } else {
                                        it.picked.value = false
                                    }
                                }
                            }
                            expandedItem.value = null
                            showPopover.value = false
                        }
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // 选项名称 - 参考 QQAIBiz: fontSize=16sp
                    Text(
                        text = requireItem.name,
                        fontSize = 16.sp,
                        color = Color(0xFF333333)
                    )
                    // 选中标记 - 参考 QQAIBiz: Image(src=QUIToken.image("check"), size=16dp)
                    if (requireItem.picked.value) {
                        Text(
                            text = "✓",
                            fontSize = 16.sp,
                            color = Color(0xFF5B6CFF)
                        )
                    }
                }
            }
        }
    }
}

// ==================== 要求栏按钮 ====================

@Composable
private fun RequireBarButton(
    item: HalfViewRequireBarItem,
    isExpanded: Boolean = false,
    onClick: () -> Unit = {},
    onPositioned: (Rect) -> Unit = {}
) {
    val selectedItem = item.getSelectedItem()

    Row(
        modifier = Modifier
            .height(HALF_VIEW_TYPE_BUTTON_HEIGHT)
            .clip(RoundedCornerShape(HALF_VIEW_TYPE_BUTTON_HEIGHT / 2))
            .border(0.5.dp, Color(0xFFE5E5E5), RoundedCornerShape(HALF_VIEW_TYPE_BUTTON_HEIGHT / 2))
            .clickable { onClick() }
            .padding(horizontal = HALF_VIEW_TYPE_BUTTON_HEIGHT / 2)
            .onGloballyPositioned { coordinates ->
                onPositioned(coordinates.toRect())
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.buttonName,
            fontSize = 14.sp,
            color = if (item.enable.value) Color(0xFF333333) else Color(0xFF999999)
        )
        // 已选项名称 - 参考 QQAIBiz: 选中项显示 text_primary 颜色（与按钮名称同色）
        selectedItem?.let {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = it.name,
                fontSize = 14.sp,
                color = if (item.enable.value) Color(0xFF333333) else Color(0xFF999999)
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = if (isExpanded) "▲" else "▼",
            fontSize = 10.sp,
            color = Color(0xFF999999)
        )
    }
}

// ==================== 胶囊半浮层状态管理 ====================

class CapsuleHalfViewState {
    var currentConfig: MutableState<CapsuleHalfViewConfig?> = mutableStateOf(null)
    val isVisible: Boolean get() = currentConfig.value != null

    fun show(config: CapsuleHalfViewConfig) {
        currentConfig.value = config
    }

    fun hide() {
        currentConfig.value?.reset()
        currentConfig.value = null
    }
}

@Composable
fun rememberCapsuleHalfViewState(): CapsuleHalfViewState {
    return remember { CapsuleHalfViewState() }
}
