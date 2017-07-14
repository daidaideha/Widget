package com.lyl.widget

import android.support.v7.widget.RecyclerView
import android.view.View
import org.jetbrains.anko.AnkoComponent
import org.jetbrains.anko.AnkoContext
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.verticalLayout

/**
 * create lyl on 2017/7/12
 * </p>
 */
open class BaseRecycler2UI : AnkoComponent<BaseRecyclerActivity> {

    val ID_RECYCLERVIEW = 0x00000001

    override fun createView(ui: AnkoContext<BaseRecyclerActivity>): View {
        return with(ui) {
            verticalLayout {
                recyclerView {
                    id = ID_RECYCLERVIEW
                }
            }
        }
    }
}