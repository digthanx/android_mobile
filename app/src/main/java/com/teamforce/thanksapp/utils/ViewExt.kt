package com.teamforce.thanksapp.utils

import android.graphics.Rect
import android.view.View
import androidx.core.widget.NestedScrollView

fun View.visible() = this.apply {
    visibility = View.VISIBLE
}

fun View.invisible() = this.apply {
    visibility = View.GONE
}

fun NestedScrollView.isViewVisible(view: View): Boolean {
    val scrollBounds = Rect()
    this.getDrawingRect(scrollBounds)
    val top = view.y
    val bottom = view.height + top
    return scrollBounds.bottom > bottom
}