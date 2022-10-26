package com.teamforce.thanksapp.presentation.adapter.decorators

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class VerticalDividerItemDecorator(
    private val divider: Int,
    private val size: Int
): RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)

        with(outRect) {
            top = divider
            bottom = divider
            right = divider
            left = divider
        }
        // Для первого элемента устанавливаем отдельный отступ
        if(parent.getChildLayoutPosition(view) == 0){
            outRect.top = 20
        }
        // Для последнего элемента устанавливаем отдельный отступ
        if(parent.getChildLayoutPosition(view) == size - 1){
            outRect.bottom = 30
        }
    }


}