package com.tencent.kuikly.demo.pages.compose.chatDemo.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.tencent.kuikly.compose.animation.core.animateFloatAsState
import com.tencent.kuikly.compose.animation.core.tween
import com.tencent.kuikly.compose.foundation.Canvas
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.BoxScope
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.foundation.layout.offset
import com.tencent.kuikly.compose.foundation.layout.size
import com.tencent.kuikly.compose.foundation.layout.width
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.ui.graphics.RectangleShape
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.alpha
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.draw.clipToBounds
import com.tencent.kuikly.compose.ui.draw.rotate
import com.tencent.kuikly.compose.ui.draw.scale
import com.tencent.kuikly.compose.ui.draw.shadow
import com.tencent.kuikly.compose.ui.geometry.Offset
import com.tencent.kuikly.compose.ui.geometry.Rect
import com.tencent.kuikly.compose.ui.geometry.Size
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.graphics.Path
import com.tencent.kuikly.compose.ui.input.pointer.pointerInput
import com.tencent.kuikly.compose.foundation.gestures.awaitEachGesture
import com.tencent.kuikly.compose.ui.layout.LayoutCoordinates
import com.tencent.kuikly.compose.ui.layout.onSizeChanged
import com.tencent.kuikly.compose.ui.platform.LocalDensity
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.IntSize
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.window.Dialog
import com.tencent.kuikly.compose.ui.window.DialogProperties
import com.tencent.kuikly.compose.ui.platform.LocalConfiguration
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/**
 * Popover 弹出菜单组件 - 参考 KuiklyUI PopMenuDialogDemo 和 AIPopover 实现
 *
 * 关键特性：
 * 1. 基于 Dialog + fillMaxSize Box + offset 实现精确定位
 * 2. 三角形箭头用旋转的正方形实现，外层 Box 裁剪
 * 3. 三角形位置跟随按钮中心，支持边沿处理
 * 4. 使用 shadow + background 实现阴影 + 圆角
 * 5. 半透明全屏蒙层
 *
 * @param isShow 控制显示/隐藏
 * @param clickViewRect 点击按钮的位置区域（用于计算弹出位置）
 * @param popoverWidth 弹窗宽度
 * @param backgroundColor 背景色
 * @param scrimColor 蒙层颜色
 * @param isTriangleTop true=三角形在顶部（菜单在按钮下方），false=三角形在底部（菜单在按钮上方）
 * @param screenEdgePadding 距离屏幕边缘的最小间距
 * @param onDismiss 关闭回调（用户触发关闭时立即调用）
 * @param onAnimationFinished 退出动画完成后的回调（用于清理数据，避免动画跳变）
 */
@Composable
fun AIPopover(
    isShow: MutableState<Boolean>,
    clickViewRect: Rect,
    popoverWidth: Dp = 160.dp,
    backgroundColor: Color = Color.White,
    scrimColor: Color = Color.Transparent,  // 默认透明蒙层
    isTriangleTop: Boolean = false,
    screenEdgePadding: Dp = 12.dp,
    onDismiss: () -> Unit = {},
    onAnimationFinished: () -> Unit = {},  // 新增：退出动画完成后的回调
    content: @Composable BoxScope.() -> Unit
) {
    // 三角形参数 - 参考 AIPopover
    val triangleHeight = 8.dp  // 三角形高度 (dp)
    val triangleBoxSize = (triangleHeight.value * sqrt(2.0)).toFloat().dp  // 旋转正方形尺寸

    // 记录菜单内容高度
    val contentSize = remember { mutableStateOf(IntSize.Zero) }
    val hasMeasured = contentSize.value.height > 0

    // 参考 QQAIBiz AIPopover：使用三个独立的状态控制动画和显示
    val isPopoverScaleShow = remember { mutableStateOf(isShow.value) }
    val isPopoverAlphaShow = remember { mutableStateOf(isShow.value) }
    
    // 当 isShow 变为 true 时，立即显示
    LaunchedEffect(isShow.value) {
        if (isShow.value) {
            isPopoverScaleShow.value = true
            isPopoverAlphaShow.value = true
        }
    }

    // 透明度动画：0f -> 1f (显示), 1f -> 0f (隐藏)
    val alpha by animateFloatAsState(
        targetValue = if (isShow.value) 1f else 0f,
        animationSpec = tween(durationMillis = 200),
        finishedListener = { value ->
            if (value == 0f) {
                isPopoverAlphaShow.value = false
                // 退出动画完成后调用清理回调
                onAnimationFinished()
            }
        }
    )
    
    // 缩放动画：0.1f -> 1f (显示), 1f -> 0.1f (隐藏) - 参考 QQAIBiz
    val scale by animateFloatAsState(
        targetValue = if (isShow.value) 1f else 0.1f,
        animationSpec = tween(durationMillis = 200),
        finishedListener = { value ->
            if (value == 0.1f) {
                isPopoverScaleShow.value = false
            }
        }
    )

    // 只有当所有动画状态都为 false 时才完全隐藏
    if (!isPopoverScaleShow.value && !isPopoverAlphaShow.value) return

    // 参考 PopMenuDialogDemo 的实现方式
    Dialog(
        onDismissRequest = { 
            isShow.value = false
            onDismiss()
        },
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true,
            usePlatformDefaultWidth = false,
            scrimColor = scrimColor
        )
    ) {
        // 获取屏幕密度，用于像素和 dp 的转换
        val density = LocalDensity.current
        
        // 全屏容器 - 点击关闭，并阻塞所有触摸事件（包括滑动）
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    // 消费所有触摸事件，阻止滑动事件透过蒙层
                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            // 消费所有变化，阻止事件传递到下层
                            event.changes.forEach { it.consume() }
                        }
                    }
                }
                .clickable { 
                    isShow.value = false
                    onDismiss()
                }
        ) {
            // clickViewRect 的坐标是像素值，需要转换为 dp
            val popoverWidthPx = with(density) { popoverWidth.toPx() }
            val triangleHeightPx = with(density) { triangleHeight.toPx() }
            val screenEdgePaddingPx = with(density) { screenEdgePadding.toPx() }
            
            // 获取屏幕宽度 - 使用 LocalConfiguration (返回的是 dp 值，需要转为像素)
            val configuration = LocalConfiguration.current
            val screenWidthDp = configuration.pageViewWidth
            val screenWidthPx = with(density) { screenWidthDp.dp.toPx() }
            
            // 按钮中心 X 坐标（像素）
            val buttonCenterX = clickViewRect.left + clickViewRect.width / 2
            
            /**
             * 计算 Popover 的 X 坐标位置（带左右安全距离保护）- 参考 QQAIBiz AIPopover
             * 
             * 直接计算 popover 左边缘的 X 坐标，确保：
             * 1. popover 左边缘 >= minMargin
             * 2. popover 右边缘 <= screenWidth - minMargin
             * 
             * @return popover 左边缘的 X 坐标（像素）
             */
            fun calculatePopoverLeftX(
                buttonCenterX: Float,
                popoverWidth: Float,
                screenWidth: Float,
                minMargin: Float
            ): Float {
                // 理想位置：按钮中心对齐 popover 中心
                val idealLeftX = buttonCenterX - popoverWidth / 2
                
                // 左边界保护：确保 popover 左边不超过安全距离
                val minLeftX = minMargin
                
                // 右边界保护：确保 popover 右边不超过安全距离
                // popover 右边缘 = leftX + popoverWidth <= screenWidth - minMargin
                // 所以 leftX <= screenWidth - minMargin - popoverWidth
                val maxLeftX = screenWidth - minMargin - popoverWidth
                
                // 应用边界限制（确保 maxLeftX >= minLeftX，否则 popover 太宽了）
                val safeMaxLeftX = max(minLeftX, maxLeftX)
                
                return max(minLeftX, min(idealLeftX, safeMaxLeftX))
            }
            
            // 计算 popover 左边缘 X 坐标（带安全距离保护）
            val popoverXPx = calculatePopoverLeftX(
                buttonCenterX = buttonCenterX,
                popoverWidth = popoverWidthPx,
                screenWidth = screenWidthPx,
                minMargin = screenEdgePaddingPx
            )
            
            // 计算三角形相对于 popover 左边缘的偏移量
            // 三角形应该指向按钮中心
            // triangleOffsetX = buttonCenterX - popoverLeftX - triangleHalfWidth
            val triangleHalfWidth = triangleHeightPx  // 三角形宽度 = 高度 * 2，半宽 = 高度
            val rawTriangleOffsetXPx = buttonCenterX - popoverXPx - triangleHalfWidth
            
            // 三角形边界保护：确保三角形不会超出 Popover 的圆角区域
            // 参考 QQAIBiz AIPopover: 三角形偏移量最小 10dp，最大 -10dp（距离边缘）
            // KuiklyUI 的圆角是 12dp，所以三角形至少要距离边缘 cornerRadius 的距离
            val triangleWidth = triangleHeightPx * 2
            val cornerRadiusPx = with(density) { 12.dp.toPx() }  // 圆角半径（与下面 cornerRadius 保持一致）
            val minTriangleOffsetX = cornerRadiusPx  // 三角形最左位置 = 圆角距离
            val maxTriangleOffsetX = popoverWidthPx - triangleWidth - cornerRadiusPx  // 三角形最右位置 = popoverWidth - 三角形宽度 - 圆角距离
            val finalTriangleOffsetXPx = max(minTriangleOffsetX, min(rawTriangleOffsetXPx, maxTriangleOffsetX))
            
            // Y: 
            val contentHeightPx = if (hasMeasured) contentSize.value.height.toFloat() else with(density) { 150.dp.toPx() }
            
            var popoverYPx = if (isTriangleTop) {
                // 菜单在按钮下方，三角形在顶部
                clickViewRect.bottom
            } else {
                // 菜单在按钮上方，三角形在底部
                // 三角形高度是额外的，内容在三角形上面
                clickViewRect.top - contentHeightPx - triangleHeightPx
            }
            
            // 安全检查：确保 Y 坐标不小于 0（否则会显示在屏幕外）
            if (popoverYPx < 0) {
                // 如果上方空间不够，改为显示在按钮下方
                popoverYPx = clickViewRect.bottom
            }
            
            // 将像素转换为 dp
            val popoverX = with(density) { popoverXPx.toDp() }
            val popoverY = with(density) { popoverYPx.toDp() }
            val triangleOffsetX = with(density) { finalTriangleOffsetXPx.toDp() }

            val cornerRadius = 12.dp
            val shape = RoundedCornerShape(cornerRadius)

            // 弹窗容器（菜单 + 三角形）- 使用 offset 定位，添加渐隐+缩放动画
            Column(
                modifier = Modifier
                    .offset(x = popoverX, y = popoverY)
                    .alpha(alpha)
                    .scale(scale)  // 缩放动画 - 参考 QQAIBiz AIPopover
            ) {
                if (isTriangleTop) {
                    // 三角形在顶部，需要偏移到按钮中心位置
                    PopoverTriangle(
                        triangleHeight = triangleHeight,
                        triangleBoxSize = triangleBoxSize,
                        backgroundColor = backgroundColor,
                        isTop = true,
                        offsetX = triangleOffsetX
                    )
                }

                // 菜单内容 - 使用 shadow + background 实现阴影圆角
                Box(
                    modifier = Modifier
                        .width(popoverWidth)
                        .shadow(
                            elevation = 8.dp,
                            shape = shape,
                            clip = false
                        )
                        .clip(shape)
                        .background(backgroundColor)
                        .onSizeChanged { size ->
                            contentSize.value = size
                        }
                        .clickable { /* 阻止穿透 */ }
                ) {
                    // content 是 BoxScope.() -> Unit
                    content()
                }

                if (!isTriangleTop) {
                    // 三角形在底部，需要偏移到按钮中心位置
                    PopoverTriangle(
                        triangleHeight = triangleHeight,
                        triangleBoxSize = triangleBoxSize,
                        backgroundColor = backgroundColor,
                        isTop = false,
                        offsetX = triangleOffsetX
                    )
                }
            }
        }
    }
}

/**
 * Popover 三角形组件 - 使用旋转正方形 + clipToBounds 实现
 * 
 * 实现原理：
 * 1. 外层 Box 尺寸为 (三角形宽度 + 额外边距, 三角形高度)，使用 clipToBounds 裁剪
 * 2. 内层 Box 是一个旋转 45° 的正方形，只露出一半形成三角形
 * 3. 通过 offsetX 参数控制三角形在水平方向的位置
 * 4. 额外边距确保旋转后的正方形角不会超出裁剪区域
 *
 * @param triangleHeight 三角形高度
 * @param triangleBoxSize 旋转正方形的尺寸
 * @param backgroundColor 背景色
 * @param isTop 是否在顶部（true=尖头朝上，false=尖头朝下）
 * @param offsetX 水平偏移量（相对于 popover 左边缘）
 */
@Composable
private fun PopoverTriangle(
    triangleHeight: Dp,
    triangleBoxSize: Dp,
    backgroundColor: Color,
    isTop: Boolean,
    offsetX: Dp = 0.dp
) {
    val triangleWidth = triangleHeight * 2  // 三角形宽度是高度的 2 倍
    
    // 外层容器 - 裁剪超出部分，确保底边平整
    // 注意：宽度需要比三角形宽度稍大，以确保旋转后的正方形被完全裁剪
    Box(
        modifier = Modifier
            .offset(x = offsetX)
            .size(width = triangleWidth, height = triangleHeight)
            .clipToBounds(),  // 关键：裁剪超出部分
        contentAlignment = if (isTop) Alignment.BottomCenter else Alignment.TopCenter
    ) {
        // 内层旋转的正方形
        // 正方形尺寸 = triangleHeight * sqrt(2)，使对角线 = triangleWidth
        // 使用稍小的尺寸（减少 2dp）避免边缘超出产生破窗
        val adjustedBoxSize = triangleBoxSize - 2.dp
        Box(
            modifier = Modifier
                .size(adjustedBoxSize)
                .offset(y = if (isTop) (triangleHeight / 2) else -(triangleHeight / 2))
                .rotate(45f)
                .background(backgroundColor)
        )
    }
}

// ==================== 辅助函数 ====================

/**
 * 从 LayoutCoordinates 创建 Rect
 * 注意：使用 localToRoot 而不是 localToWindow，因为 Kuikly 中 localToWindow 未实现
 */
fun LayoutCoordinates.toRect(): Rect {
    val position = localToRoot(Offset.Zero)
    return Rect(
        offset = position,
        size = Size(size.width.toFloat(), size.height.toFloat())
    )
}
