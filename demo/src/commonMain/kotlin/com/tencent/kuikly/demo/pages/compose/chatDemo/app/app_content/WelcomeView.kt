package com.tencent.kuikly.demo.pages.compose.chatDemo.app.app_content

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.foundation.Image
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.PaddingValues
import com.tencent.kuikly.compose.foundation.layout.Spacer
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.layout.width
import com.tencent.kuikly.compose.foundation.lazy.LazyColumn
import com.tencent.kuikly.compose.foundation.lazy.LazyListState
import com.tencent.kuikly.compose.foundation.lazy.items
import com.tencent.kuikly.compose.foundation.lazy.rememberLazyListState
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
import com.tencent.kuikly.core.base.attr.ImageUri

/**
 * 欢迎页组件
 * 
 * 显示 Logo 和预设的快捷提示卡片
 */
@OptIn(InternalResourceApi::class)
@Composable
fun WelcomeView(
    onInputTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    // 预设的快捷提示卡片
    val promptBoxes = listOf(
        PromptBox(
            title = "\uD83C\uDF93 高考志愿分析",
            prompt = "请帮我分析高考志愿填报方案，结合我的成绩和兴趣给出建议",
            subtitle = "高考之路，有我护航",
            startColor = Color(0xFFCDC4BB)
        ),
        PromptBox(
            title = "\u26BD 世界杯观赛助手",
            prompt = "分析今天的世界杯战况如何",
            subtitle = "分析比赛战况",
            startColor = Color(0xFFFEE1D3)
        ),
        PromptBox(
            title = "\u2600\uFE0F 医学健康助手",
            prompt = "请给出健康生活建议",
            subtitle = "专业、科学",
            startColor = Color(0xFFF6BEBD)
        ),
        PromptBox(
            title = "\uD83C\uDF89 高考送祝福",
            prompt = "请写一段高考祝福语，祝考生金榜题名",
            subtitle = "祝各位考生金榜题名",
            startColor = Color(0xFFCFAAA1)
        ),
        PromptBox(
            title = "\uD83D\uDCDA 学习计划助手",
            prompt = "帮我制定一个高效的学习计划，提升学习效率",
            subtitle = "科学规划，高效学习",
            startColor = Color(0xFFD4E4F7)
        ),
        PromptBox(
            title = "\uD83C\uDFA8 创意写作助手",
            prompt = "帮我写一篇富有创意的短文或故事",
            subtitle = "激发灵感，妙笔生花",
            startColor = Color(0xFFE8D5F2)
        )
    )

    LazyColumn(
        modifier = modifier,
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = contentPadding
    ) {
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
        
        item {
            // Logo
            val logoDrawable = DrawableResource(ImageUri.pageAssets(LOGO_ICON).toUrl("ChatDemo"))
            Image(
                painter = painterResource(logoDrawable),
                contentDescription = "Kuikly Logo",
                modifier = Modifier
                    .width(240.dp)
                    .height(70.dp)
            )
        }
        
        item {
            Spacer(modifier = Modifier.height(40.dp))
        }

        // 快捷提示卡片列表
        items(promptBoxes) { box ->
            PromptCard(
                box = box,
                onClick = { onInputTextChange(box.prompt) }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
        
        item {
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

/**
 * 快捷提示卡片
 */
@Composable
private fun PromptCard(
    box: PromptBox,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    com.tencent.kuikly.compose.ui.graphics.Brush.Companion.horizontalGradient(
                        colors = listOf(box.startColor, box.endColor)
                    )
                )
                .clickable { onClick() }
                .padding(vertical = 16.dp, horizontal = 18.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = box.title,
                    fontSize = 20.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = box.subtitle,
                    fontSize = 15.sp,
                    color = Color.Black.copy(alpha = 0.9f)
                )
            }
        }
    }
}

/**
 * 快捷提示数据类
 */
data class PromptBox(
    val title: String,
    val prompt: String,
    val subtitle: String = "",
    val startColor: Color = Color.White,
    val endColor: Color = Color.White
)

// ==================== 常量定义 ====================

private const val LOGO_ICON = "kuikly_logo.png"
