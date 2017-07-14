package com.lyl.widget

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import org.jetbrains.anko.find
import org.jetbrains.anko.setContentView

class BaseRecyclerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        BaseRecycler2UI().setContentView(this)

        val recyclerView = find<RecyclerView>(BaseRecycler2UI().ID_RECYCLERVIEW)
        recyclerView.layoutManager = LinearLayoutManager(this)

    }
}