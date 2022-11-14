package com.teamforce.thanksapp.presentation.adapter.decorators

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class VerticalDividerDecoratorForListWithBottomBar(
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
            bottom = divider
        }

        if(parent.getChildLayoutPosition(view) == 0){
            outRect.top = 20
        }

        // Для последнего элемента устанавливаем отдельный отступ
        if (parent.getChildLayoutPosition(view) == parent.adapter?.itemCount?.minus(1)) {
            outRect.bottom = 200
        }
    }
}
