package com.app.rivisio.ui.topic_details

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.widget.AppCompatTextView
import com.app.rivisio.R
import com.google.android.material.bottomsheet.BottomSheetDialog


object BottomSheetDialogs {

    fun showTextNote(context: Context, title: String, body: String) {
        val textNoteView =
            LayoutInflater.from(context).inflate(R.layout.text_note_dialog_layout, null)

        textNoteView.findViewById<AppCompatTextView>(R.id.title).text = title
        textNoteView.findViewById<AppCompatTextView>(R.id.body).text = body

        val dialog = BottomSheetDialog(context)
        dialog.setContentView(textNoteView)
        dialog.setCancelable(true)
        dialog.show()

    }
}