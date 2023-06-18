package com.app.rivisio.ui.add_notes

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.app.rivisio.R

class TextNoteOptionsAdapter(private val list: ArrayList<String>) : BaseAdapter() {

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val layoutInflater = LayoutInflater.from(parent?.context)
        val view = layoutInflater.inflate(R.layout.popup_list_item, parent, false)
        if (list[position] == "Edit")
            view.findViewById<AppCompatImageView>(R.id.image).setImageResource(R.drawable.pencil)
        else
            view.findViewById<AppCompatImageView>(R.id.image).setImageResource(R.drawable.delete)
        view.findViewById<AppCompatTextView>(R.id.text).text = list[position]

        return view
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }

}