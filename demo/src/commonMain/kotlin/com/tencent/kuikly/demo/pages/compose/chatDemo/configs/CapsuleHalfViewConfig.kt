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

/** 风格/比例卡片高度 - 参考 QQAIBiz layoutStyleItemHeight = 80.dp */
val STYLE_CARD_HEIGHT = 80.dp

// ==================== AI画图风格项数据（参考 QQAIBiz StyleItem） ====================

/**
 * 风格项 - AI画图风格卡片
 * @param name 风格名称（如"二次元"、"简贴画"）
 * @param url 风格缩略图 URL
 * @param style 风格标识（用于后台请求）
 * @param picked 是否选中
 */
data class AIDrawStyleItem(
    val name: String,
    val url: String,
    val style: String = "",
    val picked: MutableState<Boolean> = mutableStateOf(false)
) {
    fun reset() {
        picked.value = false
    }
}

/**
 * 比例项 - AI画图比例卡片
 * @param name 比例名称（如"1:1"）
 * @param icon 比例图标 URL
 * @param showName 显示名称（如"1:1 (头像)"）
 * @param subName 子名称（如"头像"）
 * @param picked 是否选中
 */
data class AIDrawRatioItem(
    val name: String,
    val icon: String = "",
    val showName: String = "",
    val subName: String = "",
    val picked: MutableState<Boolean> = mutableStateOf(false)
) {
    fun reset() {
        picked.value = false
    }
}

/**
 * AI画图专属配置
 * @param styleItems 风格列表
 * @param ratioItems 比例列表
 * @param isStyleExpanded 风格区域是否展开
 * @param isRatioExpanded 比例区域是否展开
 */
data class AIDrawConfig(
    val styleItems: List<AIDrawStyleItem> = emptyList(),
    val ratioItems: List<AIDrawRatioItem> = emptyList(),
    val isStyleExpanded: MutableState<Boolean> = mutableStateOf(true),
    val isRatioExpanded: MutableState<Boolean> = mutableStateOf(false)
) {
    fun reset() {
        styleItems.forEach { it.reset() }
        ratioItems.forEach { it.reset() }
        isStyleExpanded.value = true
        isRatioExpanded.value = false
    }
    
    fun getSelectedStyleName(): String = styleItems.firstOrNull { it.picked.value }?.name ?: ""
    fun getSelectedRatioName(): String = ratioItems.firstOrNull { it.picked.value }?.showName ?: ""
}

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
    val typeGridRows: Int = 2,
    /** AI 画图专属配置 - 当 id == "ai_draw" 时使用 */
    val aiDrawConfig: AIDrawConfig? = null
) {
    /** 是否为 AI 画图模式 */
    val isAIDrawMode: Boolean get() = aiDrawConfig != null
    /** 获取当前选中的类型项 */
    fun getSelectedTypeItem(): HalfViewTypeItem? = typeItems.firstOrNull { it.picked.value }
    
    /** 重置所有选择状态 */
    fun reset() {
        typeItems.forEach { it.reset() }
        defaultTypeItem?.let {
            requireBarItems.value = it.requireBarItems
        }
        aiDrawConfig?.reset()
    }
    
    // ==================== 选中条件拼接逻辑（参考 QQAIBiz BabyQCapsuleViewModel） ====================
    
    /**
     * 重新生成带选中条件的输入文本
     * 
     * 参考 QQAIBiz BabyQCapsuleViewModel.reGenerateInputText() 第 228-292 行
     * 
     * AI写作模式：
     * 1. 获取当前选中的类型（如果没有选中，使用默认类型）
     * 2. 将用户输入替换到类型的模板中 ${input}
     * 3. 收集所有选中的要求项的模板并拼接
     * 4. 将要求拼接结果替换到 ${require}
     * 
     * AI画图模式：
     * 1. 如果选中了风格，拼接风格名称
     * 2. 如果选中了比例，拼接比例名称
     * 
     * @param input 用户输入的原始文本
     * @return 拼接了选中条件后的完整文本
     */
    fun reGenerateInputText(input: String): String {
        var fullInput = input
        
        // AI画图模式
        if (isAIDrawMode && aiDrawConfig != null) {
            // 风格
            aiDrawConfig.styleItems.firstOrNull { it.picked.value }?.let {
                val style = "${it.name}风格"
                if (fullInput.isNotEmpty()) {
                    fullInput += "，$style"
                } else {
                    fullInput = "帮我画$style"
                }
            }
            
            // 比例
            aiDrawConfig.ratioItems.firstOrNull { it.picked.value }?.let {
                val ratio = "比例为${it.name}"
                if (fullInput.isNotEmpty()) {
                    fullInput += "，$ratio"
                } else {
                    fullInput = "帮我画$ratio"
                }
            }
            
            // 检查是否需要添加前缀
            if (fullInput.isNotEmpty() && !fullInput.startsWith("帮我画")) {
                fullInput = "帮我画$fullInput"
            }
            
            return fullInput
        }
        
        // AI写作模式（默认）
        // 获取当前选中的类型项，没有选中则使用默认类型项
        val selectedType = getSelectedTypeItem() ?: defaultTypeItem
        
        // 如果有类型模板，将输入替换到模板中
        // 这里简化处理：直接将类型名称作为前缀
        if (selectedType != null && selectedType.id != "default") {
            fullInput = "帮我写一篇${selectedType.name}，主题是：$input"
        }
        
        // 收集所有选中的要求项
        val selectedRequireList = mutableListOf<String>()
        requireBarItems.value.forEach { requireBarItem ->
            requireBarItem.getSelectedItem()?.let {
                // 拼接要求项的名称
                selectedRequireList.add("${requireBarItem.buttonName}：${it.name}")
            }
        }
        
        // 如果有选中的要求，拼接到最终文本中
        if (selectedRequireList.isNotEmpty()) {
            val requireString = selectedRequireList.joinToString("，")
            fullInput += "，$requireString"
        }
        
        return fullInput
    }
    
    /**
     * 获取选中条件的显示文本（用于 UI 展示）
     * 
     * @return 选中条件的简短描述，如 "日/周/月报 | 字数:1000"
     */
    fun getSelectedConditionsDisplay(): String {
        val parts = mutableListOf<String>()
        
        if (isAIDrawMode && aiDrawConfig != null) {
            // AI画图模式：显示风格和比例
            aiDrawConfig.styleItems.firstOrNull { it.picked.value }?.let {
                parts.add(it.name)
            }
            aiDrawConfig.ratioItems.firstOrNull { it.picked.value }?.let {
                parts.add(it.name)
            }
        } else {
            // AI写作模式：显示类型和要求
            getSelectedTypeItem()?.let {
                parts.add(it.name)
            }
            requireBarItems.value.forEach { requireBarItem ->
                requireBarItem.getSelectedItem()?.let {
                    parts.add("${requireBarItem.buttonName}:${it.name}")
                }
            }
        }
        
        return parts.joinToString(" | ")
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

    // 类型选项 - 来自真实 PB 数据（capsule_ai_write.json）
    // 包含工作提效 + 社媒创作 + 学习教育的完整数据
    val typeItems = listOf(
        // === 工作提效 ===
        HalfViewTypeItem(
            id = "日/周/月报", name = "日/周/月报",
            requireBarItems = listOf(
                HalfViewRequireBarItem(id = "汇报类型", buttonName = "汇报类型", items = listOf(
                    HalfViewRequireItem("日报", "日报"), HalfViewRequireItem("周报", "周报"),
                    HalfViewRequireItem("月报", "月报"), HalfViewRequireItem("项目汇报", "项目汇报")
                )),
                HalfViewRequireBarItem(id = "工作业绩", buttonName = "工作业绩", items = listOf(
                    HalfViewRequireItem("销量提升", "销量提升"), HalfViewRequireItem("达成合作", "达成合作"),
                    HalfViewRequireItem("完成汇报", "完成汇报")
                )),
                HalfViewRequireBarItem(id = "项目风险", buttonName = "项目风险", items = listOf(
                    HalfViewRequireItem("可能延期", "可能延期"), HalfViewRequireItem("人力不足", "人力不足"),
                    HalfViewRequireItem("资金不足", "资金不足"), HalfViewRequireItem("合作不顺", "合作不顺")
                )),
                HalfViewRequireBarItem(id = "职业类型", buttonName = "职业类型", items = listOf(
                    HalfViewRequireItem("产品设计类", "产品设计类"), HalfViewRequireItem("研发类", "研发类"),
                    HalfViewRequireItem("运营类", "运营类"), HalfViewRequireItem("客服类", "客服类"),
                    HalfViewRequireItem("市场销售类", "市场销售类"), HalfViewRequireItem("人力资源类", "人力资源类"),
                    HalfViewRequireItem("项目管理类", "项目管理类"), HalfViewRequireItem("教育培训类", "教育培训类")
                )),
                HalfViewRequireBarItem(id = "字数", buttonName = "字数", items = listOf(
                    HalfViewRequireItem("100", "100"), HalfViewRequireItem("500", "500"),
                    HalfViewRequireItem("1000", "1000"), HalfViewRequireItem("2000", "2000")
                )),
                HalfViewRequireBarItem(id = "呈现形式", buttonName = "呈现形式", items = listOf(
                    HalfViewRequireItem("表格", "表格"), HalfViewRequireItem("汇报文档", "汇报文档"),
                    HalfViewRequireItem("邮件", "邮件"), HalfViewRequireItem("微信消息", "微信消息")
                ))
            ),
            placeholder = "请输入你要写的工作进展"
        ),
        HalfViewTypeItem(
            id = "策划方案", name = "策划方案",
            requireBarItems = listOf(
                HalfViewRequireBarItem(id = "目标人群", buttonName = "目标人群", items = listOf(
                    HalfViewRequireItem("职场人士", "职场人士"), HalfViewRequireItem("小镇青年", "小镇青年"),
                    HalfViewRequireItem("大学生", "大学生"), HalfViewRequireItem("宝妈", "宝妈"),
                    HalfViewRequireItem("儿童", "儿童"), HalfViewRequireItem("中老年", "中老年")
                )),
                HalfViewRequireBarItem(id = "方案用途", buttonName = "方案用途", items = listOf(
                    HalfViewRequireItem("营销活动", "营销活动"), HalfViewRequireItem("培训活动", "培训活动"),
                    HalfViewRequireItem("团建活动", "团建活动"), HalfViewRequireItem("公关活动", "公关活动"),
                    HalfViewRequireItem("商业计划书", "商业计划书"), HalfViewRequireItem("工作计划书", "工作计划书")
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
                    HalfViewRequireItem("大学生", "大学生"), HalfViewRequireItem("宝妈", "宝妈"),
                    HalfViewRequireItem("儿童", "儿童"), HalfViewRequireItem("中老年", "中老年")
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
                    HalfViewRequireItem("幽默", "幽默"), HalfViewRequireItem("夸张", "夸张"),
                    HalfViewRequireItem("简洁", "简洁"), HalfViewRequireItem("古风", "古风"),
                    HalfViewRequireItem("抒情", "抒情"), HalfViewRequireItem("讽刺", "讽刺"),
                    HalfViewRequireItem("优美", "优美")
                ))
            ),
            placeholder = "请输入你要写的润色文本"
        ),
        // === 社媒创作 ===
        HalfViewTypeItem(
            id = "文章", name = "文章",
            requireBarItems = listOf(
                HalfViewRequireBarItem(id = "发布平台", buttonName = "发布平台", items = listOf(
                    HalfViewRequireItem("公众号", "公众号"), HalfViewRequireItem("知乎", "知乎"),
                    HalfViewRequireItem("头条号", "头条号"), HalfViewRequireItem("百家号", "百家号")
                )),
                HalfViewRequireBarItem(id = "写作风格", buttonName = "写作风格", items = listOf(
                    HalfViewRequireItem("正式", "正式"), HalfViewRequireItem("口语化", "口语化"),
                    HalfViewRequireItem("幽默", "幽默"), HalfViewRequireItem("夸张", "夸张"),
                    HalfViewRequireItem("简洁", "简洁"), HalfViewRequireItem("古风", "古风"),
                    HalfViewRequireItem("抒情", "抒情"), HalfViewRequireItem("讽刺", "讽刺"),
                    HalfViewRequireItem("优美", "优美")
                )),
                HalfViewRequireBarItem(id = "字数", buttonName = "字数", items = listOf(
                    HalfViewRequireItem("500", "500"), HalfViewRequireItem("1000", "1000"),
                    HalfViewRequireItem("2000", "2000"), HalfViewRequireItem("3000", "3000"),
                    HalfViewRequireItem("5000", "5000")
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
                )),
                HalfViewRequireBarItem(id = "产品描述", buttonName = "产品描述", items = listOf(
                    HalfViewRequireItem("品质很好", "品质很好"), HalfViewRequireItem("环境很好", "环境很好"),
                    HalfViewRequireItem("服务很好", "服务很好")
                ))
            ),
            placeholder = "请输入你要写的产品名"
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
                    HalfViewRequireItem("幽默", "幽默"), HalfViewRequireItem("夸张", "夸张"),
                    HalfViewRequireItem("简洁", "简洁"), HalfViewRequireItem("古风", "古风"),
                    HalfViewRequireItem("抒情", "抒情"), HalfViewRequireItem("讽刺", "讽刺"),
                    HalfViewRequireItem("优美", "优美")
                ))
            ),
            placeholder = "请输入你要写的主题"
        ),
        HalfViewTypeItem(
            id = "小红书", name = "小红书",
            requireBarItems = listOf(
                HalfViewRequireBarItem(id = "字数", buttonName = "字数", items = listOf(
                    HalfViewRequireItem("10", "10"), HalfViewRequireItem("50", "50"),
                    HalfViewRequireItem("100", "100"), HalfViewRequireItem("200", "200")
                )),
                HalfViewRequireBarItem(id = "写作风格", buttonName = "写作风格", items = listOf(
                    HalfViewRequireItem("正式", "正式"), HalfViewRequireItem("口语化", "口语化"),
                    HalfViewRequireItem("幽默", "幽默"), HalfViewRequireItem("夸张", "夸张"),
                    HalfViewRequireItem("简洁", "简洁"), HalfViewRequireItem("古风", "古风"),
                    HalfViewRequireItem("抒情", "抒情"), HalfViewRequireItem("讽刺", "讽刺"),
                    HalfViewRequireItem("优美", "优美")
                ))
            ),
            placeholder = "请输入你要写的主题"
        ),
        HalfViewTypeItem(
            id = "短视频脚本", name = "短视频脚本",
            requireBarItems = listOf(
                HalfViewRequireBarItem(id = "目标人群", buttonName = "目标人群", items = listOf(
                    HalfViewRequireItem("职场人士", "职场人士"), HalfViewRequireItem("小镇青年", "小镇青年"),
                    HalfViewRequireItem("大学生", "大学生"), HalfViewRequireItem("宝妈", "宝妈"),
                    HalfViewRequireItem("儿童", "儿童"), HalfViewRequireItem("中老年", "中老年")
                )),
                HalfViewRequireBarItem(id = "叙述角度", buttonName = "叙述角度", items = listOf(
                    HalfViewRequireItem("第一人称", "第一人称"), HalfViewRequireItem("第三人称", "第三人称")
                ))
            ),
            placeholder = "请输入你要写的主题"
        ),
        // === 学习教育 ===
        HalfViewTypeItem(
            id = "作文", name = "作文",
            requireBarItems = listOf(
                HalfViewRequireBarItem(id = "学段", buttonName = "学段", items = listOf(
                    HalfViewRequireItem("小学低年级", "小学低年级"), HalfViewRequireItem("小学高年级", "小学高年级"),
                    HalfViewRequireItem("初中", "初中"), HalfViewRequireItem("高中", "高中"),
                    HalfViewRequireItem("大学", "大学")
                )),
                HalfViewRequireBarItem(id = "文体", buttonName = "文体", items = listOf(
                    HalfViewRequireItem("不限", "不限"), HalfViewRequireItem("记叙文", "记叙文"),
                    HalfViewRequireItem("议论文", "议论文"), HalfViewRequireItem("说明文", "说明文"),
                    HalfViewRequireItem("散文", "散文")
                )),
                HalfViewRequireBarItem(id = "字数", buttonName = "字数", items = listOf(
                    HalfViewRequireItem("300", "300"), HalfViewRequireItem("500", "500"),
                    HalfViewRequireItem("800", "800"), HalfViewRequireItem("1000", "1000")
                ))
            ),
            placeholder = "请输入你要写的主题"
        ),
        HalfViewTypeItem(
            id = "论文", name = "论文",
            requireBarItems = listOf(
                HalfViewRequireBarItem(id = "字数", buttonName = "字数", items = listOf(
                    HalfViewRequireItem("1000", "1000"), HalfViewRequireItem("2000", "2000"),
                    HalfViewRequireItem("3000", "3000"), HalfViewRequireItem("5000", "5000"),
                    HalfViewRequireItem("10000", "10000")
                ))
            ),
            placeholder = "请输入你要写的主题"
        ),
        HalfViewTypeItem(
            id = "演讲稿", name = "演讲稿",
            requireBarItems = listOf(
                HalfViewRequireBarItem(id = "演讲者角度", buttonName = "演讲者角度", items = listOf(
                    HalfViewRequireItem("主持人", "主持人"), HalfViewRequireItem("领导", "领导"),
                    HalfViewRequireItem("校长", "校长"), HalfViewRequireItem("老师", "老师"),
                    HalfViewRequireItem("学生", "学生"), HalfViewRequireItem("小朋友", "小朋友")
                )),
                HalfViewRequireBarItem(id = "写作风格", buttonName = "写作风格", items = listOf(
                    HalfViewRequireItem("正式", "正式"), HalfViewRequireItem("口语化", "口语化"),
                    HalfViewRequireItem("幽默", "幽默"), HalfViewRequireItem("夸张", "夸张"),
                    HalfViewRequireItem("简洁", "简洁"), HalfViewRequireItem("古风", "古风"),
                    HalfViewRequireItem("抒情", "抒情"), HalfViewRequireItem("讽刺", "讽刺"),
                    HalfViewRequireItem("优美", "优美")
                )),
                HalfViewRequireBarItem(id = "字数", buttonName = "字数", items = listOf(
                    HalfViewRequireItem("100", "100"), HalfViewRequireItem("500", "500"),
                    HalfViewRequireItem("1000", "1000"), HalfViewRequireItem("2000", "2000")
                ))
            ),
            placeholder = "请输入你要写的主题"
        ),
        HalfViewTypeItem(
            id = "课题报告", name = "课题报告",
            requireBarItems = listOf(
                HalfViewRequireBarItem(id = "字数", buttonName = "字数", items = listOf(
                    HalfViewRequireItem("1000", "1000"), HalfViewRequireItem("2000", "2000"),
                    HalfViewRequireItem("3000", "3000"), HalfViewRequireItem("5000", "5000"),
                    HalfViewRequireItem("10000", "10000")
                ))
            ),
            placeholder = "请输入你要写的主题"
        ),
        HalfViewTypeItem(
            id = "教案", name = "教案",
            requireBarItems = listOf(
                HalfViewRequireBarItem(id = "学科", buttonName = "学科", items = listOf(
                    HalfViewRequireItem("语文", "语文"), HalfViewRequireItem("数学", "数学"),
                    HalfViewRequireItem("英语", "英语"), HalfViewRequireItem("思想政治", "思想政治"),
                    HalfViewRequireItem("历史", "历史"), HalfViewRequireItem("地理", "地理"),
                    HalfViewRequireItem("美术", "美术"), HalfViewRequireItem("音乐", "音乐"),
                    HalfViewRequireItem("物理", "物理"), HalfViewRequireItem("化学", "化学")
                )),
                HalfViewRequireBarItem(id = "学段", buttonName = "学段", items = listOf(
                    HalfViewRequireItem("小学低年级", "小学低年级"), HalfViewRequireItem("小学高年级", "小学高年级"),
                    HalfViewRequireItem("初中", "初中"), HalfViewRequireItem("高中", "高中"),
                    HalfViewRequireItem("大学", "大学")
                )),
                HalfViewRequireBarItem(id = "呈现形式", buttonName = "呈现形式", items = listOf(
                    HalfViewRequireItem("表格", "表格"), HalfViewRequireItem("汇报文档", "汇报文档"),
                    HalfViewRequireItem("邮件", "邮件"), HalfViewRequireItem("微信消息", "微信消息")
                )),
                HalfViewRequireBarItem(id = "字数", buttonName = "字数", items = listOf(
                    HalfViewRequireItem("500", "500"), HalfViewRequireItem("1000", "1000"),
                    HalfViewRequireItem("2000", "2000"), HalfViewRequireItem("3000", "3000"),
                    HalfViewRequireItem("5000", "5000")
                ))
            ),
            placeholder = "请输入你要写的主题"
        ),
        HalfViewTypeItem(
            id = "学习心得", name = "学习心得",
            requireBarItems = listOf(
                HalfViewRequireBarItem(id = "写作风格", buttonName = "写作风格", items = listOf(
                    HalfViewRequireItem("正式", "正式"), HalfViewRequireItem("口语化", "口语化"),
                    HalfViewRequireItem("幽默", "幽默"), HalfViewRequireItem("夸张", "夸张"),
                    HalfViewRequireItem("简洁", "简洁"), HalfViewRequireItem("古风", "古风"),
                    HalfViewRequireItem("抒情", "抒情"), HalfViewRequireItem("讽刺", "讽刺"),
                    HalfViewRequireItem("优美", "优美")
                )),
                HalfViewRequireBarItem(id = "字数", buttonName = "字数", items = listOf(
                    HalfViewRequireItem("100", "100"), HalfViewRequireItem("500", "500"),
                    HalfViewRequireItem("1000", "1000"), HalfViewRequireItem("2000", "2000")
                ))
            ),
            placeholder = "请输入你要写的主题"
        ),
        HalfViewTypeItem(
            id = "儿童故事", name = "儿童故事",
            requireBarItems = listOf(
                HalfViewRequireBarItem(id = "故事角色", buttonName = "故事角色", items = listOf(
                    HalfViewRequireItem("小动物", "小动物"), HalfViewRequireItem("小朋友", "小朋友"),
                    HalfViewRequireItem("神话人物", "神话人物"), HalfViewRequireItem("古代人物", "古代人物"),
                    HalfViewRequireItem("英雄人物", "英雄人物")
                )),
                HalfViewRequireBarItem(id = "叙述角度", buttonName = "叙述角度", items = listOf(
                    HalfViewRequireItem("第一人称", "第一人称"), HalfViewRequireItem("第三人称", "第三人称")
                )),
                HalfViewRequireBarItem(id = "字数", buttonName = "字数", items = listOf(
                    HalfViewRequireItem("100", "100"), HalfViewRequireItem("500", "500"),
                    HalfViewRequireItem("1000", "1000"), HalfViewRequireItem("2000", "2000")
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
 * 
 * 参考 QQAIBiz QueryHalfDrawView：
 * - 风格区域：横滑卡片网格，每个卡片有缩略图 + 底部渐变 + 名称
 * - 比例区域：横滑卡片网格，每个卡片有图标 + 子名称
 * - 风格/比例通过展开按钮互斥切换
 */
fun createAIDrawHalfViewConfig(): CapsuleHalfViewConfig {
    // 风格数据 - 来自真实 PB 数据（capsule_ai_draw.json 中的 styleFunction.styleList）
    // 共 51 个风格，按 JSON 中的顺序排列
    val styleItems = listOf(
        AIDrawStyleItem("简贴画", "https://bot-resource-1251316161.file.myqcloud.com/babyq_startup/MALRF0jjwquA7Y60/%E7%AE%80%E8%B4%B4%E7%94%BB.png", "简贴画"),
        AIDrawStyleItem("二次元", "https://static-res.qq.com/static-res/ai_draw/4.png", "二次元风格"),
        AIDrawStyleItem("水墨画", "https://static-res.qq.com/static-res/ai_draw/19.png", "水墨风格"),
        AIDrawStyleItem("剪纸风", "https://bot-resource-1251316161.file.myqcloud.com/babyq_startup/AcDGfvOdhmtSLy7J/%E5%89%AA%E7%BA%B8%E9%A3%8E.png", "剪纸风"),
        AIDrawStyleItem("摄影", "https://static-res.qq.com/static-res/ai_draw/1.png", "摄影风格"),
        AIDrawStyleItem("线条风", "https://bot-resource-1251316161.file.myqcloud.com/babyq_startup/dr1tKeVtAGKRoyfq/%E7%BA%BF%E6%9D%A1%E9%A3%8E.png", "线条"),
        AIDrawStyleItem("圆润风", "https://bot-resource-1251316161.file.myqcloud.com/babyq_startup/Brug4Le5kPzFm6xo/%E5%9C%86%E6%B6%A6%E9%A3%8E.png", "圆润风"),
        AIDrawStyleItem("清新日漫", "https://static-res.qq.com/static-res/ai_draw/6.png", "清新日漫风格"),
        AIDrawStyleItem("3D", "https://static-res.qq.com/static-res/ai_draw/7.png", "3D风格"),
        AIDrawStyleItem("童话世界", "https://static-res.qq.com/static-res/ai_draw/2.png", "童话世界风格"),
        AIDrawStyleItem("几何风", "https://bot-resource-1251316161.file.myqcloud.com/babyq_startup/MA05Gktf61EspNZj/%E5%87%A0%E4%BD%95%E9%A3%8E.png", "几何风"),
        AIDrawStyleItem("像素艺术", "https://bot-resource-1251316161.file.myqcloud.com/babyq_startup/faDfwTYbMI0v9Cnm/%E5%83%8F%E7%B4%A0%E8%89%BA%E6%9C%AF.png", "像素"),
        AIDrawStyleItem("纯真动漫", "https://static-res.qq.com/static-res/ai_draw/5.png", "纯真动漫风格"),
        AIDrawStyleItem("糖果色", "https://static-res.qq.com/static-res/ai_draw/16.png", "糖果色风格"),
        AIDrawStyleItem("中国风", "https://static-res.qq.com/static-res/ai_draw/14.png", "中国风风格"),
        AIDrawStyleItem("简线画", "https://bot-resource-1251316161.file.myqcloud.com/babyq_startup/0xVjayVW3jVuTYIP/%E7%AE%80%E7%BA%BF%E7%94%BB.png", "简线画"),
        AIDrawStyleItem("国潮", "https://static-res.qq.com/static-res/ai_draw/15.png", "国潮风格"),
        AIDrawStyleItem("暗黑系", "https://static-res.qq.com/static-res/ai_draw/12.png", "暗黑风格"),
        AIDrawStyleItem("酷海报", "https://bot-resource-1251316161.file.myqcloud.com/babyq_startup/L0rZ3IDMyuwGeSUV/%E9%85%B7%E6%B5%B7%E6%8A%A5.png", "酷海报"),
        AIDrawStyleItem("积木世界", "https://bot-resource-1251316161.file.myqcloud.com/babyq_startup/eKLTWKy9kuPgtiBW/%E7%A7%AF%E6%9C%A8%E4%B8%96%E7%95%8C.png", "乐高"),
        AIDrawStyleItem("赛博朋克", "https://static-res.qq.com/static-res/ai_draw/8.png", "赛博朋克风格"),
        AIDrawStyleItem("美式卡通", "https://bot-resource-1251316161.file.myqcloud.com/babyq_startup/3rct1Q3lhEzzabes/%E7%BE%8E%E5%BC%8F%E5%8D%A1%E9%80%9A.png", "美式卡通"),
        AIDrawStyleItem("奇趣卡通", "https://static-res.qq.com/static-res/ai_draw/3.png", "奇趣卡通风格"),
        AIDrawStyleItem("日式手绘", "https://bot-resource-1251316161.file.myqcloud.com/babyq_startup/pnqoloKueZ3fbBl5/%E6%97%A5%E5%BC%8F%E6%89%8B%E7%BB%98.png", "吉卜力"),
        AIDrawStyleItem("软陶塑感", "https://bot-resource-1251316161.file.myqcloud.com/babyq_startup/XkZZsVvnB3quGR9I/%E8%BD%AF%E9%99%B6%E5%A1%91%E6%84%9F.png", "软陶塑感"),
        AIDrawStyleItem("热血漫", "https://bot-resource-1251316161.file.myqcloud.com/babyq_startup/W3KLBCI1PYzhjdih/%E7%83%AD%E8%A1%80%E6%BC%AB.png", "热血漫"),
        AIDrawStyleItem("素描", "https://static-res.qq.com/static-res/ai_draw/18.png", "素描风格"),
        AIDrawStyleItem("极简", "https://static-res.qq.com/static-res/ai_draw/10.png", "极简风格"),
        AIDrawStyleItem("彩铅", "https://static-res.qq.com/static-res/ai_draw/28.png", "彩铅风格"),
        AIDrawStyleItem("复古杂志", "https://bot-resource-1251316161.file.myqcloud.com/babyq_startup/3UudGTvygEBgzr8L/%E5%A4%8D%E5%8F%A4%E6%9D%82%E5%BF%97.png", "复古杂志"),
        AIDrawStyleItem("毛毡", "https://static-res.qq.com/static-res/ai_draw/24.png", "毛毡风格"),
        AIDrawStyleItem("黏土玩具", "https://bot-resource-1251316161.file.myqcloud.com/babyq_startup/aEb6IqfhRFElsnIB/%E9%BB%8F%E5%9C%9F%E7%8E%A9%E5%85%B7.png", "粘土"),
        AIDrawStyleItem("油画", "https://static-res.qq.com/static-res/ai_draw/20.png", "油画风格"),
        AIDrawStyleItem("刺绣", "https://static-res.qq.com/static-res/ai_draw/27.png", "刺绣风格"),
        AIDrawStyleItem("莫奈", "https://static-res.qq.com/static-res/ai_draw/30.png", "莫奈风格"),
        AIDrawStyleItem("穆夏", "https://static-res.qq.com/static-res/ai_draw/31.png", "穆夏风格"),
        AIDrawStyleItem("童趣手绘", "https://bot-resource-1251316161.file.myqcloud.com/babyq_startup/o52jWw8x0yRVMGO1/%E7%AB%A5%E8%B6%A3%E6%89%8B%E7%BB%98.png", "童趣手绘"),
        AIDrawStyleItem("梵高", "https://static-res.qq.com/static-res/ai_draw/29.png", "梵高风格"),
        AIDrawStyleItem("平滑插画", "https://bot-resource-1251316161.file.myqcloud.com/babyq_startup/kshjFkJN15IrQows/%E5%B9%B3%E6%BB%91%E6%8F%92%E7%94%BB.png", "平滑插画"),
        AIDrawStyleItem("绘本插画", "https://bot-resource-1251316161.file.myqcloud.com/babyq_startup/FZXor2Y1IyhSjRIK/%E7%BB%98%E6%9C%AC%E6%8F%92%E7%94%BB.png", "绘本插画"),
        AIDrawStyleItem("折纸风", "https://bot-resource-1251316161.file.myqcloud.com/babyq_startup/UzoD77GU2MydioLq/%E6%8A%98%E7%BA%B8%E9%A3%8E.png", "折纸风"),
        AIDrawStyleItem("羊毛毡艺", "https://bot-resource-1251316161.file.myqcloud.com/babyq_startup/q0j4dW6MWUlxmpnP/%E7%BE%8A%E6%AF%9B%E6%AF%A1%E8%89%BA.png", "羊毛毡艺"),
        AIDrawStyleItem("胶片电影", "https://static-res.qq.com/static-res/ai_draw/17.png", "胶片电影风格"),
        AIDrawStyleItem("色块线描", "https://bot-resource-1251316161.file.myqcloud.com/babyq_startup/FghS5CLWl3ZxCdyM/%E8%89%B2%E5%9D%97%E7%BA%BF%E6%8F%8F.png", "色块线描"),
        AIDrawStyleItem("水彩", "https://static-res.qq.com/static-res/ai_draw/21.png", "水彩风格"),
        AIDrawStyleItem("波普风", "https://bot-resource-1251316161.file.myqcloud.com/babyq_startup/TndqGoKYKs4crGNJ/%E6%B3%A2%E6%99%AE%E9%A3%8E.png", "波普风"),
        AIDrawStyleItem("科幻癫疯", "https://bot-resource-1251316161.file.myqcloud.com/babyq_startup/I48ILjrx9LBOQyHy/%E7%A7%91%E5%B9%BB%E7%99%AB%E7%96%AF.png", "瑞克和莫蒂"),
        AIDrawStyleItem("复古", "https://static-res.qq.com/static-res/ai_draw/11.png", "复古风格"),
        AIDrawStyleItem("粉笔", "https://static-res.qq.com/static-res/ai_draw/22.png", "粉笔风格"),
        AIDrawStyleItem("毕加索", "https://bot-resource-1251316161.file.myqcloud.com/babyq_startup/prPolFhe6FE0K9wH/%E6%AF%95%E5%8A%A0%E7%B4%A2.png", "毕加索"),
        AIDrawStyleItem("贴纸", "https://static-res.qq.com/static-res/ai_draw/25.png", "贴纸风格"),
    )

    // 比例数据 - 来自真实 PB 数据（capsule_ai_draw.json 中的 requireFunction.requireList）
    val ratioItems = listOf(
        AIDrawRatioItem("1:1", "https://tianquan.gtimg.cn/shoal/vaclient/3987f580-e05e-43d0-9e9d-a6e2e0d9bd60.png", "1:1 (头像)", "头像"),
        AIDrawRatioItem("4:3", "https://tianquan.gtimg.cn/shoal/vaclient/7cafb94b-c30b-4f0c-829b-915f683f7b40.png", "4:3 (插画配图)", "插画配图"),
        AIDrawRatioItem("3:4", "https://tianquan.gtimg.cn/shoal/vaclient/d8aa1996-a273-4f68-a3c5-8b640d3c47a0.png", "3:4 (自拍写真)", "自拍写真"),
        AIDrawRatioItem("16:9", "https://tianquan.gtimg.cn/shoal/vaclient/0a0b7879-50ca-4f5f-834c-93225d4696ad.png", "16:9 (桌面壁纸)", "桌面壁纸"),
        AIDrawRatioItem("9:16", "https://tianquan.gtimg.cn/shoal/vaclient/6d288e4e-dc77-452b-9f5f-6da1c183028e.png", "9:16 (手机壁纸)", "手机壁纸"),
    )

    return CapsuleHalfViewConfig(
        id = "ai_draw",
        title = "AI生图",
        titleIcon = "https://bot-resource-1251316161.file.myqcloud.com/media/draw.png",
        typeTitle = "",
        requireTitle = "",
        typeItems = emptyList(),
        requireBarItems = mutableStateOf(emptyList()),
        placeholder = "描述你想要生成的图片",
        showTypeSection = false,
        showRequireSection = false,
        typeGridRows = 1,
        aiDrawConfig = AIDrawConfig(
            styleItems = styleItems,
            ratioItems = ratioItems
        )
    )
}
