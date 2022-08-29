package com.teamforce.thanksapp.data.request

import com.google.gson.annotations.SerializedName

data class CreateContactRequest(
    @SerializedName("contact_id") val contactId: String,
    @SerializedName("contact_type") val contactType: String,
    @SerializedName("profile") val profile: String
)
