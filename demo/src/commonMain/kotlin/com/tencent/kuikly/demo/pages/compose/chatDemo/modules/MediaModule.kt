package com.tencent.kuikly.demo.pages.compose.chatDemo.modules

import com.tencent.kuikly.core.log.KLog
import com.tencent.kuikly.core.module.CallbackFn
import com.tencent.kuikly.core.module.Module
import com.tencent.kuikly.core.nvi.serialization.json.JSONObject

/**
 * 媒体模块 - 用于打开相册选图和拍照
 * 
 * 需要原生侧实现对应的方法：
 * - openImagePicker: 打开相册选图
 * - openCamera: 打开相机拍照
 */
class MediaModule : Module() {
    
    companion object {
        const val MODULE_NAME = "KRMediaModule"
        private const val TAG = "MediaModule"
        
        // 方法名常量
        private const val METHOD_OPEN_IMAGE_PICKER = "openImagePicker"
        private const val METHOD_OPEN_CAMERA = "openCamera"
    }
    
    override fun moduleName(): String = MODULE_NAME
    
    /**
     * 打开相册选图
     * 
     * @param maxSelectCount 最大选择数量，默认为 9
     * @param callback 回调函数，返回选中的图片信息
     *        成功时返回: { "success": true, "data": [{ "path": "...", "width": ..., "height": ... }] }
     *        失败或取消时返回: { "success": false, "message": "..." }
     */
    fun openImagePicker(maxSelectCount: Int = 9, callback: CallbackFn?) {
        KLog.i(TAG, "openImagePicker maxSelectCount=$maxSelectCount")
        val params = JSONObject().apply {
            put("maxSelectCount", maxSelectCount)
        }
    }
    
    /**
     * 打开相机拍照
     * 
     * @param callback 回调函数，返回拍摄的图片信息
     *        成功时返回: { "success": true, "data": { "path": "...", "width": ..., "height": ... } }
     *        失败或取消时返回: { "success": false, "message": "..." }
     */
    fun openCamera(callback: CallbackFn?) {
        KLog.i(TAG, "openCamera")
    }
}

/**
 * 本地媒体信息
 */
data class LocalMediaInfo(
    val path: String,
    var width: Int = 0,
    var height: Int = 0
)
