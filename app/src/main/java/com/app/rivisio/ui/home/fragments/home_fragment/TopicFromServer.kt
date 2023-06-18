package com.app.rivisio.ui.home.fragments.home_fragment

data class TopicFromServer(
    var id: Int? = null,
    var version: Int? = null,
    var name: String? = null,
    var status: String? = null,
    var userId: Int? = null,
    var imageUrls: ArrayList<String> = arrayListOf(),
    var notes: String? = null,
    var createdOn: String? = null,
    var modifiedOn: String? = null,
    var studiedOn: String? = null,
    var tagsList: ArrayList<TagFromServer> = arrayListOf(),
    var rev1Status: String? = null,
    var rev2Status: String? = null,
    var rev3Status: String? = null,
    var rev4Status: String? = null
)