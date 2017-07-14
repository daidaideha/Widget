package com.lyl.widget

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.lyl.widget.libs.RunOperateButton
import org.jetbrains.anko.dip
import org.jetbrains.anko.find
import org.jetbrains.anko.setContentView
import org.jetbrains.anko.sp

class Main2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Main2UI().setContentView(this)

        val btn = find<RunOperateButton>(Main2UI().ID_BTN_CLICK)
        btn.setOperateText("update")

        val btn2 = find<RunOperateButton>(Main2UI().ID_BTN_PROGRESS)
        btn2.setOperateText("update2")
        btn2.setOperateTextSize(sp(13).toFloat())

        val btn3 = find<RunOperateButton>(Main2UI().ID_BTN_PROGRESS_STROKE)
        btn3.setOperateText("jump")
        btn3.setOperateTextSize(sp(10).toFloat())
        btn3.setStrokeWidth(dip(2).toFloat())
        btn3.setOperateProgressListener(object : RunOperateButton.OnProgressListener {
            override fun onProgress(view: RunOperateButton, progress: Float) {
                if (progress == view.getMaxCount()) {
                    startActivity(Intent(this@Main2Activity, MainActivity::class.java))
                }
            }
        })
        val list = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
        // region 总数操作符
//        println("any operate -> ${list.any { it % 8 == 0 }} and more than 10 is ${list.any { it > 10 }}")
//        println("all operate -> ${list.all { it % 8 == 0 }} and all more than 0 is ${list.all { it > 0 }}")
//        println("count operate -> ${list.count { it % 8 == 0 }}")
//        list.fold(5) { total, next ->
//            println("fold total is $total next is $next")
//            total + next
//        }
//        list.foldRight(4) { total, next ->
//            println("foldRight total is $total next is $next")
//            total + next
//        }
//        list.forEach { println(it) }
//        list.forEachIndexed { index, value -> println("position $index contains a $value") }
//        println("max operate -> ${list.max()}")
//        println("maxBy operate -> ${list.maxBy { -it }}")
//        println("min operate -> ${list.min()}")
//        println("minBy operate -> ${list.minBy { -it }}")
//        println("none operate -> ${list.none { it % 8 == 0 }}")
//        list.reduce { total, next ->
//            println("reduce total is $total next is $next")
//            total + next
//        }
//        list.reduceRight { total, next ->
//            println("reduceRight total is $total next is $next")
//            total + next
//        }
//        println("sumBy operate -> ${list.sumBy { it % 2 }}")
        // endregion
        // region 过滤操作符
//        list.drop(3).forEach { println("drop $it") }
//        list.dropWhile { it < 4 }.forEach { println("dropWhile $it") }
//        list.dropLast(3).forEach { println("dropLast $it") }
//        list.dropLastWhile { it > 6 }.forEach { println("dropLastWhile $it") }
//        list.filter { it < 4 }.forEach { println("filter $it") }
//        list.filterNot { it < 4 }.forEach { println("filterNot $it") }
//        list.filterNotNull().forEach { println("filterNotNull $it") }
//        list.slice(1..3).forEach { println("slice $it") }
//        list.take(3).forEach { println("take $it") }
//        list.takeLast(3).forEach { println("takeLast $it") }
//        list.takeWhile { it < 4 }.forEach { println("takeWhile $it") }
//        list.takeLastWhile { it > 6 }.forEach { println("takeLastWhile $it") }
        // endregion
        // region 映射操作符
//        list.flatMap { listOf(it, it + 1) }.forEach { println("flatMap $it") }
//        list.groupBy { if (it % 2 == 0) "even" else "odd" }.forEach { println("groupBy key is ${it.key} and value is ${it.value}") }
//        list.map { it * 2 }.forEach { println("map $it") }
//        list.mapIndexed { index, it -> index * it }.forEach { println("mapIndexed $it") }
//        list.mapNotNull { it * 2 }.forEach { println("mapNotNull $it") }
        // endregion

        list.partition { it % 2 == 0 }
        listOf(Pair(5, 7), Pair(6, 8)).unzip()

        val list2 = listOf(3, 2, 5, 7, 9, 10)
        list2.sortedBy { it - 3 == 0 }.forEach { println("sortedBy $it") }
    }

}
