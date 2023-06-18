package com.app.rivisio.ui.add_notes

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import com.app.rivisio.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import es.dmoral.toasty.Toasty

class CreateImageGroupBottomSheetDialog : BottomSheetDialogFragment() {

    private var callback: Callback? = null

    interface Callback {
        fun onImageGroupCreated(imageGroupName: String)
    }

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.create_image_group_bottom_sheet_content, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<AppCompatButton>(R.id.add_image_group_button).setOnClickListener {
            if (TextUtils.isEmpty(view.findViewById<AppCompatEditText>(R.id.image_group_field).text)) {
                return@setOnClickListener
            }

            callback?.onImageGroupCreated(
                view.findViewById<AppCompatEditText>(R.id.image_group_field).text.toString(),
            )

            dismiss()

        }
    }

    companion object {
        const val TAG = "ModalBottomSheet"
    }
}