package com.tencent.kuikly.demo.pages.compose.chatDemo.configs

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.unit.Dp
import com.tencent.kuikly.compose.ui.unit.dp

// ==================== 半浮层常量定义（参考 QQAIBiz QueryHalfWriteView） ====================

/** 类型按钮高度 - 参考 QQAIBiz layoutCapsuleTypeButtonHeight = 32.dp */
val HALF_VIEW_TYPE_BUTTON_HEIGHT = 32.dp

/** 半浮层圆角 - 顶部圆角 */
val HALF_VIEW_CORNER_RADIUS = 18.dp

/** 半浮层内边距 */
val HALF_VIEW_HORIZONTAL_PADDING = 24.dp

/** 半浮层标题栏高度 */
val HALF_VIEW_TITLE_HEIGHT = 60.dp

// ==================== 要求项数据（参考 QQAIBiz RequireItem） ====================

/**
 * 要求项 - 下拉菜单中的选项
 * @param id 唯一标识
 * @param name 显示名称
 * @param icon 图标（可选）
 * @param picked 是否选中
 */
data class HalfViewRequireItem(
    val id: String,
    val name: String,
    val icon: String? = null,
    val picked: MutableState<Boolean> = mutableStateOf(false)
) {
    fun reset() {
        picked.value = false
    }
}

// ==================== 要求栏项数据（参考 QQAIBiz RequireBarItem） ====================

/**
 * 要求栏项 - 带下拉菜单的按钮
 * @param id 唯一标识
 * @param buttonName 按钮显示名称（如"类型"、"要求"）
 * @param items 下拉菜单选项列表
 * @param isSelected 是否展开下拉菜单
 * @param enable 是否可用
 * @param display 当前选中项的显示文本
 */
data class HalfViewRequireBarItem(
    val id: String,
    val buttonName: String,
    val items: List<HalfViewRequireItem>,
    val isSelected: MutableState<Boolean> = mutableStateOf(false),
    val enable: MutableState<Boolean> = mutableStateOf(true),
    val display: MutableState<String> = mutableStateOf("")
) {
    fun reset() {
        items.forEach { it.reset() }
        display.value = ""
        isSelected.value = false
    }
    
    /** 获取当前选中的项 */
    fun getSelectedItem(): HalfViewRequireItem? = items.firstOrNull { it.picked.value }
}

// ==================== 类型项数据（参考 QQAIBiz TypeItem） ====================

/**
 * 类型项 - 类型选择按钮
 * @param id 唯一标识
 * @param name 显示名称
 * @param requireBarItems 关联的要求栏项列表
 * @param picked 是否选中
 * @param placeholder 选中时的输入框占位符
 */
data class HalfViewTypeItem(
    val id: String,
    val name: String,
    val requireBarItems: List<HalfViewRequireBarItem> = emptyList(),
    val picked: MutableState<Boolean> = mutableStateOf(false),
    val placeholder: String = ""
) {
    fun reset() {
        requireBarItems.forEach { it.reset() }
        picked.value = false
    }
}

// ==================== 半浮层配置（参考 QQAIBiz AIWriteItem） ====================

/**
 * 胶囊半浮层配置 - 通用配置，支持 AI 写作、AI 生图等多种胶囊
 *
 * @param id 半浮层唯一标识
 * @param title 标题（如"AI写作"）
 * @param titleIcon 标题图标 URL
 * @param typeTitle 类型区域标题（如"类型"）
 * @param requireTitle 要求区域标题（如"要求"）
 * @param typeItems 类型项列表
 * @param defaultTypeItem 默认类型项（未选择任何类型时使用）
 * @param requireBarItems 当前显示的要求栏项列表（根据类型切换）
 * @param placeholder 输入框占位符
 * @param showTypeSection 是否显示类型选择区域
 * @param showRequireSection 是否显示要求选择区域
 * @param typeGridRows 类型网格行数
 */
data class CapsuleHalfViewConfig(
    val id: String,
    val title: String,
    val titleIcon: String = "",
    val typeTitle: String = "类型",
    val requireTitle: String = "要求",
    val typeItems: List<HalfViewTypeItem> = emptyList(),
    val defaultTypeItem: HalfViewTypeItem? = null,
    val requireBarItems: MutableState<List<HalfViewRequireBarItem>> = mutableStateOf(emptyList()),
    val placeholder: String = "",
    val showTypeSection: Boolean = true,
    val showRequireSection: Boolean = true,
    val typeGridRows: Int = 2
) {
    /** 获取当前选中的类型项 */
    fun getSelectedTypeItem(): HalfViewTypeItem? = typeItems.firstOrNull { it.picked.value }
    
    /** 重置所有选择状态 */
    fun reset() {
        typeItems.forEach { it.reset() }
        defaultTypeItem?.let {
            requireBarItems.value = it.requireBarItems
        }
    }
    
    /** 更新要求栏为指定类型的要求 */
    fun updateRequireBarItems(typeItem: HalfViewTypeItem?) {
        requireBarItems.value = typeItem?.requireBarItems ?: defaultTypeItem?.requireBarItems ?: emptyList()
    }
}

// ==================== 从真实 PB JSON 数据构建配置 ====================

/** 从 requireFunction JSON 解析要求栏项列表 */
private fun parseRequireBarItems(requireList: List<Map<String, Any?>>): List<HalfViewRequireBarItem> {
    return requireList.map { rl ->
        val buttonName = rl["buttonName"] as? String ?: ""
        @Suppress("UNCHECKED_CAST")
        val requireDataList = rl["requireData"] as? List<Map<String, Any?>> ?: emptyList()
        HalfViewRequireBarItem(
            id = buttonName,
            buttonName = buttonName,
            items = requireDataList.map { rd ->
                val name = rd["name"] as? String ?: ""
                HalfViewRequireItem(id = name, name = name)
            }
        )
    }
}

/**
 * 创建 AI 写作半浮层配置 - 使用真实 PB 数据
 * 数据来源: capsule_ai_write.json (从 QQAIBiz 导出)
 */
fun createAIWriteHalfViewConfig(): CapsuleHalfViewConfig {
    // 默认要求项: "字数" -> [1000, 2000, 3000]
    val defaultRequireBarItems = listOf(
        HalfViewRequireBarItem(
            id = "字数", buttonName = "字数",
            items = listOf(
                HalfViewRequireItem("1000", "1000"),
                HalfViewRequireItem("2000", "2000"),
                HalfViewRequireItem("3000", "3000")
            )
        )
    )

    val defaultTypeItem = HalfViewTypeItem(
        id = "default", name = "默认",
        requireBarItems = defaultRequireBarItems,
        placeholder = "请输入你要写的主题"
    )

    // 类型选项 - 来自真实 PB 数据（工作提效 + 社媒创作 + 学习教育）
    // 只取前10个常用的展示在2行网格中
    val typeItems = listOf(
        // === 工作提效 ===
        HalfViewTypeItem(
            id = "日/周/月报", name = "日/周/月报",
            requireBarItems = listOf(
                HalfViewRequireBarItem(id = "汇报类型", buttonName = "汇报类型", items = listOf(
                    HalfViewRequireItem("日报", "日报"), HalfViewRequireItem("周报", "周报"),
                    HalfViewRequireItem("月报", "月报"), HalfViewRequireItem("项目汇报", "项目汇报")
                )),
                HalfViewRequireBarItem(id = "字数", buttonName = "字数", items = listOf(
                    HalfViewRequireItem("100", "100"), HalfViewRequireItem("500", "500"),
                    HalfViewRequireItem("1000", "1000"), HalfViewRequireItem("2000", "2000")
                ))
            ),
            placeholder = "请输入你要写的工作进展"
        ),
        HalfViewTypeItem(
            id = "申请书", name = "申请书",
            requireBarItems = listOf(
                HalfViewRequireBarItem(id = "字数", buttonName = "字数", items = listOf(
                    HalfViewRequireItem("100", "100"), HalfViewRequireItem("500", "500"),
                    HalfViewRequireItem("1000", "1000"), HalfViewRequireItem("2000", "2000")
                ))
            ),
            placeholder = "请输入你要写的申请用途"
        ),
        HalfViewTypeItem(
            id = "SWOT分析", name = "SWOT分析",
            requireBarItems = listOf(
                HalfViewRequireBarItem(id = "字数", buttonName = "字数", items = listOf(
                    HalfViewRequireItem("100", "100"), HalfViewRequireItem("500", "500"),
                    HalfViewRequireItem("1000", "1000"), HalfViewRequireItem("2000", "2000")
                ))
            ),
            placeholder = "请输入你要写的主题"
        ),
        // === 社媒创作 ===
        HalfViewTypeItem(
            id = "文章", name = "文章",
            requireBarItems = listOf(
                HalfViewRequireBarItem(id = "发布平台", buttonName = "发布平台", items = listOf(
                    HalfViewRequireItem("公众号", "公众号"), HalfViewRequireItem("知乎", "知乎"),
                    HalfViewRequireItem("头条号", "头条号"), HalfViewRequireItem("百家号", "百家号")
                )),
                HalfViewRequireBarItem(id = "字数", buttonName = "字数", items = listOf(
                    HalfViewRequireItem("500", "500"), HalfViewRequireItem("1000", "1000"),
                    HalfViewRequireItem("2000", "2000"), HalfViewRequireItem("3000", "3000")
                ))
            ),
            placeholder = "请输入你要写的主题"
        ),
        HalfViewTypeItem(
            id = "批量评论", name = "批量评论",
            requireBarItems = listOf(
                HalfViewRequireBarItem(id = "产品类型", buttonName = "产品类型", items = listOf(
                    HalfViewRequireItem("餐厅", "餐厅"), HalfViewRequireItem("住宿酒店", "住宿酒店"),
                    HalfViewRequireItem("电商购物", "电商购物")
                ))
            ),
            placeholder = "请输入你要写的产品名"
        ),
        HalfViewTypeItem(
            id = "策划方案", name = "策划方案",
            requireBarItems = listOf(
                HalfViewRequireBarItem(id = "目标人群", buttonName = "目标人群", items = listOf(
                    HalfViewRequireItem("职场人士", "职场人士"), HalfViewRequireItem("大学生", "大学生"),
                    HalfViewRequireItem("宝妈", "宝妈"), HalfViewRequireItem("中老年", "中老年")
                )),
                HalfViewRequireBarItem(id = "字数", buttonName = "字数", items = listOf(
                    HalfViewRequireItem("1000", "1000"), HalfViewRequireItem("2000", "2000"),
                    HalfViewRequireItem("3000", "3000"), HalfViewRequireItem("5000", "5000")
                ))
            ),
            placeholder = "请输入你要写的主题"
        ),
        HalfViewTypeItem(
            id = "营销文案", name = "营销文案",
            requireBarItems = listOf(
                HalfViewRequireBarItem(id = "目标人群", buttonName = "目标人群", items = listOf(
                    HalfViewRequireItem("职场人士", "职场人士"), HalfViewRequireItem("小镇青年", "小镇青年"),
                    HalfViewRequireItem("大学生", "大学生"), HalfViewRequireItem("宝妈", "宝妈")
                )),
                HalfViewRequireBarItem(id = "营销平台", buttonName = "营销平台", items = listOf(
                    HalfViewRequireItem("小红书", "小红书"), HalfViewRequireItem("大众点评", "大众点评"),
                    HalfViewRequireItem("网店店铺", "网店店铺"), HalfViewRequireItem("朋友圈", "朋友圈"),
                    HalfViewRequireItem("抖音", "抖音")
                ))
            ),
            placeholder = "请输入你要写的产品名"
        ),
        HalfViewTypeItem(
            id = "头脑风暴", name = "头脑风暴",
            requireBarItems = listOf(
                HalfViewRequireBarItem(id = "点子数量", buttonName = "点子数量", items = listOf(
                    HalfViewRequireItem("5", "5"), HalfViewRequireItem("10", "10"),
                    HalfViewRequireItem("20", "20")
                ))
            ),
            placeholder = "请输入你要写的主题"
        ),
        HalfViewTypeItem(
            id = "文本润色", name = "文本润色",
            requireBarItems = listOf(
                HalfViewRequireBarItem(id = "写作风格", buttonName = "写作风格", items = listOf(
                    HalfViewRequireItem("正式", "正式"), HalfViewRequireItem("口语化", "口语化"),
                    HalfViewRequireItem("幽默", "幽默"), HalfViewRequireItem("简洁", "简洁"),
                    HalfViewRequireItem("古风", "古风")
                ))
            ),
            placeholder = "请输入你要写的润色文本"
        ),
        HalfViewTypeItem(
            id = "朋友圈", name = "朋友圈",
            requireBarItems = listOf(
                HalfViewRequireBarItem(id = "字数", buttonName = "字数", items = listOf(
                    HalfViewRequireItem("10", "10"), HalfViewRequireItem("50", "50"),
                    HalfViewRequireItem("100", "100"), HalfViewRequireItem("200", "200")
                )),
                HalfViewRequireBarItem(id = "写作风格", buttonName = "写作风格", items = listOf(
                    HalfViewRequireItem("正式", "正式"), HalfViewRequireItem("口语化", "口语化"),
                    HalfViewRequireItem("幽默", "幽默"), HalfViewRequireItem("简洁", "简洁")
                ))
            ),
            placeholder = "请输入你要写的主题"
        )
    )

    return CapsuleHalfViewConfig(
        id = "ai_write",
        title = "AI写作",
        titleIcon = "https://bot-resource-1251316161.file.myqcloud.com/media/write.png",
        typeTitle = "类型",
        requireTitle = "要求",
        typeItems = typeItems,
        defaultTypeItem = defaultTypeItem,
        requireBarItems = mutableStateOf(defaultRequireBarItems),
        placeholder = "请输入你要写的主题",
        showTypeSection = true,
        showRequireSection = true,
        typeGridRows = 2
    )
}

/**
 * 创建 AI 生图半浮层配置 - 使用真实 PB 数据
 * 数据来源: capsule_ai_draw.json (从 QQAIBiz 导出)
 */
fun createAIDrawHalfViewConfig(): CapsuleHalfViewConfig {
    // 风格数据（取前几个常用风格显示名称）
    val styleRequire = HalfViewRequireBarItem(
        id = "风格", buttonName = "风格",
        items = listOf(
            HalfViewRequireItem("简贴画", "简贴画"),
            HalfViewRequireItem("二次元", "二次元"),
            HalfViewRequireItem("水墨画", "水墨画"),
            HalfViewRequireItem("剪纸风", "剪纸风"),
            HalfViewRequireItem("摄影", "摄影"),
            HalfViewRequireItem("线条风", "线条风"),
            HalfViewRequireItem("3D", "3D"),
            HalfViewRequireItem("童话世界", "童话世界")
        )
    )

    // 比例数据 - 来自真实 PB 数据
    val ratioRequire = HalfViewRequireBarItem(
        id = "比例", buttonName = "比例",
        items = listOf(
            HalfViewRequireItem("1:1", "1:1"),
            HalfViewRequireItem("4:3", "4:3"),
            HalfViewRequireItem("3:4", "3:4"),
            HalfViewRequireItem("16:9", "16:9"),
            HalfViewRequireItem("9:16", "9:16")
        )
    )

    val requireBarItems = listOf(styleRequire, ratioRequire)

    return CapsuleHalfViewConfig(
        id = "ai_draw",
        title = "AI生图",
        titleIcon = "https://bot-resource-1251316161.file.myqcloud.com/media/draw.png",
        typeTitle = "",
        requireTitle = "",
        typeItems = emptyList(),
        requireBarItems = mutableStateOf(requireBarItems),
        placeholder = "描述你想要生成的图片",
        showTypeSection = false,
        showRequireSection = true,
        typeGridRows = 1
    )
}
