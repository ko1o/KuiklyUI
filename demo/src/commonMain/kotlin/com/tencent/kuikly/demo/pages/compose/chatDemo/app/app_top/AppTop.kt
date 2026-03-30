package com.tencent.kuikly.demo.pages.compose.chatDemo.app.app_top

import androidx.compose.runtime.Composable
import com.tencent.kuikly.compose.foundation.Image
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.Spacer
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.layout.size
import com.tencent.kuikly.compose.foundation.layout.width
import com.tencent.kuikly.compose.material3.Text
import com.tencent.kuikly.compose.resources.DrawableResource
import com.tencent.kuikly.compose.resources.InternalResourceApi
import com.tencent.kuikly.compose.resources.painterResource
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.text.font.FontWeight
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.tencent.kuikly.core.base.attr.ImageUri

/**
 * 顶部区域组件
 * 
 * 参考 QQAIBiz 的 AppTop 结构：
 * - 状态栏占位
 * - 导航栏（返回按钮、标题、可选右侧按钮）
 * - 分割线
 */
@Composable
fun AppTop(
    statusBarHeight: Float,
    onBack: () -> Unit,
    title: String = "AI Chat",
    modifier: Modifier = Modifier
) {
    // 状态栏占位
    Spacer(modifier = Modifier.height(statusBarHeight.dp))

    // 导航栏
    NavBar(
        title = title,
        onBack = onBack,
        modifier = modifier
    )

    // 分割线
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFFE3E3E3))
    )
}

/**
 * 导航栏组件
 * 
 * 布局：左侧返回按钮 - 中间标题 - 右侧占位
 */
@OptIn(InternalResourceApi::class)
@Composable
private fun NavBar(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(44.dp)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 返回按钮
        val drawable = DrawableResource(ImageUri.pageAssets(BACK_ICON).toUrl("ChatDemo"))
        Image(
            painter = painterResource(drawable),
            contentDescription = "Back",
            modifier = Modifier
                .size(16.dp)
                .clickable { onBack() }
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // 标题
        Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        // 右侧占位
        Box(modifier = Modifier.width(20.dp))
    }
}

// ==================== 常量定义 ====================

private const val BACK_ICON = "ic_back.png"
