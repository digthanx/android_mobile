package com.teamforce.thanksapp.utils

import android.view.View

fun View.visible() = this.apply {
    visibility = View.VISIBLE
}

fun View.invisible() = this.apply {
    visibility = View.GONE
}