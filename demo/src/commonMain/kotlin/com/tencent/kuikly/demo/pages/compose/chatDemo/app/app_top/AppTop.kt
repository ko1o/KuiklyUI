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
import com.tencent.kuikly.demo.pages.compose.chatDemo.configs.AppTopConfig

/**
 * 顶部区域组件
 */
@Composable
fun AppTop(
    statusBarHeight: Float,
    onBack: () -> Unit,
    config: AppTopConfig = AppTopConfig(),
    modifier: Modifier = Modifier
) {
    // 如果有整体 builder，直接调用
    if (config.builder != null) {
        config.builder.invoke(statusBarHeight, onBack)
        return
    }
    
    // 状态栏占位
    Spacer(modifier = Modifier.height(statusBarHeight.dp))

    // 导航栏
    if (config.navBarBuilder != null) {
        config.navBarBuilder.invoke(config.title, onBack)
    } else {
        NavBar(config = config, onBack = onBack, modifier = modifier)
    }

    // 分割线
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(config.dividerHeight)
            .background(config.dividerColor)
    )
}

@OptIn(InternalResourceApi::class)
@Composable
private fun NavBar(
    config: AppTopConfig,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(config.navBarHeight)
            .padding(horizontal = config.navBarHorizontalPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val drawable = DrawableResource(ImageUri.pageAssets(config.backIcon).toUrl(config.pageName))
        Image(
            painter = painterResource(drawable),
            contentDescription = "Back",
            modifier = Modifier
                .size(config.backIconSize)
                .clickable { onBack() }
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        Text(
            text = config.title,
            fontSize = config.titleFontSize,
            fontWeight = FontWeight.Bold,
            color = config.titleColor,
            modifier = Modifier.align(Alignment.CenterVertically)
        )
        
        Spacer(modifier = Modifier.weight(1f))
        
        Box(modifier = Modifier.width(config.rightPlaceholderWidth))
    }
}
