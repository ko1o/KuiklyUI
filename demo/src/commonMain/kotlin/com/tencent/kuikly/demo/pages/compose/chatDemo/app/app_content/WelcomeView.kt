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
import com.tencent.kuikly.demo.pages.compose.chatDemo.configs.WelcomePromptBox
import com.tencent.kuikly.demo.pages.compose.chatDemo.configs.WelcomeViewConfig

/**
 * 欢迎页组件
 */
@OptIn(InternalResourceApi::class)
@Composable
fun WelcomeView(
    onInputTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    listState: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    config: WelcomeViewConfig = WelcomeViewConfig()
) {
    // 如果有整体 builder，直接调用
    if (config.builder != null) {
        config.builder.invoke(onInputTextChange)
        return
    }

    LazyColumn(
        modifier = modifier,
        state = listState,
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = contentPadding
    ) {
        item { Spacer(modifier = Modifier.height(config.logoTopSpacing)) }
        
        item {
            val logoDrawable = DrawableResource(ImageUri.pageAssets(config.logoIcon).toUrl(config.pageName))
            Image(
                painter = painterResource(logoDrawable),
                contentDescription = "Logo",
                modifier = Modifier.width(config.logoWidth).height(config.logoHeight)
            )
        }
        
        item { Spacer(modifier = Modifier.height(config.logoBottomSpacing)) }

        items(config.promptBoxes) { box ->
            if (config.cardBuilder != null) {
                config.cardBuilder.invoke(box) { onInputTextChange(box.prompt) }
            } else {
                PromptCard(box = box, config = config, onClick = { onInputTextChange(box.prompt) })
            }
            Spacer(modifier = Modifier.height(config.cardSpacing))
        }
        
        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

@Composable
private fun PromptCard(box: WelcomePromptBox, config: WelcomeViewConfig, onClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().padding(horizontal = config.cardHorizontalPadding),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(config.cardWidthFraction)
                .clip(RoundedCornerShape(config.cardCornerRadius))
                .background(
                    com.tencent.kuikly.compose.ui.graphics.Brush.Companion.horizontalGradient(
                        colors = listOf(box.startColor, box.endColor)
                    )
                )
                .clickable { onClick() }
                .padding(vertical = config.cardVerticalPadding, horizontal = config.cardInnerHorizontalPadding)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text(text = box.title, fontSize = config.cardTitleFontSize, color = config.cardTitleColor, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(config.cardTitleSubtitleSpacing))
                Text(text = box.subtitle, fontSize = config.cardSubtitleFontSize, color = Color.Black.copy(alpha = config.cardSubtitleAlpha))
            }
        }
    }
}
