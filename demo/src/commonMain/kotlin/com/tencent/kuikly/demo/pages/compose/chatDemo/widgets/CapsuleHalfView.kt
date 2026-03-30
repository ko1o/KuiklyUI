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
import com.tencent.kuikly.compose.coil3.rememberAsyncImagePainter
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
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.offset
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
import com.tencent.kuikly.core.base.attr.ImageUri
import com.tencent.kuikly.demo.pages.compose.chatDemo.configs.*

// ==================== 半浮层动画时长 ====================
private const val HALF_VIEW_ANIM_DURATION = 250
private const val PAGE_NAME = "ChatDemo"

// 图标资源路径
private fun chevronUpIcon() = ImageUri.pageAssets("chevron_up@3x.png").toUrl(PAGE_NAME)
private fun chevronDownIcon() = ImageUri.pageAssets("chevron_down@3x.png").toUrl(PAGE_NAME)
private fun checkboxTickIcon() = ImageUri.pageAssets("checkbox_tick@3x.png").toUrl(PAGE_NAME)

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

// 半浮层背景色 - 从 config 获取
@Composable
fun CapsuleHalfView(
    config: CapsuleHalfViewConfig,
    onClose: () -> Unit = {},
    onPlaceholderChange: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val ui = config.uiConfig
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(topStart = ui.cornerRadius, topEnd = ui.cornerRadius))
            .background(ui.backgroundColor)
    ) {
        CapsuleHalfViewContent(
            config = config,
            onClose = onClose,
            onPlaceholderChange = onPlaceholderChange
        )
    }
}

/**
 * 带底部输入栏的胶囊半浮层组件
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
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = it.uiConfig.cornerRadius, topEnd = it.uiConfig.cornerRadius))
                    .background(it.uiConfig.backgroundColor)
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
    val ui = config.uiConfig
    
    // 如果有整体内容 builder，直接调用
    if (ui.contentBuilder != null) {
        ui.contentBuilder.invoke(config, onClose, onPlaceholderChange)
        return
    }
    
    // 标题栏 - 支持 builder
    if (ui.titleBarBuilder != null) {
        ui.titleBarBuilder.invoke(config.title, config.titleIcon, onClose)
    } else {
        HalfViewTitleBar(
            title = config.title,
            titleIcon = config.titleIcon,
            onClose = onClose,
            uiConfig = ui
        )
    }

    if (config.isAIDrawMode && config.aiDrawConfig != null) {
        // AI 画图专属布局
        if (ui.aiDrawContentBuilder != null) {
            ui.aiDrawContentBuilder.invoke(config.aiDrawConfig)
        } else {
            AIDrawHalfViewContent(config = config.aiDrawConfig, uiConfig = ui)
        }
    } else {
        // 通用半浮层布局（AI 写作等）
        if (config.showTypeSection && config.typeItems.isNotEmpty()) {
            if (ui.typeSectionBuilder != null) {
                ui.typeSectionBuilder.invoke(config) { selectedType ->
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
            } else {
                HalfViewTypeSection(
                    title = config.typeTitle,
                    items = config.typeItems,
                    gridRows = config.typeGridRows,
                    uiConfig = ui,
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
        }

        if (config.showRequireSection && config.requireBarItems.value.isNotEmpty()) {
            if (ui.requireSectionBuilder != null) {
                ui.requireSectionBuilder.invoke(config.requireBarItems.value, config.requireTitle)
            } else {
                HalfViewRequireSection(
                    title = config.requireTitle,
                    items = config.requireBarItems.value,
                    showTitle = config.requireTitle.isNotEmpty(),
                    uiConfig = ui
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(ui.dividerHeight)
            .background(ui.dividerColor)
    )
}

// ==================== AI 画图专属内容 ====================

/**
 * AI 画图半浮层内容
 * 
 * 参考 QQAIBiz QueryHalfDrawView 第 330-583 行：
 * - 风格/比例展开按钮栏（互斥展开）
 * - 风格卡片横向滚动列表（带缩略图 + 名称 + 选中标记）
 * - 比例卡片横向滚动列表（带图标 + 子名称 + 选中标记）
 */
@Composable
private fun AIDrawHalfViewContent(config: AIDrawConfig, uiConfig: CapsuleHalfViewUIConfig = CapsuleHalfViewUIConfig()) {
    val contentHeight = (12 + uiConfig.typeButtonHeight.value.toInt() + 12 + uiConfig.styleCardHeight.value.toInt() + 16).dp
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(contentHeight)
    ) {
    Spacer(modifier = Modifier.height(uiConfig.aiDrawContentTopSpacing))
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(uiConfig.typeButtonHeight),
        contentPadding = PaddingValues(horizontal = uiConfig.horizontalPadding),
        horizontalArrangement = Arrangement.spacedBy(uiConfig.aiDrawButtonBarSpacing)
    ) {
        item {
            AIDrawExpandButton(
                label = "风格",
                display = config.getSelectedStyleName(),
                isExpanded = config.isStyleExpanded.value,
                uiConfig = uiConfig,
                onClick = {
                    if (config.isStyleExpanded.value) {
                        config.isStyleExpanded.value = false
                    } else {
                        config.isRatioExpanded.value = false
                        config.isStyleExpanded.value = true
                    }
                }
            )
        }
        item {
            AIDrawExpandButton(
                label = "比例",
                display = config.getSelectedRatioName(),
                isExpanded = config.isRatioExpanded.value,
                uiConfig = uiConfig,
                onClick = {
                    if (config.isRatioExpanded.value) {
                        config.isRatioExpanded.value = false
                    } else {
                        config.isStyleExpanded.value = false
                        config.isRatioExpanded.value = true
                    }
                }
            )
        }
    }

    Spacer(modifier = Modifier.height(uiConfig.aiDrawContentTopSpacing))

    AnimatedVisibility(
        visible = config.isStyleExpanded.value,
        enter = com.tencent.kuikly.compose.animation.fadeIn(animationSpec = tween(uiConfig.aiDrawFadeAnimDuration)),
        exit = com.tencent.kuikly.compose.animation.fadeOut(animationSpec = tween(uiConfig.aiDrawFadeAnimDuration))
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(uiConfig.styleCardHeight),
            contentPadding = PaddingValues(horizontal = uiConfig.horizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(uiConfig.aiDrawCardSpacing)
        ) {
            items(config.styleItems) { item ->
                StyleCard(item = item, isSelected = item.picked.value, uiConfig = uiConfig, onClick = {
                    config.styleItems.forEach {
                        if (it == item) it.picked.value = !it.picked.value else it.picked.value = false
                    }
                })
            }
        }
    }

    AnimatedVisibility(
        visible = config.isRatioExpanded.value,
        enter = com.tencent.kuikly.compose.animation.fadeIn(animationSpec = tween(uiConfig.aiDrawFadeAnimDuration)),
        exit = com.tencent.kuikly.compose.animation.fadeOut(animationSpec = tween(uiConfig.aiDrawFadeAnimDuration))
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(uiConfig.styleCardHeight),
            contentPadding = PaddingValues(horizontal = uiConfig.horizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(uiConfig.aiDrawCardSpacing)
        ) {
            items(config.ratioItems) { item ->
                RatioCard(item = item, isSelected = item.picked.value, uiConfig = uiConfig, onClick = {
                    config.ratioItems.forEach {
                        if (it == item) it.picked.value = !it.picked.value else it.picked.value = false
                    }
                })
            }
        }
    }

    Spacer(modifier = Modifier.height(uiConfig.aiDrawBottomSpacing))
    }
}

/**
 * AI 画图展开按钮 - 严格参考 QQAIBiz QueryHalfDrawView 第 346-457 行
 * 
 * 箭头方向参考第 449 行:
 *   if (require.isSelected.value) "chevron_up" else "chevron_down"
 * isSelected/isExpanded = true → chevron_up (∧)，表示点击可收起
 * isSelected/isExpanded = false → chevron_down (∨)，表示点击可展开
 */
@Composable
private fun AIDrawExpandButton(
    label: String,
    display: String,
    isExpanded: Boolean,
    uiConfig: CapsuleHalfViewUIConfig = CapsuleHalfViewUIConfig(),
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .height(uiConfig.typeButtonHeight)
            .clip(RoundedCornerShape(uiConfig.aiDrawButtonCornerRadius))
            .border(uiConfig.typeButtonBorderWidth, uiConfig.aiDrawButtonBorderColor, RoundedCornerShape(uiConfig.aiDrawButtonCornerRadius))
            .background(uiConfig.aiDrawButtonBgColor)
            .clickable { onClick() }
            .padding(horizontal = uiConfig.typeButtonHeight / 2),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = uiConfig.aiDrawButtonFontSize, color = uiConfig.aiDrawButtonTextColor)
        if (display.isNotEmpty()) {
            Spacer(modifier = Modifier.width(uiConfig.arrowTextSpacing))
            Text(text = display, fontSize = uiConfig.aiDrawButtonFontSize, color = uiConfig.aiDrawButtonTextColor)
        }
        Spacer(modifier = Modifier.width(uiConfig.arrowTextSpacing))
        @OptIn(InternalResourceApi::class)
        Image(
            painter = painterResource(DrawableResource(if (isExpanded) chevronUpIcon() else chevronDownIcon())),
            contentDescription = if (isExpanded) "collapse" else "expand",
            modifier = Modifier.size(uiConfig.arrowIconSize)
        )
    }
}

/**
 * 风格卡片 - 参考 QQAIBiz QueryHalfDrawView StyleCard 第 586-642 行
 * 
 * 80dp 高，圆角 12dp，背景是网络图片，底部渐变遮罩 + 白色文字
 * 选中时右上角显示打勾标记
 */
@Composable
private fun StyleCard(
    item: AIDrawStyleItem,
    isSelected: Boolean,
    uiConfig: CapsuleHalfViewUIConfig = CapsuleHalfViewUIConfig(),
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(uiConfig.styleCardHeight)
            .clip(RoundedCornerShape(uiConfig.styleCardCornerRadius))
            .clickable { onClick() }
    ) {
        Image(
            painter = rememberAsyncImagePainter(item.url),
            contentDescription = item.name,
            contentScale = com.tencent.kuikly.compose.ui.layout.ContentScale.Crop,
            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(uiConfig.styleCardCornerRadius))
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(uiConfig.styleCardGradientHeight)
                .align(Alignment.BottomCenter)
                .background(
                    brush = com.tencent.kuikly.compose.ui.graphics.Brush.verticalGradient(
                        colors = listOf(Color.Transparent, uiConfig.styleCardGradientColor)
                    ),
                    shape = RoundedCornerShape(bottomStart = uiConfig.styleCardCornerRadius, bottomEnd = uiConfig.styleCardCornerRadius)
                )
        )
        if (isSelected) {
            CheckedIndicator(uiConfig = uiConfig, modifier = Modifier.align(Alignment.TopEnd).offset(x = uiConfig.checkedIndicatorOffsetX, y = uiConfig.checkedIndicatorOffsetY))
        }
        Text(
            text = item.name, fontSize = uiConfig.styleCardNameFontSize, color = uiConfig.styleCardNameColor, maxLines = 1,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = uiConfig.styleCardNameBottomPadding)
        )
    }
}

/**
 * 比例卡片 - 严格参考 QQAIBiz QueryHalfDrawView RatioCard 第 644-690 行
 * 
 * 80dp 高，圆角 12dp，灰色背景 + 顶部图标(28dp, paddingTop=18dp) + 底部文字(paddingBottom=8dp)
 * 选中时右上角显示打勾标记
 */
@Composable
private fun RatioCard(
    item: AIDrawRatioItem,
    isSelected: Boolean,
    uiConfig: CapsuleHalfViewUIConfig = CapsuleHalfViewUIConfig(),
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(uiConfig.styleCardHeight)
            .clip(RoundedCornerShape(uiConfig.styleCardCornerRadius))
            .border(uiConfig.typeButtonBorderWidth, uiConfig.ratioCardBorderColor, RoundedCornerShape(uiConfig.styleCardCornerRadius))
            .background(uiConfig.ratioCardBgColor)
            .clickable { onClick() }
    ) {
        if (item.icon.isNotEmpty()) {
            Box(
                modifier = Modifier.align(Alignment.TopCenter).padding(top = uiConfig.ratioCardIconTopPadding).size(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(painter = rememberAsyncImagePainter(item.icon), contentDescription = item.name, modifier = Modifier.size(uiConfig.ratioCardIconSize))
            }
        }
        if (isSelected) {
            CheckedIndicator(uiConfig = uiConfig, modifier = Modifier.align(Alignment.TopEnd).offset(x = uiConfig.checkedIndicatorOffsetX, y = uiConfig.checkedIndicatorOffsetY))
        }
        Text(
            text = item.subName.ifEmpty { item.name }, fontSize = uiConfig.ratioCardNameFontSize, color = uiConfig.ratioCardNameColor, maxLines = 1,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = uiConfig.ratioCardNameBottomPadding)
        )
    }
}

@Composable
private fun CheckedIndicator(uiConfig: CapsuleHalfViewUIConfig = CapsuleHalfViewUIConfig(), modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.size(uiConfig.checkedIndicatorSize).clip(RoundedCornerShape(uiConfig.checkedIndicatorSize / 2)).background(uiConfig.checkedIndicatorBgColor),
        contentAlignment = Alignment.Center
    ) {
        @OptIn(InternalResourceApi::class)
        Image(painter = painterResource(DrawableResource(checkboxTickIcon())), contentDescription = "checked", modifier = Modifier.size(uiConfig.checkedIndicatorIconSize))
    }
}

// ==================== 标题栏 ====================

@Composable
private fun HalfViewTitleBar(
    title: String,
    titleIcon: String,
    onClose: () -> Unit,
    uiConfig: CapsuleHalfViewUIConfig = CapsuleHalfViewUIConfig()
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(uiConfig.titleHeight)
            .padding(start = uiConfig.horizontalPadding, top = uiConfig.titleTopPadding, end = uiConfig.horizontalPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (titleIcon.isNotEmpty()) {
                @OptIn(InternalResourceApi::class)
                val drawable = DrawableResource(titleIcon)
                Image(
                    painter = painterResource(drawable),
                    contentDescription = title,
                    modifier = Modifier.size(uiConfig.titleIconSize)
                )
                Spacer(modifier = Modifier.width(uiConfig.titleIconSpacing))
            }
            Text(
                text = title,
                fontSize = uiConfig.titleFontSize,
                fontWeight = FontWeight.Medium,
                color = uiConfig.titleColor
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .size(uiConfig.closeButtonSize)
                .clickable { onClose() },
            contentAlignment = Alignment.Center
        ) {
            Text(text = uiConfig.closeButtonText, fontSize = uiConfig.closeButtonFontSize, color = uiConfig.closeButtonColor)
        }
    }
}

// ==================== 类型选择区域 ====================

/**
 * 类型选择区域 - 参考 QQAIBiz QueryHalfWriteView
 * 
 * 使用 LazyHorizontalGrid 实现流式布局：
 * - rows = GridCells.Fixed(2) - 固定 2 行
 * - rowsSpacing = 8.dp - 行间距
 * - columnsSpacing = 10.dp - 列间距
 * 
 * 关键：保证横向纵向间距一致
 */
@Composable
private fun HalfViewTypeSection(
    title: String,
    items: List<HalfViewTypeItem>,
    gridRows: Int,
    uiConfig: CapsuleHalfViewUIConfig = CapsuleHalfViewUIConfig(),
    onTypeSelected: (HalfViewTypeItem) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                fontSize = uiConfig.typeSectionTitleFontSize,
                color = uiConfig.typeSectionTitleColor,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(start = uiConfig.horizontalPadding, top = uiConfig.typeSectionTitleTopPadding, bottom = uiConfig.typeSectionTitleBottomPadding)
            )
        }
        
        val gridHeight = (uiConfig.typeButtonHeight * gridRows) + (uiConfig.typeGridRowSpacing * (gridRows - 1))
        
        LazyHorizontalGrid(
            rows = GridCells.Fixed(gridRows),
            modifier = Modifier
                .fillMaxWidth()
                .height(gridHeight),
            contentPadding = PaddingValues(horizontal = uiConfig.horizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(uiConfig.typeGridColumnSpacing),
            verticalArrangement = Arrangement.spacedBy(uiConfig.typeGridRowSpacing)
        ) {
            items(items) { item ->
                TypeButton(
                    item = item,
                    isSelected = item.picked.value,
                    uiConfig = uiConfig,
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
    uiConfig: CapsuleHalfViewUIConfig = CapsuleHalfViewUIConfig(),
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) uiConfig.typeButtonSelectedBgColor else uiConfig.typeButtonBgColor
    val borderColor = if (isSelected) uiConfig.typeButtonSelectedBorderColor else uiConfig.typeButtonBorderColor
    val textColor = if (isSelected) uiConfig.typeButtonSelectedTextColor else uiConfig.typeButtonTextColor

    Box(
        modifier = Modifier
            .height(uiConfig.typeButtonHeight)
            .wrapContentWidth()
            .clip(RoundedCornerShape(uiConfig.typeButtonHeight / 2))
            .border(uiConfig.typeButtonBorderWidth, borderColor, RoundedCornerShape(uiConfig.typeButtonHeight / 2))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = uiConfig.typeButtonHeight / 2),
        contentAlignment = Alignment.Center
    ) {
        Text(text = item.name, fontSize = uiConfig.typeButtonFontSize, color = textColor)
    }
}

// ==================== 要求选择区域 ====================

@Composable
private fun HalfViewRequireSection(
    title: String,
    items: List<HalfViewRequireBarItem>,
    showTitle: Boolean = true,
    uiConfig: CapsuleHalfViewUIConfig = CapsuleHalfViewUIConfig()
) {
    val expandedItem = remember { mutableStateOf<HalfViewRequireBarItem?>(null) }
    val buttonRects = remember { mutableStateOf<Map<HalfViewRequireBarItem, Rect>>(emptyMap()) }
    val showPopover = remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        if (showTitle && title.isNotEmpty()) {
            Text(
                text = title,
                fontSize = uiConfig.requireSectionTitleFontSize,
                color = uiConfig.requireSectionTitleColor,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(start = uiConfig.horizontalPadding, top = uiConfig.requireSectionTitleTopPadding, end = uiConfig.horizontalPadding, bottom = uiConfig.requireSectionTitleBottomPadding)
            )
        }

        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .height(uiConfig.typeButtonHeight),
            contentPadding = PaddingValues(horizontal = uiConfig.horizontalPadding),
            horizontalArrangement = Arrangement.spacedBy(uiConfig.requireButtonSpacing)
        ) {
            items(items) { item ->
                RequireBarButton(
                    item = item,
                    isExpanded = expandedItem.value == item,
                    uiConfig = uiConfig,
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

        Spacer(modifier = Modifier.height(uiConfig.requireSectionBottomSpacing))
    }

    val clickViewRect = expandedItem.value?.let { buttonRects.value[it] }
        ?: Rect(Offset(80f, 200f), Size(100f, 32f))

    AIPopover(
        isShow = showPopover,
        clickViewRect = clickViewRect,
        popoverWidth = uiConfig.popoverWidth,
        isTriangleTop = false,
        onDismiss = {},
        onAnimationFinished = { expandedItem.value = null }
    ) {
        val items = expandedItem.value?.items ?: emptyList()
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(uiConfig.popoverContentPadding)
        ) {
            items.forEach { requireItem ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(uiConfig.popoverItemHeight)
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
                            showPopover.value = false
                        }
                        .padding(horizontal = uiConfig.popoverItemHorizontalPadding),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = requireItem.name,
                        fontSize = uiConfig.popoverItemFontSize,
                        color = uiConfig.popoverItemTextColor
                    )
                    if (requireItem.picked.value) {
                        @OptIn(InternalResourceApi::class)
                        Image(
                            painter = painterResource(DrawableResource(checkboxTickIcon())),
                            contentDescription = "checked",
                            modifier = Modifier.size(uiConfig.popoverCheckmarkSize),
                            colorFilter = com.tencent.kuikly.compose.ui.graphics.ColorFilter.tint(uiConfig.popoverCheckmarkColor)
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
    uiConfig: CapsuleHalfViewUIConfig = CapsuleHalfViewUIConfig(),
    onClick: () -> Unit = {},
    onPositioned: (Rect) -> Unit = {}
) {
    val selectedItem = item.getSelectedItem()

    Row(
        modifier = Modifier
            .height(uiConfig.typeButtonHeight)
            .clip(RoundedCornerShape(uiConfig.typeButtonHeight / 2))
            .border(uiConfig.typeButtonBorderWidth, uiConfig.requireButtonBorderColor, RoundedCornerShape(uiConfig.typeButtonHeight / 2))
            .clickable { onClick() }
            .padding(horizontal = uiConfig.typeButtonHeight / 2)
            .onGloballyPositioned { coordinates ->
                onPositioned(coordinates.toRect())
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.buttonName,
            fontSize = uiConfig.requireButtonFontSize,
            color = if (item.enable.value) uiConfig.requireButtonTextColor else uiConfig.requireButtonDisabledTextColor
        )
        selectedItem?.let {
            Spacer(modifier = Modifier.width(uiConfig.arrowTextSpacing))
            Text(
                text = it.name,
                fontSize = uiConfig.requireButtonFontSize,
                color = if (item.enable.value) uiConfig.requireButtonTextColor else uiConfig.requireButtonDisabledTextColor
            )
        }
        Spacer(modifier = Modifier.width(uiConfig.arrowTextSpacing))
        @OptIn(InternalResourceApi::class)
        Image(
            painter = painterResource(
                DrawableResource(if (isExpanded) chevronUpIcon() else chevronDownIcon())
            ),
            contentDescription = if (isExpanded) "collapse" else "expand",
            modifier = Modifier.size(uiConfig.arrowIconSize)
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
