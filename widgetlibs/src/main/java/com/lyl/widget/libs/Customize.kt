@file:Suppress("NOTHING_TO_INLINE")

package com.lyl.widget.libs

import android.view.ViewManager
import org.jetbrains.anko.custom.ankoView

/**
 * create lyl on 2017/7/11
 * </p>
 */
public inline fun ViewManager.OperateButton() = OperateButton {}

public inline fun ViewManager.OperateButton(init: RunOperateButton.() -> Unit): RunOperateButton {
    return ankoView({ RunOperateButton(it) }, init)
}