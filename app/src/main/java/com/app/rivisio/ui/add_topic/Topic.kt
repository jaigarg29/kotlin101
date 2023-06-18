package com.app.rivisio.ui.add_topic

import org.json.JSONArray

data class Topic(
    var imageUrls: ArrayList<String>,
    var name: String,
    var notes: String,
    var studiedOnStr: String,
    var tags: ArrayList<Int>
)
