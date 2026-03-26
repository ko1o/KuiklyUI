package com.tencent.kuikly.demo.pages.compose.chatDemo.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.tencent.kuikly.compose.coil3.rememberAsyncImagePainter
import com.tencent.kuikly.compose.foundation.Image
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.border
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Spacer
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.offset
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.layout.size
import com.tencent.kuikly.compose.foundation.layout.width
import com.tencent.kuikly.compose.foundation.lazy.LazyRow
import com.tencent.kuikly.compose.foundation.lazy.itemsIndexed
import com.tencent.kuikly.compose.foundation.shape.CircleShape
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.material3.Text
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.layout.ContentScale
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.tencent.kuikly.core.base.attr.ImageUri

// ==================== 数据模型 ====================

/**
 * 本地媒体信息
 */
data class LocalMediaInfo(
    val path: String,           // 文件路径
    val width: Int = 0,         // 图片宽度
    val height: Int = 0,        // 图片高度
    val fileSize: Long = 0,     // 文件大小
    val mimeType: String = ""   // MIME类型
)

// ==================== 常量 ====================

/** 图片选择区域高度 */
val IMAGE_PICKER_HEIGHT = 120f.dp

/** 最大图片选择数量 */
const val MAX_IMAGE_PICK_COUNT = 9

// ==================== 测试数据 ====================

/**
 * 模拟测试图片资源（使用本地 assets 图片）
 */
object TestImageResources {
    // 使用本地 assets 测试图片
    private val testAssetImages = listOf(
        "cat1.png",
        "cat2.png",
        "panda.png",
        "panda2.png",
        "penguin2.png"
    )
    
    /**
     * 获取模拟的图片选择结果（使用本地 assets 图片）
     * @param count 选择数量
     */
    fun getMockSelectedImages(count: Int): List<LocalMediaInfo> {
        return testAssetImages.take(count.coerceIn(1, testAssetImages.size)).map { assetName ->
            LocalMediaInfo(
                path = ImageUri.commonAssets(assetName).toUrl(""),
                width = 200,
                height = 200,
                fileSize = 1024 * 100,
                mimeType = "image/png"
            )
        }
    }
}

// ==================== 状态管理 ====================

/**
 * 图片选择状态
 */
class ImagePickerState {
    // 已选择的图片列表
    val pickedImages: SnapshotStateList<LocalMediaInfo> = mutableStateListOf()
    
    // 是否有已选图片
    val hasImages: Boolean
        get() = pickedImages.isNotEmpty()
    
    // 已选图片数量
    val imageCount: Int
        get() = pickedImages.size
    
    // 是否可以继续添加图片
    val canAddMore: Boolean
        get() = pickedImages.size < MAX_IMAGE_PICK_COUNT
    
    /**
     * 添加图片
     */
    fun addImage(image: LocalMediaInfo) {
        if (canAddMore) {
            pickedImages.add(image)
        }
    }
    
    /**
     * 添加多张图片
     */
    fun addImages(images: List<LocalMediaInfo>) {
        val remaining = MAX_IMAGE_PICK_COUNT - pickedImages.size
        pickedImages.addAll(images.take(remaining))
    }
    
    /**
     * 删除指定索引的图片
     */
    fun removeImage(index: Int) {
        if (index in 0 until pickedImages.size) {
            pickedImages.removeAt(index)
        }
    }
    
    /**
     * 清空所有图片
     */
    fun clearImages() {
        pickedImages.clear()
    }
}

// ==================== UI 组件 ====================

/**
 * 图片选择预览视图 - 显示在输入框上方
 */
@Composable
fun ImagePickerView(
    state: ImagePickerState,
    onAddClick: () -> Unit = {},
    isDarkMode: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(IMAGE_PICKER_HEIGHT)
    ) {
        LazyRow(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, top = 8.dp, end = 16.dp)
        ) {
            // 已选图片列表
            itemsIndexed(state.pickedImages) { index, imageInfo ->
                ImagePickerItem(
                    imageInfo = imageInfo,
                    onDeleteClick = { state.removeImage(index) }
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            
            // 添加按钮（如果还可以添加更多图片）
            if (state.canAddMore) {
                item {
                    AddImageButton(
                        onClick = onAddClick,
                        isDarkMode = isDarkMode
                    )
                }
            }
        }
    }
}

/**
 * 单个图片项
 */
@Composable
private fun ImagePickerItem(
    imageInfo: LocalMediaInfo,
    onDeleteClick: () -> Unit
) {
    // 容器尺寸：图片88dp + 删除按钮偏移
    Box(
        modifier = Modifier.size(100.dp, 100.dp)
    ) {
        // 图片 - 88x88，圆角12dp
        Image(
            painter = rememberAsyncImagePainter(imageInfo.path),
            contentDescription = "Selected image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(88.dp)
                .align(Alignment.BottomStart)
                .clip(RoundedCornerShape(12.dp))
        )
        
        // 删除按钮 - 24dp圆形，灰色背景，位于右上角
        Box(
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.TopEnd)
                .offset(x = (-2).dp, y = 2.dp)
                .background(Color(0xFF666666), CircleShape)
                .clickable { onDeleteClick() },
            contentAlignment = Alignment.Center
        ) {
            // × 图标
            Text(
                text = "×",
                color = Color.White,
                fontSize = 16.sp
            )
        }
    }
}

/**
 * 添加图片按钮
 */
@Composable
private fun AddImageButton(
    onClick: () -> Unit,
    isDarkMode: Boolean = false
) {
    val borderColor = if (isDarkMode) Color(0xFF444444) else Color(0xFFD0D0D0)
    val iconColor = if (isDarkMode) Color(0xFFAAAAAA) else Color(0xFF999999)
    
    Box(
        modifier = Modifier
            .padding(top = 12.dp)
            .size(88.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // + 文字图标
        Text(
            text = "+",
            color = iconColor,
            fontSize = 36.sp
        )
    }
}
