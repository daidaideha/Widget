package com.lyl.widget.libs

import android.content.Context
import android.graphics.Color
import android.support.v4.view.MotionEventCompat
import android.support.v4.view.ViewCompat
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import org.jetbrains.anko.backgroundColor

/**
 * create lyl on 2017/7/12
 * </p>
 */
class SWSwipeRefreshLayout : ViewGroup {

    private val INVALID_POINTER = -1
    private val DRAG_RATE = .5f
    private val LAYOUT_ATTRS = intArrayOf(android.R.attr.enabled)
    private val HEADER_DIAMETER: Int = 50
    private val HEADER_PADDING_BOTTOM: Int = 10

    private var target: View? = null

    private var headerView: RunOperateButton? = null

    private var headerWidth: Int = 0
    private var headerHeight: Int = 0
    private var headerPaddingTopBottom: Int = 0
    private var headerIndex: Int
    private var currentTargetOffsetTop: Int
    private var mOriginalOffsetTop: Int
    private var touchSlop: Int

    constructor(context: Context?, attrs: AttributeSet) : super(context, attrs) {
        this.touchSlop = ViewConfiguration.get(context).scaledPagingTouchSlop
        setWillNotDraw(false)
        val metrics = resources.displayMetrics
        this.headerWidth = (HEADER_DIAMETER * metrics.density).toInt()
        this.headerHeight = (HEADER_DIAMETER * metrics.density).toInt()
        this.headerPaddingTopBottom = (HEADER_PADDING_BOTTOM * metrics.density).toInt()

        createProgressView()

        this.headerIndex = -1
        this.currentTargetOffsetTop = -headerHeight
        this.mOriginalOffsetTop = currentTargetOffsetTop

        ViewCompat.setChildrenDrawingOrderEnabled(this, true)

        val a = context?.obtainStyledAttributes(attrs, LAYOUT_ATTRS)
        isEnabled = a!!.getBoolean(0, true)
        a.recycle()
    }

    constructor(context: Context?, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.touchSlop = ViewConfiguration.get(context).scaledPagingTouchSlop
        setWillNotDraw(false)
        val metrics = resources.displayMetrics
        this.headerWidth = (HEADER_DIAMETER * metrics.density).toInt()
        this.headerHeight = (HEADER_DIAMETER * metrics.density).toInt()
        this.headerPaddingTopBottom = (HEADER_PADDING_BOTTOM * metrics.density).toInt()

        createProgressView()

        this.headerIndex = -1
        this.currentTargetOffsetTop = -headerHeight
        this.mOriginalOffsetTop = currentTargetOffsetTop

        ViewCompat.setChildrenDrawingOrderEnabled(this, true)

        val a = context?.obtainStyledAttributes(attrs, LAYOUT_ATTRS)
        isEnabled = a!!.getBoolean(0, true)
        a.recycle()
    }

    private fun createProgressView() {
        headerView = RunOperateButton(context)
        headerView!!.backgroundColor = Color.BLUE
        headerView!!.visibility = View.GONE
        addView(headerView)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (target == null) {
            ensureTarget()
        }
        if (target == null) return
        target?.measure(MeasureSpec.makeMeasureSpec(measuredWidth - paddingLeft - paddingRight, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(measuredHeight - paddingTop - paddingBottom, MeasureSpec.EXACTLY))
        headerView?.measure(MeasureSpec.makeMeasureSpec(headerWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(headerHeight, MeasureSpec.EXACTLY))
        headerIndex = (0..childCount - 1).firstOrNull { getChildAt(it) == headerView } ?: -1
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val width: Int = measuredWidth
        val height: Int = measuredHeight
        if (childCount == 0) return
        if (target == null) {
            ensureTarget()
        }
        if (target == null) return
        val child: View = target as View
        val childLeft: Int = paddingLeft
        val childTop: Int = paddingTop
        val childWidth: Int = width - paddingLeft - paddingRight
        val childHeight: Int = height - paddingTop - paddingBottom
        child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight)
        val headerW: Int = (headerView as View).measuredWidth
        val headerH: Int = (headerView as View).measuredHeight
        (headerView as View).layout((width - headerW) / 2, currentTargetOffsetTop, (width + headerW) / 2, currentTargetOffsetTop + headerH)
    }

    private var mReturningToStart: Boolean = false
    private var mRefreshing: Boolean = false
    private var mActivePointerId: Int = -1
    private var mIsBeingDragged: Boolean = false
    private var mInitialDownY: Float = 0.toFloat()
    private var mInitialMotionY: Float = 0.toFloat()

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        ensureTarget()

        val action = MotionEventCompat.getActionMasked(ev)

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false
        }

        // 刷新时 阻止滚动
        if (mRefreshing) {
            return true
        }

        if (!isEnabled || mReturningToStart || canChildScrollUp()) {//|| mRefreshing
            // Fail fast if we're not in a state where a swipe is possible
            return false
        }

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                setTargetOffsetTopAndBottom(mOriginalOffsetTop - (headerView?.top ?: 0), true)
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0)
                mIsBeingDragged = false
                val initialDownY = getMotionEventY(ev, mActivePointerId)
                if (initialDownY == -1f) {
                    return false
                }
                mInitialDownY = initialDownY
            }

            MotionEvent.ACTION_MOVE -> {
                if (mActivePointerId == INVALID_POINTER) {
                    return false
                }

                val y = getMotionEventY(ev, mActivePointerId)
                if (y == -1f) {
                    return false
                }
                val yDiff = y - mInitialDownY
                if (yDiff > touchSlop && !mIsBeingDragged) {
                    mInitialMotionY = mInitialDownY + touchSlop
                    mIsBeingDragged = true
                }
            }

            MotionEventCompat.ACTION_POINTER_UP -> onSecondaryPointerUp(ev)

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mIsBeingDragged = false
                mActivePointerId = INVALID_POINTER
            }
        }

        return mIsBeingDragged
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        val action = MotionEventCompat.getActionMasked(ev)
        var pointerIndex = -1

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false
        }

        if (mRefreshing) {
            return true
        }

        if (!isEnabled || mReturningToStart || canChildScrollUp()) {
            // Fail fast if we're not in a state where a swipe is possible
            return false
        }

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0)
                mIsBeingDragged = false
            }

            MotionEvent.ACTION_MOVE -> {
                pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId)
                if (pointerIndex < 0) {
                    return false
                }

                val y = MotionEventCompat.getY(ev, pointerIndex)
                val overscrollTop = (y - mInitialMotionY) * DRAG_RATE
                if (mIsBeingDragged) {
                    if (overscrollTop > 0) {
                        moveSpinner(overscrollTop)
                    } else {
                        return false
                    }
                }
            }
            MotionEventCompat.ACTION_POINTER_DOWN -> {
                pointerIndex = MotionEventCompat.getActionIndex(ev)
                if (pointerIndex < 0) {
                    return false
                }
                mActivePointerId = MotionEventCompat.getPointerId(ev, pointerIndex)
            }

            MotionEventCompat.ACTION_POINTER_UP -> onSecondaryPointerUp(ev)

            MotionEvent.ACTION_UP -> {
                pointerIndex = MotionEventCompat.findPointerIndex(ev, mActivePointerId)
                if (pointerIndex < 0) {
                    return false
                }

                val y = MotionEventCompat.getY(ev, pointerIndex)
                val overscrollTop = (y - mInitialMotionY) * DRAG_RATE
                mIsBeingDragged = false
                finishSpinner(overscrollTop)
                mActivePointerId = INVALID_POINTER
                return false
            }
            MotionEvent.ACTION_CANCEL -> return false
        }

        return true
    }

    fun canChildScrollUp(): Boolean {
        return ViewCompat.canScrollVertically(target, -1)
    }

    // 阻尼
    private var mTotalDragDistance = -1f
    private var mUsingCustomStart: Boolean = false
    // 下载刷新位置
    private var mSpinnerFinalOffset: Float = 0.toFloat()
    private var isPullOff = false // 是否 拉断（拉过可刷新的距离）

    private fun moveSpinner(overscrollTop: Float) {
        val originalDragPercent = overscrollTop / mTotalDragDistance

        val dragPercent = Math.min(1f, Math.abs(originalDragPercent))
        // 渐进式
        //        float adjustedPercent = (float) Math.max(dragPercent - .4, 0) * 5 / 3;
        val extraOS = Math.abs(overscrollTop) - mTotalDragDistance
        val slingshotDist = if (mUsingCustomStart)
            mSpinnerFinalOffset - mOriginalOffsetTop
        else
            mSpinnerFinalOffset
        val tensionSlingshotPercent = Math.max(0f, Math.min(extraOS, slingshotDist * 2) / slingshotDist)
        val tensionPercent = (tensionSlingshotPercent / 4 - Math.pow(
                (tensionSlingshotPercent / 4).toDouble(), 2.0)).toFloat() * 2f
        val extraMove = slingshotDist * tensionPercent * 2f

        val targetY = mOriginalOffsetTop + (slingshotDist * dragPercent + extraMove).toInt()
        // where 1.0f is a full circle
//        if (headerView?.visibility !== View.VISIBLE) {
            headerView?.visibility = View.VISIBLE
//        }
        if (overscrollTop < mTotalDragDistance && !isPullOff) {
//            headerView.setCurrentPlayTimeAtRatio(dragPercent)
        } else if (!isPullOff) {
            // 拉断
            isPullOff = true
//            mCircleView.startEyeAnimWithTwo()
        }
        setTargetOffsetTopAndBottom(targetY - currentTargetOffsetTop, true /* requires update */)
    }

    private fun finishSpinner(overscrollTop: Float) {
        if (overscrollTop > mTotalDragDistance) {
//            setRefreshing(true, true /* notify */)
        } else {
            // cancel refresh
            mRefreshing = false
//            animateOffsetToStartPosition(currentTargetOffsetTop, null)
        }
    }

    private fun getMotionEventY(ev: MotionEvent, activePointerId: Int): Float {
        val index = MotionEventCompat.findPointerIndex(ev, activePointerId)
        if (index < 0) {
            return -1f
        }
        return MotionEventCompat.getY(ev, index)
    }

    private fun setTargetOffsetTopAndBottom(offset: Int, requiresUpdate: Boolean) {
        headerView?.offsetTopAndBottom(offset)
//        target.invalidate()// fix 残影
        target?.offsetTopAndBottom(offset)
        currentTargetOffsetTop = headerView?.top ?: 0
        //        if (requiresUpdate)
        //            invalidate();
    }

    private fun onSecondaryPointerUp(ev: MotionEvent) {
        val pointerIndex = MotionEventCompat.getActionIndex(ev)
        val pointerId = MotionEventCompat.getPointerId(ev, pointerIndex)
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            val newPointerIndex = if (pointerIndex == 0) 1 else 0
            mActivePointerId = MotionEventCompat.getPointerId(ev, newPointerIndex)
        }
    }

    private fun ensureTarget() {
        if (target == null) {
            for (i in 0..childCount - 1) {
                val child = getChildAt(i)
                if (child != headerView) {
                    target = child
                    break
                }
            }
        }
    }
}