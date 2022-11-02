package com.teamforce.thanksapp.data.entities.feed


import com.google.gson.annotations.SerializedName

data class Tag(
    @SerializedName("name")
    val name: String,
    @SerializedName("tag_id")
    val tagId: Int
)