package com.teamforce.thanksapp.data.response

import com.google.gson.annotations.SerializedName

data class UpdateContactResponseError(
    @SerializedName("non_field_errors") val data: String
)
