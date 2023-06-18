package com.app.rivisio.ui.home.fragments.home_fragment

data class TagFromServer(
    var id: Int? = null,
    var version: Int? = null,
    var name: String? = null,
    var hexCode: String? = null,
    var userId: Int? = null,
    var createdOn: String? = null,
    var modifiedOn: String? = null
)
