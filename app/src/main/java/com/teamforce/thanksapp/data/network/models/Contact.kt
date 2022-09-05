package com.teamforce.thanksapp.data.network.models

import com.google.gson.annotations.SerializedName

data class Contact(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("contact_type")
    val contact_type: String,
    var contact_id: String
)
