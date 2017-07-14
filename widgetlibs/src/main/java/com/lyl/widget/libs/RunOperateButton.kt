package com.lyl.widget.libs

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.text.TextUtils
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import org.jetbrains.anko.dip

/**
 * create lyl on 2017/7/7
 * </p>
 */
class RunOperateButton(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : Button(context, attrs, defStyleAttr) {

    val STYLE_NONE: Int = 1
    val STYLE_PROGRESS: Int = 0
    val STYLE_PROGRESS_STROKE: Int = 2

    // 中间文字
    private var text: String = ""
    // 最大进度
    private var maxCount: Float = 100.toFloat()
    // 半径变化最大值
    private var offsetMax: Float = 10.toFloat()
    // 分割圈外围宽度
    private var strokeWidth: Float = 0.toFloat()
    // 文字大小
    private var operateTextSize: Float = 0.toFloat()
    // 文字颜色
    private var operateTextColor: Int = 0
    // 背景圈颜色
    private var operateBackgroundColor: Int = 0
    // 分割线圈颜色
    private var operateStrokeColor: Int = 0
    // 进度条空状态颜色
    private var operateEmptyColor: Int = 0
    // 样式 STYLE_NONE 正常类型 STYLE_PROGRESS 进度条类型（正常状态隐藏进度圈）STYLE_PROGRESS_STROKE 进度条类型
    private var operateStyle = STYLE_NONE
    // 圆心坐标
    private var centerX: Float = 0.toFloat()
    private var centerY: Float = 0.toFloat()
    // 背景圆半径
    private var radiusBackground: Float = 0.toFloat()
    // 分割圆半径
    private var radiusStroke: Float = 0.toFloat()
    // 进度圆半径
    private var radiusProgress: Float = 0.toFloat()
    // 当前进度
    private var currentCount: Float = 0.toFloat()
    // 控件宽度
    private var viewWidth: Int = 0
    // 控件高度
    private var viewHeight: Int = 0
    // 背景矩形
    private var rectBackground: RectF? = null
    // 当前动画状态
    private var status = RunStatus.Stop
    // 各种画笔
    private var paintBackground: Paint? = null
    private var paintStroke: Paint? = null
    private var paintEmpty: Paint? = null
    private var paintText: Paint? = null
    // 点击事件监听
    private var onClickListener: OnClickListener? = null
    // 进度变化监听
    private var onProgressListener: OnProgressListener? = null

    private var animHandler: Handler? = null
    private var runnable: Runnable? = null
    private var runnable2: Runnable? = null
    private var runnable3: Runnable? = null
    private var runnable4: Runnable? = null
    private var runnableProgress: Runnable? = null
    private var runnableResetProgress: Runnable? = null

    /***
     * 动画状态
     *
     * Running 正在进行动画中
     * Stop 已停止所有动画
     */
    private enum class RunStatus {
        Running, Stop
    }


    interface OnClickListener {
        fun onOperateClick(view: View)
    }

    interface OnProgressListener {
        fun onProgress(view: RunOperateButton, progress: Float)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        viewWidth = measureDimension(dip(80), widthMeasureSpec)
        viewHeight = measureDimension(dip(80), heightMeasureSpec)
        setMeasuredDimension(viewWidth, viewHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        viewWidth = width
        viewHeight = height
        reset()
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        if (operateStyle == STYLE_PROGRESS || operateStyle == STYLE_PROGRESS_STROKE) {
            val section = currentCount / maxCount
            // 绘制空进度色圈
            canvas.drawCircle(centerX, centerY, radiusProgress, paintEmpty)
            // 绘制当前进度圈
            canvas.drawArc(rectBackground, -90.toFloat(), section * 360, true, paintBackground)
        }
        // 绘制背景色圈
        canvas.drawCircle(centerX, centerY, radiusBackground, paintBackground)
        // 绘制分割线圈
        canvas.drawCircle(centerX, centerY, radiusStroke, paintStroke)

        if (TextUtils.isEmpty(text)) return
        if (paintText != null) {
            val fontMetrics = (paintText as Paint).fontMetrics
            if (fontMetrics != null) {
                // 计算文字高度
                val fontHeight = fontMetrics.bottom - fontMetrics.top
                // 计算文字高度baseline
                val textBaseY = viewHeight.toFloat() - (viewHeight - fontHeight) / 2 - fontMetrics.bottom
                val textBaseX = centerX - (paintText as Paint).measureText(text) / 2
                canvas.drawText(text, textBaseX, textBaseY, paintText)
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (status == RunStatus.Running) return false
                animHandler?.post(if (operateStyle == STYLE_NONE) runnable else runnableProgress)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                when (operateStyle) {
                    STYLE_PROGRESS -> {
                        animHandler?.removeCallbacks(runnableProgress)
                        animHandler?.post(runnableResetProgress)
                    }
                    STYLE_PROGRESS_STROKE -> {
                        reset()
                    }
                    else -> {
                        animHandler?.removeCallbacks(runnable)
                        val diff = radiusBackground - radiusStroke
                        if (isClickSelf(event)) {
                            reset()
                            animHandler?.post(runnable2)
                        } else {
                            if (diff == 25f) {
                                animHandler?.post(runnable3)
                            } else {
                                val waitTime = (15 * (25 - diff) / 2).toLong()
                                animHandler?.postDelayed(runnable3, waitTime)
                            }
                        }
                    }
                }
            }
            else -> {
            }
        }
        return true
    }

    fun setOperateTextSize(textSize: Float) {
        this.operateTextSize = textSize
        this.paintText?.textSize = operateTextSize
    }

    fun setOperateText(text: String) {
        this.text = text
    }

    fun setOperateTextColor(color: Int) {
        this.operateTextColor = color
        this.paintText?.color = operateTextColor
    }

    fun setOperateBackgroundColor(color: Int) {
        this.operateBackgroundColor = color
        this.paintBackground?.color = operateBackgroundColor
    }

    fun setOperateStrokeColor(color: Int) {
        this.operateStrokeColor = color
        this.paintStroke?.color = operateStrokeColor
    }

    fun setOperateEmptyColor(color: Int) {
        this.operateEmptyColor = color
        this.paintEmpty?.color = operateEmptyColor
    }

    fun setOperateStyle(style: Int) {
        this.operateStyle = style
    }

    fun setMaxCount(count: Float) {
        this.maxCount = count
    }

    fun getMaxCount(): Float {
        return this.maxCount
    }

    fun setOffsetMax(offset: Float) {
        this.offsetMax = offset
    }

    fun setStrokeWidth(strokeWidth: Float) {
        this.strokeWidth = strokeWidth
    }

    fun setOperateClickListener(onClickListener: OnClickListener) {
        this.onClickListener = onClickListener
    }

    fun setOperateProgressListener(onProgressListener: OnProgressListener) {
        this.onProgressListener = onProgressListener
    }

    private fun init(attrs: AttributeSet?) {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RunOperateButton)
            offsetMax = typedArray.getDimension(R.styleable.RunOperateButton_offset, dip(5).toFloat())
            strokeWidth = typedArray.getDimension(R.styleable.RunOperateButton_strokeCircleWidth, dip(5).toFloat())
            text = typedArray.getString(R.styleable.RunOperateButton_operateText)
            operateTextSize = typedArray.getDimension(R.styleable.RunOperateButton_operateTextSize, dip(16).toFloat())
            operateTextColor = typedArray.getColor(R.styleable.RunOperateButton_operateTextColor, Color.WHITE)
            operateBackgroundColor = typedArray.getColor(R.styleable.RunOperateButton_operateBackgroundColor, Color.BLUE)
            operateStrokeColor = typedArray.getColor(R.styleable.RunOperateButton_operateStrokeColor, Color.BLACK)
            operateEmptyColor = typedArray.getColor(R.styleable.RunOperateButton_operateEmptyColor, Color.WHITE)
            operateStyle = typedArray.getInt(R.styleable.RunOperateButton_operateStyle, STYLE_NONE)
            maxCount = typedArray.getInt(R.styleable.RunOperateButton_operateMaxProgress, 60).toFloat()
            typedArray.recycle()
        } else {
            offsetMax = dip(5f).toFloat()
            strokeWidth = dip(5f).toFloat()
            operateTextSize = dip(16f).toFloat()
            operateTextColor = Color.WHITE
            operateBackgroundColor = Color.BLUE
            operateStyle = Color.BLACK
            maxCount = 60f
        }
        animHandler = Handler()
        rectBackground = RectF()

        initPaint()
        initHandler()
    }

    private fun initHandler() {
        // region 点击扩大操作
        runnable = Runnable {
            status = RunStatus.Running
            radiusBackground++
            radiusStroke--
            if (radiusBackground - radiusStroke < strokeWidth * 2) {
                animHandler?.postDelayed(runnable, 15)
            } else {
                status = RunStatus.Stop
                animHandler?.removeCallbacks(runnable)
            }
            postInvalidate()
        }
        // endregion
        // region click扩大操作
        runnable2 = Runnable {
            status = RunStatus.Running
            radiusBackground++
            radiusStroke--
            if (radiusBackground - radiusStroke < strokeWidth * 2 + offsetMax) {
                animHandler?.postDelayed(runnable2, 8)
            } else {
                animHandler?.removeCallbacks(runnable2)
                animHandler?.postDelayed(runnable4, 8)
            }
            postInvalidate()
        }
        // endregion
        // region 点击取消操作
        runnable3 = Runnable {
            status = RunStatus.Running
            radiusBackground--
            radiusStroke++
            if (radiusBackground - radiusStroke > strokeWidth) {
                animHandler?.postDelayed(runnable3, 15)
            } else {
                status = RunStatus.Stop
                animHandler?.removeCallbacks(runnable3)
            }
            postInvalidate()
        }
        // endregion
        // region click回缩操作
        runnable4 = Runnable {
            status = RunStatus.Running
            radiusBackground--
            radiusStroke++
            if (radiusBackground - radiusStroke > strokeWidth) {
                animHandler?.postDelayed(runnable4, 8)
            } else {
                status = RunStatus.Stop
                animHandler?.removeCallbacks(runnable4)
                onClickListener?.onOperateClick(this)
            }
            postInvalidate()
        }
        // endregion
        // region 进度条增加
        runnableProgress = Runnable {
            status = RunStatus.Running
            if (operateStyle == STYLE_PROGRESS) { // （正常状态隐藏进度圈）
                if (radiusProgress - radiusBackground < strokeWidth) {
                    radiusProgress++
                    radiusBackground--
                    radiusStroke--
                    val left = centerX - radiusProgress
                    val top = centerY - radiusProgress
                    rectBackground?.set(left, top, viewWidth - left, viewHeight - top)
                }
            }
            onProgressListener?.onProgress(this, currentCount)
            if (currentCount < maxCount) {
                currentCount++
                animHandler?.postDelayed(runnableProgress, 15)
            } else {
                status = RunStatus.Stop
            }
            postInvalidate()
        }
        // endregion
        // region 进度条减少
        runnableResetProgress = Runnable {
            status = RunStatus.Running
            onProgressListener?.onProgress(this, currentCount)
            if (currentCount < maxCount)
                currentCount--
            if (radiusProgress - radiusBackground > 0) {
                radiusProgress--
                radiusBackground++
                radiusStroke++
                val left = centerX - radiusProgress
                val top = centerY - radiusProgress
                rectBackground?.set(left, top, viewWidth - left, viewHeight - top)
                animHandler?.postDelayed(runnableResetProgress, 15)
                postInvalidate()
            } else {
                reset()
            }
        }
        // endregion
    }

    /***
     * 初始化画笔
     */
    private fun initPaint() {
        paintBackground = Paint()
        paintBackground?.isAntiAlias = true
        paintBackground?.style = Paint.Style.FILL
        paintBackground?.color = operateBackgroundColor

        paintStroke = Paint()
        paintStroke?.isAntiAlias = true
        paintStroke?.strokeWidth = dip(2).toFloat()
        paintStroke?.style = Paint.Style.STROKE
        paintStroke?.color = operateStrokeColor

        paintEmpty = Paint()
        paintEmpty?.isAntiAlias = true
        paintEmpty?.style = Paint.Style.FILL
        paintEmpty?.color = operateEmptyColor

        paintText = Paint()
        paintText?.isAntiAlias = true
        paintText?.style = Paint.Style.FILL
        paintText?.color = operateTextColor
        paintText?.textSize = operateTextSize
    }

    private fun measureDimension(defaultSize: Int, measureSpec: Int): Int {
        val result: Int
        val specMode: Int = MeasureSpec.getMode(measureSpec)
        val specSize: Int = MeasureSpec.getSize(measureSpec)
        when (specMode) {
            MeasureSpec.EXACTLY -> result = specSize
            MeasureSpec.AT_MOST -> result = Math.min(defaultSize, specSize)
            else -> result = defaultSize
        }
        return result
    }

    private fun isClickSelf(event: MotionEvent): Boolean = 0 <= event.x && right - left >= event.x && 0 <= event.y && bottom - top >= event.y

    private fun reset() {
        status = RunStatus.Stop
        centerX = (viewWidth / 2).toFloat()
        centerY = (viewHeight / 2).toFloat()

        when (operateStyle) {
            STYLE_PROGRESS -> {
                currentCount = 0.toFloat()
                radiusBackground = centerX - offsetMax
                radiusStroke = radiusBackground
                radiusProgress = radiusStroke// 最外圈 内圈和黑色分割圈 进度半径
                val left = centerX - radiusProgress
                val top = centerY - radiusProgress
                rectBackground?.set(left, top, viewWidth - left, viewHeight - top)
                animHandler?.removeCallbacks(runnableProgress)
            }
            STYLE_PROGRESS_STROKE -> {
                currentCount = 0.toFloat()
                radiusProgress = centerX - offsetMax // 最外圈 进度半径
                radiusStroke = radiusProgress - strokeWidth
                radiusBackground = radiusStroke // 内圈和黑色分割圈半径
                val left = centerX - radiusProgress
                val top = centerY - radiusProgress
                rectBackground?.set(left, top, viewWidth - left, viewHeight - top)
                animHandler?.removeCallbacks(runnableProgress)
            }
            else -> {
                radiusBackground = centerX - offsetMax // 最外圈度半径
                radiusStroke = radiusBackground - strokeWidth // 黑色分割圈半径
                animHandler?.removeCallbacks(runnable)
                animHandler?.removeCallbacks(runnable2)
                animHandler?.removeCallbacks(runnable3)
                animHandler?.removeCallbacks(runnable4)
                animHandler?.removeCallbacksAndMessages(null)
            }

        }

        postInvalidate()
    }

    init {
        init(attrs)
    }
}