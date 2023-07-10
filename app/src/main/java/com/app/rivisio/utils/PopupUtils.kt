package com.app.rivisio.utils

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.BaseAdapter
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.content.ContextCompat
import com.app.rivisio.R
import com.app.rivisio.ui.topic_details.TopicDetailsActivity

fun getPopupMenu(
    context: Context,
    anchor: View,
    adapter: BaseAdapter,
    clickListener: AdapterView.OnItemClickListener,
    verticalOffset: Int,
    horizontalOffset: Int
): ListPopupWindow {

    val listPopupWindow = ListPopupWindow(context)
    listPopupWindow.anchorView = anchor

    listPopupWindow.setDropDownGravity(Gravity.END)

    listPopupWindow.width = context.resources.getDimension(R.dimen.popup_width).toInt()
    listPopupWindow.height = ListPopupWindow.WRAP_CONTENT

    listPopupWindow.verticalOffset = verticalOffset
    listPopupWindow.horizontalOffset = horizontalOffset

    listPopupWindow.isModal = true

    listPopupWindow.setAdapter(adapter)
    listPopupWindow.setBackgroundDrawable(
        ContextCompat.getDrawable(context, R.drawable.bg_popup_menu_2)
    )

    listPopupWindow.setOnItemClickListener(clickListener)

    return listPopupWindow
}



fun getPopupMenuTopic(
    context: Context,
    anchor: View,
    adapter: BaseAdapter,
    clickListener: AdapterView.OnItemClickListener,
    verticalOffset: Int,
    horizontalOffset: Int
): ListPopupWindow {

    val listPopupWindow = ListPopupWindow(context)
    listPopupWindow.anchorView = anchor

    listPopupWindow.setDropDownGravity(Gravity.END)

    listPopupWindow.width = context.resources.getDimension(R.dimen.popup_width).toInt()
    listPopupWindow.height = ListPopupWindow.WRAP_CONTENT

    listPopupWindow.verticalOffset = verticalOffset
    listPopupWindow.horizontalOffset = horizontalOffset

    listPopupWindow.isModal = true

    listPopupWindow.setAdapter(adapter)
    listPopupWindow.setBackgroundDrawable(
        ContextCompat.getDrawable(context, R.drawable.bg_popup_menu_2)
    )

    listPopupWindow.setOnItemClickListener(clickListener)

    return listPopupWindow
}