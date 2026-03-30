package com.tencent.kuikly.demo.pages.compose.chatDemo.configs

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.TextUnit
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp

// ==================== 常量定义（参考 QQAIBiz ExtBottomBar） ====================

/** 扩展面板高度 */
val LAYOUT_EXT_BOTTOM_HEIGHT = 286f.dp
/** 扩展面板列间距 */
val LAYOUT_EXT_SPACING = 16.dp
/** 扩展面板列数 */
const val LAYOUT_EXT_COLUMNS = 4
/** 图标大小 */
val EXTENSION_ICON_SIZE = 36.dp
/** 图标与文字间距 */
val EXTENSION_ICON_TEXT_SPACING = 9.dp

// ==================== 数据模型 ====================

/**
 * 扩展面板按钮类型
 */
enum class ExtensionPanelItemType {
    PHOTO, CAMERA, FILE, DOCUMENT
}

/**
 * 扩展面板按钮配置
 */
data class ExtensionPanelItem(
    val type: ExtensionPanelItemType,
    val title: String,
    val iconUrl: String,
    val enabled: Boolean = true
)

/**
 * 扩展面板配置
 */
data class ExtensionPanelConfig(
    val items: List<ExtensionPanelItem> = emptyList(),
    val backgroundColor: Color = Color.White,
    val dividerColor: Color = Color(0xFFE5E5E5),
    val textColor: Color = Color(0xFF333333),
    val textDisabledColor: Color = Color(0xFF999999),
    val isDarkMode: Boolean = false,
    val panelHeight: Dp = LAYOUT_EXT_BOTTOM_HEIGHT,
    val dividerHeight: Dp = 0.5.dp,
    val contentPadding: Dp = LAYOUT_EXT_SPACING,
    val columns: Int = LAYOUT_EXT_COLUMNS,
    val iconSize: Dp = EXTENSION_ICON_SIZE,
    val iconTextSpacing: Dp = EXTENSION_ICON_TEXT_SPACING,
    val itemVerticalPadding: Dp = 32.dp,
    val textFontSize: TextUnit = 12.sp,
    val itemBuilder: (@Composable (item: ExtensionPanelItem, config: ExtensionPanelConfig, onClick: () -> Unit) -> Unit)? = null,
    val builder: (@Composable (config: ExtensionPanelConfig, onItemClick: (ExtensionPanelItemType) -> Unit) -> Unit)? = null
)
