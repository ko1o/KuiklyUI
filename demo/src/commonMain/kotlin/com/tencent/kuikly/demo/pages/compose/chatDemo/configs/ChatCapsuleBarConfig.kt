package com.tencent.kuikly.demo.pages.compose.chatDemo.configs

import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp

// ==================== 胶囊位常量定义（参考 QQAIBiz ChatCapsuleBar） ====================

/** 胶囊栏默认高度 - 参考 QQAIBiz layoutCapsuleBarDefaultHeight = 84f.dp */
val CAPSULE_BAR_DEFAULT_HEIGHT = 84.dp

/** 胶囊栏顶部间距 - 参考 QQAIBiz 悬浮效果，与内容区有间距 */
val CAPSULE_BAR_TOP_PADDING = 12.dp

/** 胶囊栏底部间距（渐变过渡区域） - 参考 QQAIBiz gradientHeight = 12.dp */
val CAPSULE_BAR_BOTTOM_PADDING = 0.dp

/** 胶囊项圆角 - 参考 QQAIBiz borderRadius(12.dp) */
val CAPSULE_ITEM_BORDER_RADIUS = 12.dp

/** 胶囊项最小宽度 - 参考目标截图中近正方形比例 */
val CAPSULE_ITEM_MIN_WIDTH = 90.dp

/** 胶囊项高度 - 参考 QQAIBiz height(72f + aiFontScaleAddSize(14f)) */
val CAPSULE_ITEM_HEIGHT = 72.dp

/** 胶囊项图标大小 - 参考目标截图约 28dp */
val CAPSULE_ITEM_ICON_SIZE = 28.dp

/** 胶囊栏左右内边距 - 参考 QQAIBiz contentPadding left=24, right=24 */
val CAPSULE_BAR_HORIZONTAL_PADDING = 16.dp

/** 胶囊项之间的间距 */
val CAPSULE_ITEM_SPACING = 8.dp

// ==================== 胶囊项类型定义（参考 QQAIBiz CapsuleItem type） ====================

/** 胶囊项类型 */
enum class CapsuleItemType {
    /** 打开 URL 页面 */
    URL_PAGE,
    /** 发送消息 */
    SEND_MSG,
    /** 自定义操作 */
    CUSTOM
}

// ==================== 胶囊项图标（base64 内嵌，与 ExtensionPanel 方案一致） ====================

/**
 * 胶囊栏图标配置 - 使用 base64 Data URI 格式内嵌 SVG 图标
 * 参考 ExtensionPanel.kt 中的 ExtensionPanelIcons 方案
 */
object CapsuleCDN {
    // AI 生图图标 - 复用照片图标 base64 PNG（已在 ExtensionPanel 验证可用）
    const val AI_DRAW = "data:image/png;base64,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"
    // 照片动起来图标 - 复用拍摄图标 base64 PNG（已在 ExtensionPanel 验证可用）
    const val AI_VIDEO = "data:image/png;base64,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"
    // AI 写作图标 - 复用文件图标 base64 PNG（已在 ExtensionPanel 验证可用）
    const val AI_WRITE = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEgAAABIBAMAAACnw650AAAALVBMVEUAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAADBoCg+AAAADnRSTlMAv+8gQIBgkN8QcF9QMELvLO0AAACYSURBVEjH7dRBCoJAGMXxsaJoE0MHiLpAdALxBB0hvE6bjhYdIRURFN4ZXKkPZh7iys33Xw3MD77dc5a1pN0ZQ51EL4yVD2H2oLxACaNWoBMq8j+F/sNze8d7Frkvmnl0AFXkcXT8sKrjyD3BZQEK78EHKCjFVSL+MGTI0IpogyqOUkIJZH5aaI1ynmhRzRt9iZtb5ixrQT0LFzK4ExyT9wAAAABJRU5ErkJggg=="
    // 录音纪要图标 - 复用文档图标 base64 PNG（已在 ExtensionPanel 验证可用）
    const val RECORD_MINUTES = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAEkAAABICAYAAAC6L9h5AAAAAXNSR0IArs4c6QAAAERlWElmTU0AKgAAAAgAAYdpAAQAAAABAAAAGgAAAAAAA6ABAAMAAAABAAEAAKACAAQAAAABAAAASaADAAQAAAABAAAASAAAAAAXl42sAAACvUlEQVR4Ae2ajVEUQRCFEQngQpgM1AgYIhAi8MzADG4zQCPADJQI9jIAI2CNgMsA3ytvrK6pRbdul+l3VnfVY3r/rt98O9P8FCcnEUEgCASBIBAEgkAQCAJBQIDAGh566KmR7lAnQUcTGzhtBcfWeUDddAyUMkxa463zR9S/VAd14wypvJSNMqi+gpQbmO2qmgXUdYPaB5XwgESjHVTg2FGyoXtBIqgMPUAWEnOeS5BM9HBiTebGzhLqEYr1wFyqofeVwYzj1pFQsPZRoG1amxmrV5vLYzc1OtehToFjR/eGrgSJ7+I5UJkXD4nTQx4Sf6aDvwtot5TP/xES2WyheyZLhCqkT5jc2yUmuMRnqEJ6j8mx110uMcm5n6EKiatoBX2DPsyd5NznFSEVQGVuX5FsyoHHqAgpjYDocM4NlCKkPAKJpzrIBdQZqzeKhDrUv+L8Lzd0uJagj1CzaAmJk7qBEpMZscazCbqCdtCLR8vtNmA2FxDHuZHxAT2UoBePlpA4mQFaChS/CzYB1RoS5rUoqNQCVOueREiMAeKKSpCNumd9x8Uv9gaP3AsS5zrsxZyxghITE7fIt+bYJfXYbs9NlD2mjvv6hMdxQJpAXQnSeeV3Wx27HSpBqrfbDzcqVWEVSGNNW6IfkZcKpHoV0VtAIgUT2eQlDUiFxH6Ubdr0p7rdZJq2CqQEIyuaMbE1uXuqsJLGmvbgTsYYUICUjR+mO0imadOQAqQ3NGJCChB9KUCqt5tU0yYkzz+VsD7j3e/hz1duN6lQgDRIERkxo7DdRmxpnQpIE95HQApIEwhMuCVWUkCaQGDCLbGSnCCtJtQ9qluW+GHyZzVj/gufYgyepjKKP4mrnwPo9ZyH988OGF9BGVKMAaauIInfCdcwcgeprKpHePkMJSgiCASBIBAEgkAQCAJBIAj4EPgFBBpGX2nnkv0AAAAASUVORK5CYII="
    // 拍照搜题图标 - 复用拍摄图标 base64 PNG（已在 ExtensionPanel 验证可用）
    const val CAMERA_SEARCH = "data:image/png;base64,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"
}

// ==================== 数据模型（参考 QQAIBiz CapsuleItem） ====================

/**
 * 胶囊项数据 - 参考 QQAIBiz CapsuleItem
 *
 * @param id 唯一标识
 * @param name 显示名称
 * @param icon 图标 URL
 * @param type 类型
 * @param enabled 是否可用
 * @param onClick 点击回调（可选，也可通过外部 onItemClick 统一处理）
 */
data class CapsuleItemConfig(
    val id: String,
    val name: String,
    val icon: String,
    val type: CapsuleItemType = CapsuleItemType.CUSTOM,
    val enabled: Boolean = true
)

// ==================== 胶囊栏整体配置 ====================

/**
 * 胶囊栏整体配置 - 参考 QQAIBiz ChatCapsuleBar 布局
 *
 * @param items 胶囊项列表
 * @param barHeight 胶囊栏高度
 * @param barBackgroundColor 胶囊栏背景色（透明悬浮）
 * @param topPadding 胶囊栏顶部间距
 * @param bottomPadding 胶囊栏底部间距
 * @param itemBackgroundColor 胶囊项背景色
 * @param itemBorderColor 胶囊项边框颜色
 * @param itemBorderWidth 胶囊项边框宽度
 * @param itemTextColor 胶囊项文字颜色
 * @param itemIconTintColor 胶囊项图标着色
 * @param itemMinWidth 胶囊项最小宽度
 * @param itemHeight 胶囊项高度
 * @param iconSize 图标大小
 * @param horizontalPadding 栏左右内边距
 * @param itemSpacing 胶囊项之间间距
 * @param borderRadius 胶囊项圆角
 * @param isDarkMode 是否暗黑模式
 */
data class ChatCapsuleBarConfig(
    val items: List<CapsuleItemConfig> = emptyList(),
    val barHeight: Dp = CAPSULE_BAR_DEFAULT_HEIGHT,
    val barBackgroundColor: Color = Color.Transparent,  // 透明背景，悬浮效果
    val topPadding: Dp = CAPSULE_BAR_TOP_PADDING,       // 顶部间距
    val bottomPadding: Dp = CAPSULE_BAR_BOTTOM_PADDING, // 底部间距
    val itemBackgroundColor: Color = Color.White,
    val itemBorderColor: Color = Color(0xFFE5E5E5),
    val itemBorderWidth: Dp = 0.5.dp,
    val itemTextColor: Color = Color(0xFF333333),
    val itemIconTintColor: Color? = null,
    val itemMinWidth: Dp = CAPSULE_ITEM_MIN_WIDTH,
    val itemHeight: Dp = CAPSULE_ITEM_HEIGHT,
    val iconSize: Dp = CAPSULE_ITEM_ICON_SIZE,
    val horizontalPadding: Dp = CAPSULE_BAR_HORIZONTAL_PADDING,
    val itemSpacing: Dp = CAPSULE_ITEM_SPACING,
    val borderRadius: Dp = CAPSULE_ITEM_BORDER_RADIUS,
    val isDarkMode: Boolean = false
)

// ==================== 便捷构建器 ====================

/**
 * 创建默认的胶囊栏配置 - 包含示例胶囊项
 * 参考 QQAIBiz：胶囊栏悬浮，透明背景
 */
fun defaultCapsuleBarConfig(
    isDarkMode: Boolean = false
): ChatCapsuleBarConfig {
    return ChatCapsuleBarConfig(
        items = defaultCapsuleItems(),
        barBackgroundColor = Color.Transparent,  // 透明背景，实现悬浮效果
        itemBackgroundColor = if (isDarkMode) Color(0xFF2A2C2E) else Color.White,
        itemBorderColor = if (isDarkMode) Color(0xFF3D3D3D) else Color(0xFFE5E5E5),
        itemTextColor = if (isDarkMode) Color(0xFFFFFFFF) else Color(0xFF333333),
        isDarkMode = isDarkMode
    )
}

/**
 * 默认胶囊项列表 - 使用真实 PB 数据
 * 数据来源: capsule_bar_data.json (从 QQAIBiz 导出)
 * 图标使用真实 CDN URL
 */
fun defaultCapsuleItems(): List<CapsuleItemConfig> {
    return listOf(
        CapsuleItemConfig(
            id = "14",
            name = "AI写作",
            icon = "https://bot-resource-1251316161.file.myqcloud.com/media/write.png",
            type = CapsuleItemType.CUSTOM
        ),
        CapsuleItemConfig(
            id = "13",
            name = "AI生图",
            icon = "https://bot-resource-1251316161.file.myqcloud.com/media/draw.png",
            type = CapsuleItemType.CUSTOM
        ),
        CapsuleItemConfig(
            id = "45",
            name = "照片动起来",
            icon = "https://bot-resource-1251316161.file.myqcloud.com/media/video.png",
            type = CapsuleItemType.CUSTOM
        ),
        CapsuleItemConfig(
            id = "24",
            name = "录音纪要",
            icon = "https://bot-resource-1251316161.file.myqcloud.com/media/xiaoq_luyin2.png",
            type = CapsuleItemType.CUSTOM
        ),
        CapsuleItemConfig(
            id = "34",
            name = "拍照答疑",
            icon = "https://bot-resource-1251316161.file.myqcloud.com/media/xiaoq_camera.png",
            type = CapsuleItemType.CUSTOM
        ),
        CapsuleItemConfig(
            id = "17",
            name = "AI翻译",
            icon = "https://bot-resource-1251316161.file.myqcloud.com/media/translate.png",
            type = CapsuleItemType.CUSTOM
        )
    )
}
