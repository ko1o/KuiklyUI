package com.tencent.kuikly.demo.pages.compose.chatDemo.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.tencent.kuikly.compose.animation.core.animateFloatAsState
import com.tencent.kuikly.compose.animation.core.tween
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
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.alpha
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.draw.clipToBounds
import com.tencent.kuikly.compose.ui.draw.rotate
import com.tencent.kuikly.compose.ui.draw.shadow
import com.tencent.kuikly.compose.ui.geometry.Offset
import com.tencent.kuikly.compose.ui.geometry.Rect
import com.tencent.kuikly.compose.ui.geometry.Size
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.layout.LayoutCoordinates
import com.tencent.kuikly.compose.ui.layout.onSizeChanged
import com.tencent.kuikly.compose.ui.platform.LocalDensity
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.IntSize
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.window.Dialog
import com.tencent.kuikly.compose.ui.window.DialogProperties
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
 * @param onDismiss 关闭回调
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
    content: @Composable BoxScope.() -> Unit
) {
    if (!isShow.value) return

    // 三角形参数 - 参考 AIPopover
    val triangleHeight = 8.dp  // 三角形高度 (dp)
    val triangleBoxSize = (triangleHeight.value * sqrt(2.0)).toFloat().dp  // 旋转正方形尺寸

    // 记录菜单内容高度
    val contentSize = remember { mutableStateOf(IntSize.Zero) }
    val hasMeasured = contentSize.value.height > 0

    // 渐显动画状态
    val animateTarget = remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (animateTarget.value) 1f else 0f,
        animationSpec = tween(durationMillis = 200)
    )
    
    // 启动渐显动画
    LaunchedEffect(Unit) {
        animateTarget.value = true
    }

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
        
        // 全屏容器 - 点击关闭
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { 
                    isShow.value = false
                    onDismiss()
                }
        ) {
            // clickViewRect 的坐标是像素值，需要转换为 dp
            val popoverWidthPx = with(density) { popoverWidth.toPx() }
            val triangleHeightPx = with(density) { triangleHeight.toPx() }
            val screenEdgePaddingPx = with(density) { screenEdgePadding.toPx() }
            
            // 按钮中心 X 坐标（像素）
            val buttonCenterX = clickViewRect.left + clickViewRect.width / 2
            
            // 计算理想的 popover X 位置（按钮中心对齐 popover 中心）
            var popoverXPx = buttonCenterX - popoverWidthPx / 2
            
            // 边沿处理：确保 popover 不超出屏幕边缘
            // 左边界限制
            if (popoverXPx < screenEdgePaddingPx) {
                popoverXPx = screenEdgePaddingPx
            }
            // 注意：右边界需要知道屏幕宽度，这里先不处理，可以在实际使用时传入
            
            // 计算三角形相对于 popover 左边缘的偏移量（像素）
            // 三角形应该指向按钮中心
            val triangleOffsetXPx = buttonCenterX - popoverXPx - triangleHeightPx
            
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
            val triangleOffsetX = with(density) { triangleOffsetXPx.toDp() }

            val cornerRadius = 12.dp
            val shape = RoundedCornerShape(cornerRadius)

            // 弹窗容器（菜单 + 三角形）- 使用 offset 定位，添加渐显动画
            Column(
                modifier = Modifier
                    .offset(x = popoverX, y = popoverY)
                    .alpha(alpha)
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
 * 1. 外层 Box 尺寸为 (三角形宽度, 三角形高度)，使用 clipToBounds 裁剪
 * 2. 内层 Box 是一个旋转 45° 的正方形，只露出一半形成三角形
 * 3. 通过 offsetX 参数控制三角形在水平方向的位置
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
    Box(
        modifier = Modifier
            .offset(x = offsetX)
            .size(width = triangleWidth, height = triangleHeight)
            .clipToBounds(),  // 关键：裁剪超出部分
        contentAlignment = if (isTop) Alignment.BottomCenter else Alignment.TopCenter
    ) {
        // 内层旋转的正方形
        // 正方形尺寸 = triangleHeight * sqrt(2)，使对角线 = triangleWidth
        Box(
            modifier = Modifier
                .size(triangleBoxSize)
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
