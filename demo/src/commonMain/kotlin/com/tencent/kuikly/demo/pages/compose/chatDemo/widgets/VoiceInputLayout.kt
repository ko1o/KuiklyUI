package com.tencent.kuikly.demo.pages.compose.chatDemo.widgets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.tencent.kuikly.compose.foundation.background
import com.tencent.kuikly.compose.foundation.clickable
import com.tencent.kuikly.compose.foundation.gestures.awaitEachGesture
import com.tencent.kuikly.compose.foundation.gestures.awaitFirstDown
import com.tencent.kuikly.compose.foundation.layout.Arrangement
import com.tencent.kuikly.compose.foundation.layout.Box
import com.tencent.kuikly.compose.foundation.layout.Column
import com.tencent.kuikly.compose.foundation.layout.Row
import com.tencent.kuikly.compose.foundation.layout.Spacer
import com.tencent.kuikly.compose.foundation.layout.fillMaxHeight
import com.tencent.kuikly.compose.foundation.layout.fillMaxSize
import com.tencent.kuikly.compose.foundation.layout.fillMaxWidth
import com.tencent.kuikly.compose.foundation.layout.height
import com.tencent.kuikly.compose.foundation.layout.padding
import com.tencent.kuikly.compose.foundation.layout.size
import com.tencent.kuikly.compose.foundation.layout.width
import com.tencent.kuikly.compose.foundation.layout.wrapContentHeight
import com.tencent.kuikly.compose.foundation.shape.RoundedCornerShape
import com.tencent.kuikly.compose.material3.Text
import com.tencent.kuikly.compose.ui.Alignment
import com.tencent.kuikly.compose.ui.Modifier
import com.tencent.kuikly.compose.ui.draw.clip
import com.tencent.kuikly.compose.ui.geometry.Size
import com.tencent.kuikly.compose.ui.graphics.Brush
import com.tencent.kuikly.compose.ui.graphics.Color
import com.tencent.kuikly.compose.ui.input.pointer.PointerEventPass
import com.tencent.kuikly.compose.ui.input.pointer.changedToUp
import com.tencent.kuikly.compose.ui.input.pointer.pointerInput
import com.tencent.kuikly.compose.ui.layout.onGloballyPositioned
import com.tencent.kuikly.compose.ui.text.font.FontWeight
import com.tencent.kuikly.compose.ui.text.style.TextAlign
import com.tencent.kuikly.compose.ui.unit.dp
import com.tencent.kuikly.compose.ui.unit.sp
import com.tencent.kuikly.compose.ui.unit.toSize
import com.tencent.kuikly.compose.ui.util.fastAny
import com.tencent.kuikly.compose.ui.window.Dialog
import com.tencent.kuikly.compose.ui.window.DialogProperties
import com.tencent.kuikly.compose.ui.window.KuiklyDialogProperties

// ==================== 常量定义 ====================

/** 语音输入提示文字 */
const val VOICE_INPUT_TEXT = "按住 说话"

/** 松手发送提示 */
const val VOICE_DEFAULT_TIP = "松手发送 上移取消"

/** 松手取消提示 */
const val VOICE_CANCEL_TIP = "松手取消"

/** 录音超时时间（秒） */
const val VOICE_RECORD_TIMEOUT = 60

/** 录音倒计时提示后缀 */
const val VOICE_RECORD_COUNTDOWN_SUFFIX = "后将停止录音"

/** 录音默认提示 */
const val VOICE_DEFAULT_HINT = "您好，请说话"

/** 波形条颜色 - 正常状态 */
val VOICE_WAVE_COLOR_NORMAL = Color(0xFF1A1C1E)

/** 波形条颜色 - 取消状态（红色） */
val VOICE_WAVE_COLOR_CANCEL = Color(0xFFFF4D4D)

/** 波形条背景色 - 正常状态（黑色） */
val VOICE_WAVE_BG_NORMAL = Color(0xFF1A1C1E)

/** 波形条背景色 - 取消状态（红色） */
val VOICE_WAVE_BG_CANCEL = Color(0xFFEF5350)

/** 提示文字颜色 - 正常状态 */
val VOICE_TIP_COLOR_NORMAL = Color(0xFF999999)

/** 提示文字颜色 - 取消状态（红色） */
val VOICE_TIP_COLOR_CANCEL = Color(0xFFEF5350)

// ==================== 状态管理 ====================

/**
 * 语音输入状态
 */
class VoiceInputState {
    /** 是否显示录音视图 */
    val isShowRecordView: MutableState<Boolean> = mutableStateOf(false)
    
    /** 手指是否在录音区域内（决定是发送还是取消） */
    val isGestureTriggeredInside: MutableState<Boolean> = mutableStateOf(true)
    
    /** 当前识别的文字 */
    val currentAudioText: MutableState<String> = mutableStateOf("")
    
    /** 倒计时时间 */
    val countDownTime: MutableState<Int> = mutableStateOf(VOICE_RECORD_TIMEOUT)
    
    /** 是否正在录音 */
    val isRecording: MutableState<Boolean> = mutableStateOf(false)
    
    /** 当前音量振幅（用于波形动画） */
    val currentAmplitude: MutableState<Float> = mutableStateOf(0f)
    
    /** 输入按钮区域大小，用于判断手势位置 */
    var inputStyleSize: Size = Size.Zero
    
    /** 录音视图区域大小，用于判断手势位置 */
    var recordViewSize: Size = Size.Zero
    
    /** 开始录音 */
    fun startRecording() {
        isRecording.value = true
        isShowRecordView.value = true
        isGestureTriggeredInside.value = true
        countDownTime.value = VOICE_RECORD_TIMEOUT
        currentAudioText.value = ""
    }
    
    /** 停止录音 */
    fun stopRecording() {
        isRecording.value = false
        isShowRecordView.value = false
    }
    
    /** 重置状态 */
    fun reset() {
        isShowRecordView.value = false
        isGestureTriggeredInside.value = true
        isRecording.value = false
        currentAmplitude.value = 0f
        countDownTime.value = VOICE_RECORD_TIMEOUT
    }
    
    /**
     * 处理手势移动事件
     * 参考 QQAIBiz 的逻辑：根据 Y 轴位移判断是否在取消区域
     * 当手指上移超过一定距离时，进入取消状态
     */
    fun handleMoveEvent(x: Float, y: Float) {
        // 如果上移超出录音区域，则认为是取消操作
        // 参考 QQAIBiz：通过判断手指是否在录音按钮区域内来决定是否取消
        val isInside = y >= 0 // 简化判断：手指没有上移出初始触摸区域
        isGestureTriggeredInside.value = isInside
    }
    
    /** 更新手势位置（判断是否在录音区域内） */
    fun updateGesturePosition(isInside: Boolean) {
        isGestureTriggeredInside.value = isInside
    }
    
    /** 更新音量振幅 */
    fun updateAmplitude(amplitude: Float) {
        currentAmplitude.value = amplitude
    }
}

/**
 * 创建并记住语音输入状态
 */
@Composable
fun rememberVoiceInputState(): VoiceInputState {
    return remember { VoiceInputState() }
}

// ==================== 组件实现 ====================

/**
 * 语音输入按住说话按钮
 * 覆盖在输入框上方，按住后进入语音输入模式
 * 
 * 参考 QQAIBiz 的 VoiceInputStyleLayout 实现：
 * - 使用 pointerInteropFilter 处理 ACTION_DOWN/UP/CANCEL 事件
 * - 使用 pan 手势处理移动事件
 * - 按下时开始录音，抬起时结束录音
 * - 移动时判断是否在取消区域
 * 
 * @param state 语音输入状态
 * @param onPressDown 按下回调（开始录音）
 * @param onPressUp 抬起回调（结束录音，shouldSend: 是否发送）
 * @param onMove 移动回调（更新手势位置）
 */
@Composable
fun VoiceInputStyleLayout(
    state: VoiceInputState,
    onPressDown: () -> Unit = {},
    onPressUp: (shouldSend: Boolean) -> Unit = {},
    onMove: (x: Float, y: Float) -> Unit = { _, _ -> }
) {
    // 记录按下时的初始 Y 坐标，用于判断上移取消
    val initialY = remember { mutableStateOf(0f) }
    // 取消阈值：上移超过此距离则进入取消状态
    val cancelThreshold = 100f
    
    // 语音输入按钮的高度 - 与文本输入框高度一致
    val voiceButtonHeight = 32.dp
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(voiceButtonHeight)  // 使用固定高度而不是 fillMaxHeight
            .onGloballyPositioned { coordinates ->
                state.inputStyleSize = coordinates.size.toSize()
            }
            .pointerInput(Unit) {
                awaitEachGesture {
                    // 1. 等待按下事件
                    val down = awaitFirstDown(requireUnconsumed = false)
                    initialY.value = down.position.y
                    
                    // 按下时开始录音
                    state.startRecording()
                    onPressDown()
                    
                    // 2. 持续监听后续事件（移动、抬起、取消）
                    var shouldSend = true
                    while (true) {
                        val event = awaitPointerEvent(PointerEventPass.Main)
                        val anyUp = event.changes.fastAny { it.changedToUp() }
                        
                        if (anyUp) {
                            // 手指抬起，结束录音
                            shouldSend = state.isGestureTriggeredInside.value
                            onPressUp(shouldSend)
                            state.stopRecording()
                            break
                        }
                        
                        // 处理移动事件
                        event.changes.forEach { change ->
                            if (change.pressed) {
                                val currentY = change.position.y
                                val deltaY = initialY.value - currentY // 上移为正
                                
                                // 判断是否上移超过取消阈值
                                val isInside = deltaY < cancelThreshold
                                state.updateGesturePosition(isInside)
                                onMove(change.position.x, change.position.y)
                            }
                        }
                        
                        // 检查是否所有手指都抬起
                        if (event.changes.all { !it.pressed }) {
                            shouldSend = state.isGestureTriggeredInside.value
                            onPressUp(shouldSend)
                            state.stopRecording()
                            break
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = VOICE_INPUT_TEXT,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1A1C1E),
            textAlign = TextAlign.Center
        )
    }
}

/**
 * 语音录音视图 - 显示在录音时的UI
 * 使用 Dialog 实现全屏覆盖效果
 * 包含：倒计时、提示文字、波形动画条
 * 
 * @param state 语音输入状态
 */
@Composable
fun VoiceRecordView(
    state: VoiceInputState
) {
    if (!state.isShowRecordView.value) {
        return
    }
    
    // 使用 Dialog 实现全屏覆盖
    Dialog(
        onDismissRequest = { /* 不允许关闭，必须通过手势操作 */ },
        properties = KuiklyDialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
            inWindow = false,
            scrimColor = Color.Transparent,
            contentAlignment = Alignment.BottomCenter
        )
    ) {
        // 全屏容器，底部渐变背景
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { /* 拦截点击 */ },
            contentAlignment = Alignment.BottomCenter
        ) {
            // 底部渐变背景 + 内容
            // 参考 QQAIBiz：0% 透明 → 13% 白色 → 100% 白色
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .background(
                        brush = Brush.verticalGradient(
                            colorStops = arrayOf(
                                0.0f to Color.White.copy(alpha = 0f),
                                0.13f to Color.White,
                                1.0f to Color.White
                            )
                        )
                    )
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 倒计时提示（只在最后10秒显示）
                CountdownTimerView(state)
                
                // 操作提示文字
                RecordTipView(state)
                
                Spacer(modifier = Modifier.height(9.dp))
                
                // 波形条
                VoiceWaveView(state)
            }
        }
    }
}

/**
 * 倒计时提示
 */
@Composable
private fun CountdownTimerView(state: VoiceInputState) {
    val countDown = state.countDownTime.value
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(top = 40.dp),
        contentAlignment = Alignment.Center
    ) {
        if (countDown <= 10) {
            Text(
                text = "${countDown}''$VOICE_RECORD_COUNTDOWN_SUFFIX",
                fontSize = 16.sp,
                color = Color(0xFF1A1C1E),
                fontWeight = FontWeight.Normal,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * 录音操作提示
 */
@Composable
private fun RecordTipView(state: VoiceInputState) {
    val isInside = state.isGestureTriggeredInside.value
    val tipText = if (isInside) VOICE_DEFAULT_TIP else VOICE_CANCEL_TIP
    val color = if (isInside) VOICE_TIP_COLOR_NORMAL else VOICE_TIP_COLOR_CANCEL
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = tipText,
            fontSize = 14.sp,
            color = color,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * 波形动画视图
 */
@Composable
private fun VoiceWaveView(state: VoiceInputState) {
    val isInside = state.isGestureTriggeredInside.value
    val bgColor = if (isInside) VOICE_WAVE_BG_NORMAL else VOICE_WAVE_BG_CANCEL
    val waveColor = Color.White
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        // 简化的波形动画 - 使用多个竖条模拟
        VoiceWaveBars(
            amplitude = state.currentAmplitude.value,
            color = waveColor
        )
    }
}

/**
 * 波形竖条动画
 */
@Composable
private fun VoiceWaveBars(
    amplitude: Float,
    color: Color
) {
    val barCount = 30
    val maxHeight = 24.dp
    val minHeight = 4.dp
    val barWidth = 3.dp
    val barSpacing = 4.dp
    
    Row(
        modifier = Modifier.wrapContentHeight(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(barCount) { index ->
            // 根据位置和振幅计算高度
            val distanceFromCenter = kotlin.math.abs(index - barCount / 2)
            val normalizedDistance = distanceFromCenter.toFloat() / (barCount / 2)
            
            // 基础高度 + 振幅影响
            val baseHeight = minHeight + (maxHeight - minHeight) * (1 - normalizedDistance * 0.5f)
            val currentHeight = baseHeight * (0.3f + 0.7f * amplitude.coerceIn(0f, 1f))
            
            Box(
                modifier = Modifier
                    .width(barWidth)
                    .height(currentHeight)
                    .clip(RoundedCornerShape(barWidth / 2))
                    .background(color)
            )
            
            if (index < barCount - 1) {
                Spacer(modifier = Modifier.width(barSpacing))
            }
        }
    }
}
