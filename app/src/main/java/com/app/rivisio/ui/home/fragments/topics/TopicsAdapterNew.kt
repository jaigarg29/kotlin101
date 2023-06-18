package com.app.rivisio.ui.home.fragments.topics

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.app.rivisio.R
import com.app.rivisio.ui.home.fragments.home_fragment.TagFromServer
import com.app.rivisio.ui.home.fragments.home_fragment.TopicFromServer
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.shape.ShapeAppearanceModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TopicsAdapterNew(var topics: ArrayList<TopicFromServer> = arrayListOf()) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var topicsFiltered: ArrayList<TopicFromServer> = ArrayList()

    interface Callback {
        fun onTopicClick(topicFromServer: TopicFromServer)
        fun onTopicReviseButtonClick(topicFromServer: TopicFromServer)
    }

    private lateinit var callback: Callback

    fun setCallback(callback: Callback) {
        this.callback = callback
    }

    fun addItems(topic: ArrayList<TopicFromServer>) {
        this.topics = topic
        this.topicsFiltered = topic
        notifyDataSetChanged()
    }

    fun updateItems(topic: ArrayList<TopicFromServer>) {
        this.topicsFiltered = topic
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val topicViewHolder = TopicViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.topic_list_item_2, parent, false)
        )

        topicViewHolder.itemView.setOnClickListener {
            if (callback != null) {
                val position = topicViewHolder.bindingAdapterPosition
                callback.onTopicClick(topicsFiltered[position])
            }
        }

        return topicViewHolder
    }

    override fun getItemCount() = topicsFiltered.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as TopicViewHolder).onBind(topicsFiltered[position])
    }

    inner class TopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun onBind(topicFromServer: TopicFromServer) {
            itemView.findViewById<AppCompatTextView>(R.id.topic_name).text = topicFromServer.name

            itemView.findViewById<ChipGroup>(R.id.selected_tags).removeAllViews()

            if (topicFromServer.tagsList != null) {
                topicFromServer.tagsList!!.forEach {
                    addTagChip(it)
                }
            }

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            val dateTime = LocalDateTime.parse(topicFromServer.studiedOn, formatter)

            itemView.findViewById<AppCompatTextView>(R.id.started_on).text =
                "Started on : ${getFormattedDate(dateTime)}"
        }

        private fun getFormattedDate(dateTime: LocalDateTime): String {
            val formatter2 = DateTimeFormatter.ofPattern("dd/MM")
            return dateTime.format(formatter2)
        }

        private fun addTagChip(tag: TagFromServer) {
            itemView.findViewById<ChipGroup>(R.id.selected_tags).addView(getChip(tag))
        }

        private fun getChip(tag: TagFromServer): Chip {

            var hexColor = tag.hexCode!!.removePrefix("#")
            hexColor = "#1A$hexColor"

            return Chip(itemView.context).apply {
                text = tag.name
                shapeAppearanceModel =
                    ShapeAppearanceModel().withCornerSize(itemView.context.resources.getDimension(R.dimen.chip_corner_Radius))
                chipBackgroundColor =
                    ColorStateList.valueOf(Color.parseColor(hexColor))
                isCloseIconVisible = false
                chipStrokeWidth = 0f
            }
        }
    }

    fun filterList(query: String) {
        topicsFiltered = topics.filter {
            (it.name!!.uppercase().contains(query.uppercase())) or (
                    it.tagsList.stream()
                        .filter { t -> t.name!!.uppercase().contains(query.uppercase()) }
                        .findAny().isPresent
                    )
        } as ArrayList<TopicFromServer>

        notifyDataSetChanged()
    }
}