package com.tencent.kuikly.demo.pages.compose.chatDemo.widgets

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.foundation.Image
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.Spacer
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.layout.size
import com.tencent.kuikly.compose.foundation.layout.width
import com.tencent.kuikly.compose.foundation.layout.wrapContentHeight
import com.tencent.kuikly.compose.material3.Text
import com.tencent.kuikly.compose.resources.DrawableResource
import com.tencent.kuikly.compose.resources.InternalResourceApi
import com.tencent.kuikly.compose.resources.painterResource
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.demo.pages.compose.chatDemo.configs.*

// ==================== CDN 图片链接（资源数据，保留在 widget 中） ====================

/**
 * 扩展面板图标配置 - base64格式，与 QQAIBiz 保持一致
 */
object ExtensionPanelIcons {
    // 照片图标 - 日间模式
    const val PHOTO_DAY = "data:image/png;base64,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"
    
    // 照片图标 - 夜间模式
    const val PHOTO_NIGHT = "data:image/png;base64,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"
    
    // 拍摄图标 - 日间模式
    const val CAMERA_DAY = "data:image/png;base64,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"
    
    // 拍摄图标 - 夜间模式
    const val CAMERA_NIGHT = "data:image/png;base64,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"
    
    // 文件图标
    const val FILE = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEgAAABIBAMAAACnw650AAAALVBMVEUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADBoCg+AAAADnRSTlMAv+8gQIBgkN8QcF9QMELvLO0AAACYSURBVEjH7dRBCoJAGMXxsaJoE0MHiLpAdALxBB0hvE6bjhYdIRURFN4ZXKkPZh7iys33Xw3MD77dc5a1pN0ZQ51EL4yVD2H2oLxACaNWoBMq8j+F/sNze8d7Frkvmnl0AFXkcXT8sKrjyD3BZQEK78EHKCjFVSL+MGTI0IpogyqOUkIJZH5aaI1ynmhRzRt9iZtb5ixrQT0LFzK4ExyT9wAAAABJRU5ErkJggg=="
    
    // 文档图标
    const val DOCUMENT = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEkAAABICAYAAAC6L9h5AAAAAXNSR0IArs4c6QAAAERlWElmTU0AKgAAAAgAAYdpAAQAAAABAAAAGgAAAAAAA6ABAAMAAAABAAEAAKACAAQAAAABAAAASaADAAQAAAABAAAASAAAAAAXl42sAAACvUlEQVR4Ae2ajVEUQRCFEQngQpgM1AgYIhAi8MzADG4zQCPADJQI9jIAI2CNgMsA3ytvrK6pRbdul+l3VnfVY3r/rt98O9P8FCcnEUEgCASBIBAEgkAQCAJBQIDAGh566KmR7lAnQUcTGzhtBcfWeUDddAyUMkxa463zR9S/VAd14wypvJSNMqi+gpQbmO2qmgXUdYPaB5XwgESjHVTg2FGyoXtBIqgMPUAWEnOeS5BM9HBiTebGzhLqEYr1wFyqofeVwYzj1pFQsPZRoG1amxmrV5vLYzc1OtehToFjR/eGrgSJ7+I5UJkXD4nTQx4Sf6aDvwtot5TP/xES2WyheyZLhCqkT5jc2yUmuMRnqEJ6j8mx110uMcm5n6EKiatoBX2DPsyd5NznFSEVQGVuX5FsyoHHqAgpjYDocM4NlCKkPAKJpzrIBdQZqzeKhDrUv+L8Lzd0uJagj1CzaAmJk7qBEpMZscazCbqCdtCLR8vtNmA2FxDHuZHxAT2UoBePlpA4mQFaChS/CzYB1RoS5rUoqNQCVOueREiMAeKKSpCNumd9x8Uv9gaP3AsS5zrsxZyxghITE7fIt+bYJfXYbs9NlD2mjvv6hMdxQJpAXQnSeeV3Wx27HSpBqrfbDzcqVWEVSGNNW6IfkZcKpHoV0VtAIgUT2eQlDUiFxH6Ubdr0p7rdZJq2CqQEIyuaMbE1uXuqsJLGmvbgTsYYUICUjR+mO0imadOQAqQ3NGJCChB9KUCqt5tU0yYkzz+VsD7j3e/hz1duN6lQgDRIERkxo7DdRmxpnQpIE95HQApIEwhMuCVWUkCaQGDCLbGSnCCtJtQ9qluW+GHyZzVj/gufYgyepjKKP4mrnwPo9ZyH988OGF9BGVKMAaauIInfCdcwcgeprKpHePkMJSgiCASBIBAEgkAQCAJBIAj4EPgFBBpGX2nnkv0AAAAASUVORK5CYII="
    
    fun photo(isDark: Boolean = false) = if (isDark) PHOTO_NIGHT else PHOTO_DAY
    fun camera(isDark: Boolean = false) = if (isDark) CAMERA_NIGHT else CAMERA_DAY
}

// ==================== 组件实现 ====================

/**
 * 扩展面板组件 - 参考 QQAIBiz ExtBottomBar
 * 
 * @param config 面板配置
 * @param appWidth 应用宽度（用于计算每项宽度）
 * @param onItemClick 面板项点击回调
 * @param modifier 修饰符
 */
@Composable
fun ExtensionPanel(
    config: ExtensionPanelConfig = ExtensionPanelConfig(),
    appWidth: Float = 375f,
    onItemClick: (ExtensionPanelItemType) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // 如果有整体 builder，直接调用 builder 渲染
    if (config.builder != null) {
        config.builder.invoke(config, onItemClick)
        return
    }
    
    // 计算每项宽度 - 参考 QQAIBiz: (appWidth - spacing * (columns + 1)) / columns
    val itemWidth = (appWidth - config.contentPadding.value * (config.columns + 1)) / config.columns

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(config.panelHeight)
            .background(config.backgroundColor)
    ) {
        // 顶部分割线 - 参考 QQAIBiz
        Box(
            modifier = Modifier
                .height(config.dividerHeight)
                .fillMaxWidth()
                .background(config.dividerColor)
        )

        // 面板内容 - 参考 QQAIBiz: padding(layoutExtSpacing)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(config.contentPadding),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            config.items.forEach { item ->
                if (config.itemBuilder != null) {
                    config.itemBuilder.invoke(item, config, { onItemClick(item.type) })
                } else {
                    ExtensionPanelItemView(
                        item = item,
                        itemWidth = itemWidth.dp,
                        config = config,
                        onClick = { onItemClick(item.type) }
                    )
                }
            }
        }
    }
}

/**
 * 扩展面板单项视图 - 参考 QQAIBiz ExtBottomBar 中的 Column 布局
 */
@OptIn(InternalResourceApi::class)
@Composable
private fun ExtensionPanelItemView(
    item: ExtensionPanelItem,
    itemWidth: Dp,
    config: ExtensionPanelConfig,
    onClick: () -> Unit
) {
    val textColor = if (item.enabled) config.textColor else config.textDisabledColor

    Column(
        modifier = Modifier
            .width(itemWidth)
            .padding(vertical = config.itemVerticalPadding)
            .clickable(enabled = item.enabled) { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 图标 - 使用 base64 图片
        val drawable = DrawableResource(item.iconUrl)
        Image(
            painter = painterResource(drawable),
            contentDescription = item.title,
            modifier = Modifier.size(config.iconSize)
        )
        
        Spacer(modifier = Modifier.height(config.iconTextSpacing))
        
        // 文字标签
        Text(
            text = item.title,
            fontSize = config.textFontSize,
            color = textColor
        )
    }
}

/**
 * 默认扩展面板项列表
 */
fun defaultExtensionPanelItems(isDarkMode: Boolean = false): List<ExtensionPanelItem> {
    return listOf(
        ExtensionPanelItem(type = ExtensionPanelItemType.PHOTO, title = "照片", iconUrl = ExtensionPanelIcons.photo(isDarkMode)),
        ExtensionPanelItem(type = ExtensionPanelItemType.CAMERA, title = "拍摄", iconUrl = ExtensionPanelIcons.camera(isDarkMode)),
        ExtensionPanelItem(type = ExtensionPanelItemType.FILE, title = "文件", iconUrl = ExtensionPanelIcons.FILE),
        ExtensionPanelItem(type = ExtensionPanelItemType.DOCUMENT, title = "文档", iconUrl = ExtensionPanelIcons.DOCUMENT)
    )
}

/**
 * 创建默认的扩展面板配置
 */
fun defaultExtensionPanelConfig(isDarkMode: Boolean = false): ExtensionPanelConfig {
    return ExtensionPanelConfig(
        items = defaultExtensionPanelItems(isDarkMode),
        backgroundColor = if (isDarkMode) Color(0xFF1A1C1E) else Color.White,
        dividerColor = if (isDarkMode) Color(0xFF3D3D3D) else Color(0xFFE5E5E5),
        textColor = if (isDarkMode) Color(0xFFFFFFFF) else Color(0xFF333333),
        textDisabledColor = if (isDarkMode) Color(0xFF666666) else Color(0xFF999999),
        isDarkMode = isDarkMode
    )
}
