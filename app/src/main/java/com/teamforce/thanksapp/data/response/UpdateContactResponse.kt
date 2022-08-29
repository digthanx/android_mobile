package com.teamforce.thanksapp.data.response

import com.google.gson.annotations.SerializedName


data class UpdateContactResponse(
    val data: String,
    @SerializedName("non_field_errors") val error: String
)