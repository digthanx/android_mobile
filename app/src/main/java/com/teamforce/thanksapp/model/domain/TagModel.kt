package com.teamforce.thanksapp.model.domain

import com.google.gson.annotations.SerializedName

data class TagModel(
    @SerializedName("tag__id")
    val id: Int,
    @SerializedName("tag__name")
    val name: String,
    var enabled: Boolean = false
)
