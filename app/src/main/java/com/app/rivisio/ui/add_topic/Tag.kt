package com.app.rivisio.ui.add_topic

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Tag(
    val id: Int,
    val name: String,
    val hexCode: String
) : Parcelable {
    override fun toString(): String {
        return name
    }
}

