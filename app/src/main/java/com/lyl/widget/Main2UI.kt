package com.lyl.widget

import android.graphics.Color
import android.view.View
import com.lyl.widget.libs.OperateButton
import com.lyl.widget.libs.RunOperateButton
import org.jetbrains.anko.*

/**
 * create lyl on 2017/7/11
 * </p>
 */
open class Main2UI : AnkoComponent<Main2Activity> {

    val ID_BTN_CLICK = 0x00000001
    val ID_BTN_PROGRESS = 0x00000002
    val ID_BTN_PROGRESS_STROKE = 0x00000003

    override fun createView(ui: AnkoContext<Main2Activity>): View {
        return with(ui) {
            verticalLayout {
                backgroundColor = 0xFF373840.toInt()
                val name = editText()
                OperateButton {
                    id = ID_BTN_CLICK
                    setOperateTextSize(sp(15).toFloat())
                    setOperateBackgroundColor(0xFFFFFFFF.toInt())
                    setOperateEmptyColor(0x33FFFFFF)
                    setOperateStrokeColor(0xFF373840.toInt())
                    setOperateStyle(STYLE_NONE)
                    setOperateText("点击")
                    setOperateTextColor(0xFF333333.toInt())
                    setOffsetMax(dip(5).toFloat())
                    setStrokeWidth(dip(5).toFloat())
                    setMaxCount(60.toFloat())
                    backgroundColor = Color.TRANSPARENT
//                    setOperateClickListener(object : RunOperateButton.OnClickListener {
//
//                        override fun onOperateClick(view: View) {
//                            toast("Hello, ${name.text}!")
//                        }
//                    })
                }.lparams(width = dip(100), height = dip(100))

                OperateButton {
                    id = ID_BTN_PROGRESS
                    setOperateTextSize(sp(15).toFloat())
                    setOperateBackgroundColor(0xFFFFFFFF.toInt())
                    setOperateEmptyColor(0x33FFFFFF)
                    setOperateStrokeColor(0xFF373840.toInt())
                    setOperateStyle(STYLE_PROGRESS)
                    setOperateText("进度1")
                    setOperateTextColor(0xFF333333.toInt())
                    setOffsetMax(dip(5).toFloat())
                    setStrokeWidth(dip(5).toFloat())
                    setMaxCount(60.toFloat())
                    backgroundColor = Color.TRANSPARENT
                    setOperateProgressListener(object : RunOperateButton.OnProgressListener {
                        override fun onProgress(view: RunOperateButton, progress: Float) {
                            println("current progress is $progress")
                        }

                    })
                }.lparams(width = dip(80), height = dip(80))

                OperateButton {
                    id = ID_BTN_PROGRESS_STROKE
                    setOperateTextSize(sp(15).toFloat())
                    setOperateBackgroundColor(0xFFFFFFFF.toInt())
                    setOperateEmptyColor(0x33FFFFFF)
                    setOperateStrokeColor(0xFF373840.toInt())
                    setOperateStyle(STYLE_PROGRESS_STROKE)
                    setOperateText("进度2")
                    setOperateTextColor(0xFF333333.toInt())
                    setOffsetMax(dip(5).toFloat())
                    setStrokeWidth(dip(5).toFloat())
                    setMaxCount(60.toFloat())
                    backgroundColor = Color.TRANSPARENT
                    setOperateProgressListener(object : RunOperateButton.OnProgressListener {
                        override fun onProgress(view: RunOperateButton, progress: Float) {
                            println("current progress is $progress")
                        }

                    })
                }.lparams(width = dip(60), height = dip(60))
                button("Say Hello") {
                    onClick { toast("Hello, ${name.text}!") }
                }
            }
        }
    }
}