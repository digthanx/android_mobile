package com.teamforce.thanksapp.data.request

import com.google.gson.annotations.SerializedName

data class UpdateContactRequest(
    @SerializedName("contact_id") val  contactValue: String?,
)
