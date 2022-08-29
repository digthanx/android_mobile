package com.teamforce.thanksapp.data.request

import com.google.gson.annotations.SerializedName

data class UpdateContactRequest(
    @SerializedName("contact") val  tgName: String?,
    @SerializedName("surname") val  surname: String?,
)
